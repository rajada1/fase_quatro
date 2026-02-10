package br.com.grupo99.customerservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando um cliente é criado
 * Part of Saga Pattern (Choreography)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCriadoEvent {
    private UUID clienteId;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDateTime timestamp;
    private String eventType = "CLIENTE_CRIADO";
}
