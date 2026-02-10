package br.com.grupo99.peopleservice.application.dto;

import br.com.grupo99.peopleservice.domain.model.Perfil;
import br.com.grupo99.peopleservice.domain.model.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de Pessoa.
 */
public record PessoaRequestDTO(
                @NotBlank(message = "Número de documento é obrigatório") String numeroDocumento,

                TipoPessoa tipoPessoa,

                @NotBlank(message = "Nome é obrigatório") String name,

                @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email,

                String phone,

                Perfil perfil) {
}
