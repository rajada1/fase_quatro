package br.com.grupo99.customerservice.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO para requisição de Cliente.
 * ClienteRequestDTO apenas recebe pessoaId (vem do People Service).
 */
public record ClienteRequestDTO(
                @NotNull(message = "Pessoa ID é obrigatório") UUID pessoaId) {
}
