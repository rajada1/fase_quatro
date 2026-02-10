package br.com.grupo99.operacoes.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoRequestDTO {
    @NotNull(message = "Orçamento ID não pode ser nulo")
    private UUID orcamentoId;

    private UUID manutencaoId;

    @NotBlank(message = "Descrição não pode ser vazia")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @NotNull(message = "Prioridade não pode ser nula")
    private String prioridade;

    private UUID responsavelId;

    @Positive(message = "Tempo estimado deve ser positivo")
    private Integer tempoEstimadoHoras;

    private String observacoes;
}
