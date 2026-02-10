package br.com.grupo99.manutencao.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManutencaoRequestDTO {
    @NotNull(message = "Veículo ID não pode ser nulo")
    private UUID veiculoId;

    @NotNull(message = "Data agendada não pode ser nula")
    @Future(message = "Data agendada deve ser no futuro")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAgendada;

    @NotNull(message = "Tipo de manutenção não pode ser nulo")
    private String tipoManutencao;

    @NotBlank(message = "Descrição não pode ser vazia")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    private String observacoes;
}
