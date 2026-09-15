package cl.barriodigital.barriodigitalcatalog.service;

import cl.barriodigital.barriodigitalcatalog.dto.CreateProcedureRequest;
import cl.barriodigital.barriodigitalcatalog.dto.ProcedureResponse;
import cl.barriodigital.barriodigitalcatalog.dto.UpdateProcedureRequest;
import cl.barriodigital.barriodigitalcatalog.exception.ProcedureAlreadyExistsException;
import cl.barriodigital.barriodigitalcatalog.exception.ProcedureNotFoundException;
import cl.barriodigital.barriodigitalcatalog.model.ProcedureEntity;
import cl.barriodigital.barriodigitalcatalog.repository.ProcedureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final ProcedureMapper procedureMapper;

    public ProcedureService(ProcedureRepository procedureRepository, ProcedureMapper procedureMapper) {
        this.procedureRepository = procedureRepository;
        this.procedureMapper = procedureMapper;
    }

    @Transactional(readOnly = true)
    public List<ProcedureResponse> getAllProcedures() {
        return procedureRepository.findAllByOrderByNameAsc()
                .stream()
                .map(procedureMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProcedureResponse getProcedureById(Long id) {
        return procedureMapper.toResponse(findProcedure(id));
    }

    @Transactional
    public ProcedureResponse createProcedure(CreateProcedureRequest request) {
        String normalizedName = normalizeName(request.name());
        if (procedureRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ProcedureAlreadyExistsException(normalizedName);
        }

        ProcedureEntity procedure = new ProcedureEntity();
        procedure.setName(normalizedName);
        procedure.setRequirements(normalizeRequirements(request.requirements()));
        procedure.setDailyQuota(request.dailyQuota());
        procedure.setAvailable(request.available() == null ? true : request.available());

        return procedureMapper.toResponse(procedureRepository.save(procedure));
    }

    @Transactional
    public ProcedureResponse updateProcedure(Long id, UpdateProcedureRequest request) {
        ProcedureEntity procedure = findProcedure(id);
        String normalizedName = normalizeName(request.name());

        Optional<ProcedureEntity> procedureWithSameName = procedureRepository.findByNameIgnoreCase(normalizedName);
        if (procedureWithSameName.isPresent() && !procedureWithSameName.get().getId().equals(id)) {
            throw new ProcedureAlreadyExistsException(normalizedName);
        }

        procedure.setName(normalizedName);
        procedure.setRequirements(normalizeRequirements(request.requirements()));
        procedure.setDailyQuota(request.dailyQuota());
        procedure.setAvailable(request.available());

        return procedureMapper.toResponse(procedureRepository.save(procedure));
    }

    private ProcedureEntity findProcedure(Long id) {
        return procedureRepository.findById(id)
                .orElseThrow(() -> new ProcedureNotFoundException(id));
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private List<String> normalizeRequirements(List<String> requirements) {
        if (requirements == null) {
            return List.of();
        }

        List<String> normalized = new ArrayList<>();
        for (String requirement : requirements) {
            normalized.add(requirement.trim());
        }
        return normalized;
    }
}
