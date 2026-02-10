package br.com.grupo99.billingservice.infrastructure.persistence.adapter;

import br.com.grupo99.billingservice.domain.model.Pagamento;
import br.com.grupo99.billingservice.domain.model.StatusPagamento;
import br.com.grupo99.billingservice.domain.repository.PagamentoRepository;
import br.com.grupo99.billingservice.infrastructure.persistence.repository.MongoPagamentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter Repository implementando PagamentoRepository (domain interface)
 * 
 * ✅ CLEAN ARCHITECTURE - ADAPTER PATTERN:
 * - Implementa a interface de domínio
 * - Adapta MongoDB para domínio
 * - Domain não conhece detalhes de persistência
 */
@Slf4j
@Component
public class PagamentoRepositoryAdapter implements PagamentoRepository {

    private final MongoPagamentoRepository mongoRepository;
    private final PagamentoEntityMapper mapper;

    public PagamentoRepositoryAdapter(
            MongoPagamentoRepository mongoRepository,
            PagamentoEntityMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    /**
     * Salva um Pagamento
     */
    @Override
    public Pagamento save(Pagamento pagamento) {
        log.debug("Salvando pagamento: {}", pagamento.getId());

        // Domain → Entity → MongoDB → Entity → Domain
        var entity = mapper.toEntity(pagamento);
        var saved = mongoRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * Busca Pagamento por ID
     */
    @Override
    public Optional<Pagamento> findById(UUID id) {
        log.debug("Buscando pagamento por id: {}", id);
        var entity = mongoRepository.findById(id.toString());
        return entity.map(mapper::toDomain);
    }

    /**
     * Busca Pagamento por Orcamento ID
     */
    @Override
    public Optional<Pagamento> findByOrcamentoId(UUID orcamentoId) {
        log.debug("Buscando pagamento por orcamentoId: {}", orcamentoId);
        var entity = mongoRepository.findByOrcamentoId(orcamentoId.toString());
        return entity.map(mapper::toDomain);
    }

    /**
     * Busca Pagamentos por OS ID
     */
    @Override
    public List<Pagamento> findByOsId(UUID osId) {
        log.debug("Buscando pagamentos por osId: {}", osId);
        var entities = mongoRepository.findByOsId(osId.toString());
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Busca Pagamentos por Status
     */
    @Override
    public List<Pagamento> findByStatus(StatusPagamento status) {
        log.debug("Buscando pagamentos por status: {}", status);
        var entities = mongoRepository.findByStatus(status.name());
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Verifica se existe Pagamento com status específico
     */
    @Override
    public boolean existsByOrcamentoIdAndStatus(UUID orcamentoId, StatusPagamento status) {
        log.debug("Verificando existência de pagamento para orcamentoId: {} e status: {}", orcamentoId, status);
        return mongoRepository.existsByOrcamentoIdAndStatus(orcamentoId.toString(), status.name());
    }

    /**
     * Deleta um Pagamento
     */
    @Override
    public void deleteById(UUID id) {
        log.debug("Deletando pagamento: {}", id);
        mongoRepository.deleteById(id.toString());
    }
}
