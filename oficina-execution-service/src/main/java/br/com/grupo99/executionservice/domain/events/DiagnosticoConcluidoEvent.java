package br.com.grupo99.executionservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoConcluidoEvent {
    private UUID osId;
    private UUID execucaoId;
    private String diagnostico;
    private BigDecimal valorEstimado;
    private LocalDateTime timestamp;
    @Builder.Default
    private String eventType = "DIAGNOSTICO_CONCLUIDO";
}
