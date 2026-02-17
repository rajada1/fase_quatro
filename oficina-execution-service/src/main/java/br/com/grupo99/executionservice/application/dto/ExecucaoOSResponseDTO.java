package br.com.grupo99.executionservice.application.dto;

import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para resposta de uma Execução de OS.
 */
public record ExecucaoOSResponseDTO(
        @Schema(description = "ID da execução") UUID id,

        @Schema(description = "ID da Ordem de Serviço") UUID osId,

        @Schema(description = "ID do Orçamento") UUID orcamentoId,

        @Schema(description = "Status da execução") StatusExecucao status,

        @Schema(description = "Nome do mecânico responsável") String mecanico,

        @Schema(description = "Data de início da execução") Instant dataInicio,

        @Schema(description = "Data de finalização da execução") Instant dataFinalizacao,

        @Schema(description = "Observações sobre a execução") String observacoes,

        @Schema(description = "Número de diagnósticos realizados") Integer totalDiagnosticos,

        @Schema(description = "Número de tarefas") Integer totalTarefas,

        @Schema(description = "Número de peças utilizadas") Integer totalPecas,

        @Schema(description = "Data de criação") Instant createdAt,

        @Schema(description = "Data de atualização") Instant updatedAt) {
    /**
     * Mapeia um ExecucaoOS de domínio para um DTO de resposta.
     *
     * @param execucaoOS execução de domínio
     * @return DTO de resposta
     */
    public static ExecucaoOSResponseDTO fromDomain(ExecucaoOS execucaoOS) {
        return new ExecucaoOSResponseDTO(
                execucaoOS.getId(),
                execucaoOS.getOsId(),
                execucaoOS.getOrcamentoId(),
                execucaoOS.getStatus(),
                execucaoOS.getMecanico(),
                execucaoOS.getDataInicio(),
                execucaoOS.getDataFinalizacao(),
                execucaoOS.getObservacoes(),
                execucaoOS.getDiagnosticos().size(),
                execucaoOS.getTarefas().size(),
                execucaoOS.getPecas().size(),
                execucaoOS.getCreatedAt(),
                execucaoOS.getUpdatedAt());
    }
}
