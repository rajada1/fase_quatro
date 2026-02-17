package br.com.grupo99.executionservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para requisição de criação/atualização de uma Tarefa.
 */
public record TarefaRequestDTO(
                @Schema(description = "Descrição da tarefa", requiredMode = Schema.RequiredMode.REQUIRED) String descricao,

                @Schema(description = "Nome do mecânico responsável", requiredMode = Schema.RequiredMode.REQUIRED) String mecanico,

                @Schema(description = "Tempo estimado em minutos", requiredMode = Schema.RequiredMode.REQUIRED) Integer tempoEstimadoMinutos) {
}
