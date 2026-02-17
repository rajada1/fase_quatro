package br.com.grupo99.executionservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para requisição de criação/atualização de um Uso de Peça.
 */
public record UsoPecaRequestDTO(
                @Schema(description = "ID da peça", requiredMode = Schema.RequiredMode.REQUIRED) UUID pecaId,

                @Schema(description = "Descrição da peça", requiredMode = Schema.RequiredMode.REQUIRED) String descricao,

                @Schema(description = "Quantidade utilizada", requiredMode = Schema.RequiredMode.REQUIRED) Integer quantidade,

                @Schema(description = "Valor unitário", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal valorUnitario) {
}
