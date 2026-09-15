package cl.barriodigital.barriodigitalcatalog.repository;

import cl.barriodigital.barriodigitalcatalog.model.ProcedureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcedureRepository extends JpaRepository<ProcedureEntity, Long> {

    List<ProcedureEntity> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    Optional<ProcedureEntity> findByNameIgnoreCase(String name);
}
