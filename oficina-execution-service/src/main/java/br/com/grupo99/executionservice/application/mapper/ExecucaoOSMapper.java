package br.com.grupo99.executionservice.application.mapper;

import br.com.grupo99.executionservice.application.dto.ExecucaoOSRequestDTO;
import br.com.grupo99.executionservice.application.dto.ExecucaoOSResponseDTO;
import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversões entre ExecucaoOS de domínio e DTOs.
 */
@Component
public class ExecucaoOSMapper {

    /**
     * Converte um DTO de requisição para uma ExecucaoOS de domínio.
     *
     * @param request DTO de requisição
     * @return ExecucaoOS de domínio
     */
    public ExecucaoOS toDomain(ExecucaoOSRequestDTO request) {
        if (request == null) {
            return null;
        }

        return ExecucaoOS.criar(
                request.osId(),
                request.orcamentoId(),
                request.mecanico());
    }

    /**
     * Converte uma ExecucaoOS de domínio para um DTO de resposta.
     *
     * @param domain ExecucaoOS de domínio
     * @return DTO de resposta
     */
    public ExecucaoOSResponseDTO toResponseDTO(ExecucaoOS domain) {
        if (domain == null) {
            return null;
        }

        return ExecucaoOSResponseDTO.fromDomain(domain);
    }
}
