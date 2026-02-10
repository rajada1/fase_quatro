package br.com.grupo99.executionservice.infrastructure.persistence.mapper;

import br.com.grupo99.executionservice.domain.model.UsoPeca;
import br.com.grupo99.executionservice.infrastructure.persistence.entity.UsoPecaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre UsoPeca (domínio) e UsoPecaEntity (persistência).
 */
@Component
public class UsoPecaEntityMapper {

    /**
     * Converte UsoPecaEntity (persistência) → UsoPeca (domínio).
     *
     * @param entity entidade JPA
     * @return modelo de domínio
     */
    public UsoPeca toDomain(UsoPecaEntity entity) {
        if (entity == null) {
            return null;
        }

        return UsoPeca.builder()
                .id(entity.getId())
                .pecaId(entity.getPecaId())
                .descricao(entity.getDescricao())
                .quantidade(entity.getQuantidade())
                .valorUnitario(entity.getValorUnitario())
                .valorTotal(entity.getValorTotal())
                .dataUso(entity.getDataUso())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converte UsoPeca (domínio) → UsoPecaEntity (persistência).
     *
     * @param domain modelo de domínio
     * @return entidade JPA
     */
    public UsoPecaEntity toEntity(UsoPeca domain) {
        if (domain == null) {
            return null;
        }

        return UsoPecaEntity.builder()
                .id(domain.getId())
                .pecaId(domain.getPecaId())
                .descricao(domain.getDescricao())
                .quantidade(domain.getQuantidade())
                .valorUnitario(domain.getValorUnitario())
                .valorTotal(domain.getValorTotal())
                .dataUso(domain.getDataUso())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
