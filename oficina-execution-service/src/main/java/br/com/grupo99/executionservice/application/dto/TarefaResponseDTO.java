package br.com.grupo99.executionservice.application.dto;

import br.com.grupo99.executionservice.domain.model.Tarefa;
import br.com.grupo99.executionservice.domain.model.StatusTarefa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para resposta de uma Tarefa.
 */
public record TarefaResponseDTO(
        @Schema(description = "ID da tarefa") UUID id,

        @Schema(description = "Descrição da tarefa") String descricao,

        @Schema(description = "Nome do mecânico responsável") String mecanico,

        @Schema(description = "Tempo estimado em minutos") Integer tempoEstimadoMinutos,

        @Schema(description = "Tempo real gasto em minutos") Integer tempoRealMinutos,

        @Schema(description = "Status da tarefa") StatusTarefa status,

        @Schema(description = "Data de início") Instant dataInicio,

        @Schema(description = "Data de finalização") Instant dataFinalizacao) {
    /**
     * Mapeia uma Tarefa de domínio para um DTO de resposta.
     *
     * @param tarefa tarefa de domínio
     * @return DTO de resposta
     */
    public static TarefaResponseDTO fromDomain(Tarefa tarefa) {
        return new TarefaResponseDTO(
                tarefa.getId(),
                tarefa.getDescricao(),
                tarefa.getMecanico(),
                tarefa.getTempoEstimadoMinutos(),
                tarefa.getTempoRealMinutos(),
                tarefa.getStatus(),
                tarefa.getDataInicio(),
                tarefa.getDataFinalizacao());
    }
}
