package br.com.grupo99.executionservice.infrastructure.persistence.mapper;

import br.com.grupo99.executionservice.domain.model.Diagnostico;
import br.com.grupo99.executionservice.infrastructure.persistence.entity.DiagnosticoEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Diagnostico (domínio) e DiagnosticoEntity
 * (persistência).
 */
@Component
public class DiagnosticoEntityMapper {

    /**
     * Converte DiagnosticoEntity (persistência) → Diagnostico (domínio).
     *
     * @param entity entidade JPA
     * @return modelo de domínio
     */
    public Diagnostico toDomain(DiagnosticoEntity entity) {
        if (entity == null) {
            return null;
        }

        return Diagnostico.builder()
                .id(entity.getId())
                .descricao(entity.getDescricao())
                .mecanico(entity.getMecanico())
                .dataDiagnostico(entity.getDataDiagnostico())
                .observacoes(entity.getObservacoes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converte Diagnostico (domínio) → DiagnosticoEntity (persistência).
     *
     * @param domain modelo de domínio
     * @return entidade JPA
     */
    public DiagnosticoEntity toEntity(Diagnostico domain) {
        if (domain == null) {
            return null;
        }

        return DiagnosticoEntity.builder()
                .id(domain.getId())
                .descricao(domain.getDescricao())
                .mecanico(domain.getMecanico())
                .dataDiagnostico(domain.getDataDiagnostico())
                .observacoes(domain.getObservacoes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
