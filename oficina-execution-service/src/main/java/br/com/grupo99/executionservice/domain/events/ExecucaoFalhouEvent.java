package br.com.grupo99.executionservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de compensação - Execução falhou e precisa ser cancelada
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecucaoFalhouEvent {
    private UUID execucaoId;
    private UUID osId;
    private String motivo;
    private String etapaFalha;
    @Builder.Default
    private Boolean requerRetrabalho = false;
    private LocalDateTime timestamp;
    @Builder.Default
    private String eventType = "EXECUCAO_FALHOU";
}
