package br.com.grupo99.executionservice.application.mapper;

import br.com.grupo99.executionservice.application.dto.DiagnosticoRequestDTO;
import br.com.grupo99.executionservice.application.dto.DiagnosticoResponseDTO;
import br.com.grupo99.executionservice.domain.model.Diagnostico;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversões entre Diagnostico de domínio e DTOs.
 */
@Component
public class DiagnosticoMapper {

    /**
     * Converte um DTO de requisição para um Diagnostico de domínio.
     *
     * @param request DTO de requisição
     * @return Diagnostico de domínio
     */
    public Diagnostico toDomain(DiagnosticoRequestDTO request) {
        if (request == null) {
            return null;
        }

        return Diagnostico.criar(
                request.descricao(),
                request.mecanico(),
                request.observacoes());
    }

    /**
     * Converte um Diagnostico de domínio para um DTO de resposta.
     *
     * @param domain Diagnostico de domínio
     * @return DTO de resposta
     */
    public DiagnosticoResponseDTO toResponseDTO(Diagnostico domain) {
        if (domain == null) {
            return null;
        }

        return DiagnosticoResponseDTO.fromDomain(domain);
    }
}
