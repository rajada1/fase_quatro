package br.com.grupo99.executionservice.application.dto;

import br.com.grupo99.executionservice.domain.model.UsoPeca;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO para resposta de um Uso de Peça.
 */
public record UsoPecaResponseDTO(
        @Schema(description = "ID do uso de peça") UUID id,

        @Schema(description = "ID da peça") UUID pecaId,

        @Schema(description = "Descrição da peça") String descricao,

        @Schema(description = "Quantidade utilizada") Integer quantidade,

        @Schema(description = "Valor unitário") BigDecimal valorUnitario,

        @Schema(description = "Valor total (quantidade * valor unitário)") BigDecimal valorTotal,

        @Schema(description = "Data de uso") Instant dataUso) {
    /**
     * Mapeia um UsoPeca de domínio para um DTO de resposta.
     *
     * @param usoPeca uso de peça de domínio
     * @return DTO de resposta
     */
    public static UsoPecaResponseDTO fromDomain(UsoPeca usoPeca) {
        return new UsoPecaResponseDTO(
                usoPeca.getId(),
                usoPeca.getPecaId(),
                usoPeca.getDescricao(),
                usoPeca.getQuantidade(),
                usoPeca.getValorUnitario(),
                usoPeca.getValorTotal(),
                usoPeca.getDataUso());
    }
}
