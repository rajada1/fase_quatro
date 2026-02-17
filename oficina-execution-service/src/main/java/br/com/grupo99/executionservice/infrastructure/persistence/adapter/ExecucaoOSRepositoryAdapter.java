package br.com.grupo99.executionservice.infrastructure.persistence.adapter;

import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import br.com.grupo99.executionservice.domain.repository.ExecucaoOSRepository;
import br.com.grupo99.executionservice.infrastructure.persistence.entity.ExecucaoOSEntity;
import br.com.grupo99.executionservice.infrastructure.persistence.mapper.ExecucaoOSEntityMapper;
import br.com.grupo99.executionservice.infrastructure.persistence.repository.PostgresExecucaoOSRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter Repository que implementa a interface de domínio
 * ExecucaoOSRepository.
 * 
 * Padrão Adapter: Traduz chamadas do domínio para operações PostgreSQL,
 * mantendo a separação entre domínio e infraestrutura.
 * 
 * Responsabilidades:
 * - Converter ExecucaoOS (domínio) ↔ ExecucaoOSEntity (persistência)
 * - Delegar operações SQL para PostgresExecucaoOSRepository
 * - Manter o domínio independente de tecnologia de persistência
 */
@Repository
public class ExecucaoOSRepositoryAdapter implements ExecucaoOSRepository {

    private final PostgresExecucaoOSRepository postgresRepository;
    private final ExecucaoOSEntityMapper mapper;

    public ExecucaoOSRepositoryAdapter(
            PostgresExecucaoOSRepository postgresRepository,
            ExecucaoOSEntityMapper mapper) {
        this.postgresRepository = postgresRepository;
        this.mapper = mapper;
    }

    @Override
    @SuppressWarnings("null")
    public ExecucaoOS save(ExecucaoOS execucaoOS) {
        ExecucaoOSEntity entity = mapper.toEntity(execucaoOS);
        ExecucaoOSEntity saved = postgresRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @SuppressWarnings("null")
    public Optional<ExecucaoOS> findById(UUID id) {
        return postgresRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ExecucaoOS> findByOsId(UUID osId) {
        return postgresRepository.findByOsId(osId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ExecucaoOS> findByStatus(StatusExecucao status) {
        return postgresRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExecucaoOS> findByMecanico(String mecanico) {
        return postgresRepository.findByMecanico(mecanico).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExecucaoOS> findAll() {
        return postgresRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public void deleteById(UUID id) {
        postgresRepository.deleteById(id);
    }

    @Override
    public boolean existsByOsId(UUID osId) {
        return postgresRepository.existsByOsId(osId);
    }

    @Override
    @SuppressWarnings("null")
    public boolean existsById(UUID id) {
        return postgresRepository.existsById(id);
    }
}
