package br.com.grupo99.operacoes.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.grupo99.operacoes.domain.Operacao;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoResponseDTO {
    private UUID id;
    private String numeroOperacao;
    private UUID orcamentoId;
    private UUID manutencaoId;
    private String descricao;
    private String status;
    private String prioridade;
    private UUID responsavelId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataConclusao;

    private Integer tempoEstimadoHoras;
    private Integer tempoRealizadoHoras;
    private String observacoes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static OperacaoResponseDTO fromDomain(Operacao operacao) {
        return OperacaoResponseDTO.builder()
                .id(operacao.getId())
                .numeroOperacao(operacao.getNumeroOperacao())
                .orcamentoId(operacao.getOrcamentoId())
                .manutencaoId(operacao.getManutencaoId())
                .descricao(operacao.getDescricao())
                .status(operacao.getStatus().name())
                .prioridade(operacao.getPrioridade().name())
                .responsavelId(operacao.getResponsavelId())
                .dataInicio(operacao.getDataInicio())
                .dataConclusao(operacao.getDataConclusao())
                .tempoEstimadoHoras(operacao.getTempoEstimadoHoras())
                .tempoRealizadoHoras(operacao.getTempoRealizadoHoras())
                .observacoes(operacao.getObservacoes())
                .createdAt(operacao.getCreatedAt())
                .updatedAt(operacao.getUpdatedAt())
                .build();
    }
}
