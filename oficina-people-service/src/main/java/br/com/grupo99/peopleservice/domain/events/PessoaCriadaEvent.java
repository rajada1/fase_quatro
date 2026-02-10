package br.com.grupo99.peopleservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando uma pessoa é criada
 * Part of Saga Pattern (Choreography)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaCriadaEvent {
    private UUID pessoaId;
    private String nome;
    private String cpf;
    private String email;
    private LocalDateTime timestamp;
    private String eventType = "PESSOA_CRIADA";
}
