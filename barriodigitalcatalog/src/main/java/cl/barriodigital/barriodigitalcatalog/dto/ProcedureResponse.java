package cl.barriodigital.barriodigitalcatalog.dto;

import java.time.Instant;
import java.util.List;

public record ProcedureResponse(
        Long id,
        String name,
        List<String> requirements,
        Integer dailyQuota,
        Boolean available,
        Instant createdAt,
        Instant updatedAt
) {
}
