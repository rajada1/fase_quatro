package br.com.grupo99.executionservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO para requisição de criação/atualização de uma Execução de OS.
 */
public record ExecucaoOSRequestDTO(
        @Schema(description = "ID da Ordem de Serviço", required = true) UUID osId,

        @Schema(description = "ID do Orçamento", required = true) UUID orcamentoId,

        @Schema(description = "Nome do mecânico responsável", required = true) String mecanico) {
}
