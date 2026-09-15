package cl.barriodigital.barriodigitalcatalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateProcedureRequest(
        @NotBlank(message = "El nombre del tipo de tramite es obligatorio.")
        @Size(max = 160, message = "El nombre no puede superar 160 caracteres.")
        String name,

        List<@NotBlank(message = "Los requisitos no pueden estar vacios.")
                @Size(max = 500, message = "Cada requisito no puede superar 500 caracteres.") String> requirements,

        @NotNull(message = "El cupo diario es obligatorio.")
        @Min(value = 0, message = "El cupo diario debe ser mayor o igual a 0.")
        Integer dailyQuota,

        Boolean available
) {
}
