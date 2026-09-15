package cl.barriodigital.barriodigitalcatalog;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.barriodigital.barriodigitalcatalog.repository.ProcedureRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalogdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc
class BarriodigitalcatalogApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProcedureRepository procedureRepository;

    @BeforeEach
    void setUp() {
        procedureRepository.deleteAll();
    }

    @Test
    void getEmptyProceduresReturnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/catalog/procedures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createProcedureReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/catalog/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Permiso de obra menor",
                                "requirements", List.of("Cedula de identidad"),
                                "dailyQuota", 10,
                                "available", true
                        ))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Permiso de obra menor"))
                .andExpect(jsonPath("$.dailyQuota").value(10))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createProcedureDefaultsAvailableToTrueWhenOmitted() throws Exception {
        mockMvc.perform(post("/api/catalog/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Certificado de residencia",
                                "requirements", List.of(),
                                "dailyQuota", 5
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requirements", hasSize(0)));
    }

    @Test
    void postWithoutNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/catalog/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "requirements", List.of("Documento"),
                                "dailyQuota", 1,
                                "available", true
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void negativeDailyQuotaReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/catalog/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Solicitud con cupo invalido",
                                "requirements", List.of(),
                                "dailyQuota", -1,
                                "available", true
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getExistingProcedureReturnsProcedure() throws Exception {
        Long id = createProcedure("Licencia temporal", List.of("Formulario", "Cedula"));

        mockMvc.perform(get("/api/catalog/procedures/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Licencia temporal"));
    }

    @Test
    void getMissingProcedureReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/catalog/procedures/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateProcedureReturnsUpdatedData() throws Exception {
        Long id = createProcedure("Autorizacion inicial", List.of("Documento inicial"));

        mockMvc.perform(put("/api/catalog/procedures/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Autorizacion actualizada",
                                "requirements", List.of("Documento actualizado"),
                                "dailyQuota", 20,
                                "available", false
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Autorizacion actualizada"))
                .andExpect(jsonPath("$.requirements[0]").value("Documento actualizado"))
                .andExpect(jsonPath("$.dailyQuota").value(20))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void updateMissingProcedureReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/catalog/procedures/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "No existe",
                                "requirements", List.of(),
                                "dailyQuota", 3,
                                "available", true
                        ))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void duplicateNameReturnsConflict() throws Exception {
        createProcedure("Nombre duplicado", List.of());

        mockMvc.perform(post("/api/catalog/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "nombre duplicado",
                                "requirements", List.of(),
                                "dailyQuota", 10,
                                "available", true
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void requirementsArePersistedAndReturned() throws Exception {
        Long id = createProcedure("Tramite con requisitos", List.of("Documento A", "Documento B"));

        mockMvc.perform(get("/api/catalog/procedures/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requirements", hasSize(2)))
                .andExpect(jsonPath("$.requirements[0]").value("Documento A"))
                .andExpect(jsonPath("$.requirements[1]").value("Documento B"));
    }

    private Long createProcedure(String name, List<String> requirements) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/catalog/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", name,
                                "requirements", requirements,
                                "dailyQuota", 10,
                                "available", true
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
