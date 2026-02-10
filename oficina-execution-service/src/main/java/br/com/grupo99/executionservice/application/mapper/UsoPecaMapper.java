package br.com.grupo99.executionservice.application.mapper;

import br.com.grupo99.executionservice.application.dto.UsoPecaRequestDTO;
import br.com.grupo99.executionservice.application.dto.UsoPecaResponseDTO;
import br.com.grupo99.executionservice.domain.model.UsoPeca;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversões entre UsoPeca de domínio e DTOs.
 */
@Component
public class UsoPecaMapper {

    /**
     * Converte um DTO de requisição para um UsoPeca de domínio.
     *
     * @param request DTO de requisição
     * @return UsoPeca de domínio
     */
    public UsoPeca toDomain(UsoPecaRequestDTO request) {
        if (request == null) {
            return null;
        }

        return UsoPeca.criar(
                request.pecaId(),
                request.descricao(),
                request.quantidade(),
                request.valorUnitario());
    }

    /**
     * Converte um UsoPeca de domínio para um DTO de resposta.
     *
     * @param domain UsoPeca de domínio
     * @return DTO de resposta
     */
    public UsoPecaResponseDTO toResponseDTO(UsoPeca domain) {
        if (domain == null) {
            return null;
        }

        return UsoPecaResponseDTO.fromDomain(domain);
    }
}
