package br.com.grupo99.executionservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para requisição de criação/atualização de um Uso de Peça.
 */
public record UsoPecaRequestDTO(
        @Schema(description = "ID da peça", required = true) UUID pecaId,

        @Schema(description = "Descrição da peça", required = true) String descricao,

        @Schema(description = "Quantidade utilizada", required = true) Integer quantidade,

        @Schema(description = "Valor unitário", required = true) BigDecimal valorUnitario) {
}
