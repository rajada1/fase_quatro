package br.com.grupo99.customerservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando um veículo é adicionado ao cliente
 * Part of Saga Pattern (Choreography)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoAdicionadoEvent {
    private UUID veiculoId;
    private UUID clienteId;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private LocalDateTime timestamp;
    private String eventType = "VEICULO_ADICIONADO";
}
