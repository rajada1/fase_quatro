package br.com.grupo99.executionservice.application.dto;

import br.com.grupo99.executionservice.domain.model.Tarefa;
import br.com.grupo99.executionservice.domain.model.StatusTarefa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para requisição de criação/atualização de uma Tarefa.
 */
public record TarefaRequestDTO(
        @Schema(description = "Descrição da tarefa", required = true) String descricao,

        @Schema(description = "Nome do mecânico responsável", required = true) String mecanico,

        @Schema(description = "Tempo estimado em minutos", required = true) Integer tempoEstimadoMinutos) {
}
