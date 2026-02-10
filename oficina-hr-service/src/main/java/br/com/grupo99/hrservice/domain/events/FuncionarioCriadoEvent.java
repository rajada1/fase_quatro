package br.com.grupo99.hrservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando um funcionário é criado
 * Part of Saga Pattern (Choreography)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioCriadoEvent {
    private UUID funcionarioId;
    private UUID pessoaId;
    private String matricula;
    private String cargo;
    private LocalDateTime dataAdmissao;
    private LocalDateTime timestamp;
    private String eventType = "FUNCIONARIO_CRIADO";
}
