package cl.barriodigital.barriodigitalcatalog.service;

import cl.barriodigital.barriodigitalcatalog.dto.ProcedureResponse;
import cl.barriodigital.barriodigitalcatalog.model.ProcedureEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcedureMapper {

    public ProcedureResponse toResponse(ProcedureEntity procedure) {
        return new ProcedureResponse(
                procedure.getId(),
                procedure.getName(),
                List.copyOf(procedure.getRequirements()),
                procedure.getDailyQuota(),
                procedure.getAvailable(),
                procedure.getCreatedAt(),
                procedure.getUpdatedAt()
        );
    }
}
