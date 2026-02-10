package br.com.grupo99.peopleservice.application.dto;

import br.com.grupo99.peopleservice.domain.model.Perfil;
import br.com.grupo99.peopleservice.domain.model.Pessoa;
import br.com.grupo99.peopleservice.domain.model.TipoPessoa;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de Pessoa.
 */
public record PessoaResponseDTO(
        UUID id,
        String numeroDocumento,
        TipoPessoa tipoPessoa,
        String name,
        String email,
        String phone,
        Perfil perfil,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static PessoaResponseDTO fromDomain(Pessoa pessoa) {
        return new PessoaResponseDTO(
                pessoa.getId(),
                pessoa.getNumeroDocumento(),
                pessoa.getTipoPessoa(),
                pessoa.getName(),
                pessoa.getEmail(),
                pessoa.getPhone(),
                pessoa.getPerfil(),
                pessoa.getCreatedAt(),
                pessoa.getUpdatedAt());
    }
}
