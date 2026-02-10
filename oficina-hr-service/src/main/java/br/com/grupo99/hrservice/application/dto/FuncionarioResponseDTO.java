package br.com.grupo99.hrservice.application.dto;

import br.com.grupo99.hrservice.domain.model.Funcionario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de Funcionario.
 */
public record FuncionarioResponseDTO(
        UUID pessoaId,
        LocalDate dataAdmissao,
        String setor,
        String cargo,
        BigDecimal salario,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static FuncionarioResponseDTO fromDomain(Funcionario funcionario) {
        return new FuncionarioResponseDTO(
                funcionario.getPessoaId(),
                funcionario.getDataAdmissao(),
                funcionario.getSetor(),
                funcionario.getCargo(),
                funcionario.getSalario(),
                funcionario.getAtivo(),
                funcionario.getCreatedAt(),
                funcionario.getUpdatedAt());
    }
}
