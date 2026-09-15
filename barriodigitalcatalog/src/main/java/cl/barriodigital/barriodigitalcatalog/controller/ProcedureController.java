package cl.barriodigital.barriodigitalcatalog.controller;

import cl.barriodigital.barriodigitalcatalog.dto.CreateProcedureRequest;
import cl.barriodigital.barriodigitalcatalog.dto.ProcedureResponse;
import cl.barriodigital.barriodigitalcatalog.dto.UpdateProcedureRequest;
import cl.barriodigital.barriodigitalcatalog.service.ProcedureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/procedures")
public class ProcedureController {

    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @GetMapping
    public ResponseEntity<List<ProcedureResponse>> getAllProcedures() {
        return ResponseEntity.ok(procedureService.getAllProcedures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcedureResponse> getProcedureById(@PathVariable Long id) {
        return ResponseEntity.ok(procedureService.getProcedureById(id));
    }

    @PostMapping
    public ResponseEntity<ProcedureResponse> createProcedure(@Valid @RequestBody CreateProcedureRequest request) {
        ProcedureResponse response = procedureService.createProcedure(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcedureResponse> updateProcedure(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProcedureRequest request
    ) {
        return ResponseEntity.ok(procedureService.updateProcedure(id, request));
    }
}
