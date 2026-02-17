package br.com.grupo99.executionservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO para requisição de criação/atualização de uma Execução de OS.
 */
public record ExecucaoOSRequestDTO(
                @Schema(description = "ID da Ordem de Serviço", requiredMode = Schema.RequiredMode.REQUIRED) UUID osId,

                @Schema(description = "ID do Orçamento", requiredMode = Schema.RequiredMode.REQUIRED) UUID orcamentoId,

                @Schema(description = "Nome do mecânico responsável", requiredMode = Schema.RequiredMode.REQUIRED) String mecanico) {
}
