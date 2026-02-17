package br.com.grupo99.executionservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para requisição de criação/atualização de um Diagnóstico.
 */
public record DiagnosticoRequestDTO(
                @Schema(description = "Descrição do diagnóstico", requiredMode = Schema.RequiredMode.REQUIRED) String descricao,

                @Schema(description = "Nome do mecânico responsável", requiredMode = Schema.RequiredMode.REQUIRED) String mecanico,

                @Schema(description = "Observações sobre o diagnóstico") String observacoes) {
}
