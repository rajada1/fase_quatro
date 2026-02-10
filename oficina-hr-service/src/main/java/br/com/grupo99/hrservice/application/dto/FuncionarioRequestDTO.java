package br.com.grupo99.hrservice.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para requisições de Funcionario.
 */
public record FuncionarioRequestDTO(
        @NotNull(message = "Pessoa ID é obrigatório") UUID pessoaId,

        @NotNull(message = "Data de admissão é obrigatória") @PastOrPresent(message = "Data de admissão não pode ser no futuro") LocalDate dataAdmissao,

        @NotBlank(message = "Setor é obrigatório") String setor,

        @NotBlank(message = "Cargo é obrigatório") String cargo,

        @NotNull(message = "Salário é obrigatório") @DecimalMin(value = "0.01", message = "Salário deve ser maior que zero") BigDecimal salario) {
}
