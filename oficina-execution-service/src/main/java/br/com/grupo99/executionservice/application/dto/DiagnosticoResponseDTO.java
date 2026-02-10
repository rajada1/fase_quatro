package br.com.grupo99.executionservice.application.dto;

import br.com.grupo99.executionservice.domain.model.Diagnostico;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para resposta de um Diagnóstico.
 */
public record DiagnosticoResponseDTO(
        @Schema(description = "ID do diagnóstico") UUID id,

        @Schema(description = "Descrição do diagnóstico") String descricao,

        @Schema(description = "Nome do mecânico responsável") String mecanico,

        @Schema(description = "Data do diagnóstico") Instant dataDiagnostico,

        @Schema(description = "Observações sobre o diagnóstico") String observacoes) {
    /**
     * Mapeia um Diagnostico de domínio para um DTO de resposta.
     *
     * @param diagnostico diagnóstico de domínio
     * @return DTO de resposta
     */
    public static DiagnosticoResponseDTO fromDomain(Diagnostico diagnostico) {
        return new DiagnosticoResponseDTO(
                diagnostico.getId(),
                diagnostico.getDescricao(),
                diagnostico.getMecanico(),
                diagnostico.getDataDiagnostico(),
                diagnostico.getObservacoes());
    }
}
