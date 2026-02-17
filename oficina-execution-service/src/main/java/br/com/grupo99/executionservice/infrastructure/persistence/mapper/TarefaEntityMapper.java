package br.com.grupo99.executionservice.infrastructure.persistence.mapper;

import br.com.grupo99.executionservice.domain.model.Tarefa;
import br.com.grupo99.executionservice.infrastructure.persistence.entity.TarefaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Tarefa (domínio) e TarefaEntity (persistência).
 */
@Component
public class TarefaEntityMapper {

    /**
     * Converte TarefaEntity (persistência) → Tarefa (domínio).
     *
     * @param entity entidade JPA
     * @return modelo de domínio
     */
    public Tarefa toDomain(TarefaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Tarefa.builder()
                .id(entity.getId())
                .descricao(entity.getDescricao())
                .mecanico(entity.getMecanico())
                .tempoEstimadoMinutos(entity.getTempoEstimadoMinutos())
                .tempoRealMinutos(entity.getTempoRealMinutos())
                .status(entity.getStatus())
                .dataInicio(entity.getDataInicio())
                .dataFinalizacao(entity.getDataFinalizacao())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converte Tarefa (domínio) → TarefaEntity (persistência).
     *
     * @param domain modelo de domínio
     * @return entidade JPA
     */
    public TarefaEntity toEntity(Tarefa domain) {
        if (domain == null) {
            return null;
        }

        return TarefaEntity.builder()
                .id(domain.getId())
                .descricao(domain.getDescricao())
                .mecanico(domain.getMecanico())
                .tempoEstimadoMinutos(domain.getTempoEstimadoMinutos())
                .tempoRealMinutos(domain.getTempoRealMinutos())
                .status(domain.getStatus())
                .dataInicio(domain.getDataInicio())
                .dataFinalizacao(domain.getDataFinalizacao())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
