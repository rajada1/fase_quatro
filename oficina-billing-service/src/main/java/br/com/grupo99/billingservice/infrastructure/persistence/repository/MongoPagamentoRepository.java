package br.com.grupo99.billingservice.infrastructure.persistence.repository;

import br.com.grupo99.billingservice.infrastructure.persistence.entity.PagamentoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB Repository
 * 
 * ✅ CLEAN ARCHITECTURE: Repository específico do MongoDB fica na infrastructure
 */
@Repository
public interface MongoPagamentoRepository extends MongoRepository<PagamentoEntity, String> {

    Optional<PagamentoEntity> findByOrcamentoId(String orcamentoId);

    List<PagamentoEntity> findByOsId(String osId);

    List<PagamentoEntity> findByStatus(String status);

    boolean existsByOrcamentoIdAndStatus(String orcamentoId, String status);
}
