package br.com.grupo99.billingservice.infrastructure.persistence.repository;

import br.com.grupo99.billingservice.infrastructure.persistence.entity.OrcamentoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB Repository
 * 
 * ✅ CLEAN ARCHITECTURE: Repository específico do MongoDB fica na infrastructure
 * Este é um detalhe técnico, não faz parte da interface de domínio
 */
@Repository
public interface MongoOrcamentoRepository extends MongoRepository<OrcamentoEntity, String> {

    Optional<OrcamentoEntity> findByOsId(String osId);

    List<OrcamentoEntity> findByStatus(String status);

    boolean existsByOsId(String osId);
}
