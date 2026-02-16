package br.com.grupo99.executionservice.application.mapper;

import br.com.grupo99.executionservice.application.dto.TarefaRequestDTO;
import br.com.grupo99.executionservice.application.dto.TarefaResponseDTO;
import br.com.grupo99.executionservice.domain.model.Tarefa;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversões entre Tarefa de domínio e DTOs.
 */
@Component
public class TarefaMapper {

    /**
     * Converte um DTO de requisição para uma Tarefa de domínio.
     *
     * @param request DTO de requisição
     * @return Tarefa de domínio
     */
    public Tarefa toDomain(TarefaRequestDTO request) {
        if (request == null) {
            return null;
        }

        return Tarefa.criar(
                request.descricao(),
                request.mecanico(),
                request.tempoEstimadoMinutos());
    }

    /**
     * Converte uma Tarefa de domínio para um DTO de resposta.
     *
     * @param domain Tarefa de domínio
     * @return DTO de resposta
     */
    public TarefaResponseDTO toResponseDTO(Tarefa domain) {
        if (domain == null) {
            return null;
        }

        return TarefaResponseDTO.fromDomain(domain);
    }
}
