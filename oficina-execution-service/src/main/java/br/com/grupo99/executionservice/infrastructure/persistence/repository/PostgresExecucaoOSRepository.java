package br.com.grupo99.executionservice.infrastructure.persistence.repository;

import br.com.grupo99.executionservice.infrastructure.persistence.entity.ExecucaoOSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository para ExecucaoOSEntity.
 * Responsável apenas por operações de persistência com PostgreSQL.
 */
@Repository
public interface PostgresExecucaoOSRepository extends JpaRepository<ExecucaoOSEntity, UUID> {

    Optional<ExecucaoOSEntity> findByOsId(UUID osId);

    List<ExecucaoOSEntity> findByStatus(String status);

    List<ExecucaoOSEntity> findByMecanico(String mecanico);

    boolean existsByOsId(UUID osId);
}
