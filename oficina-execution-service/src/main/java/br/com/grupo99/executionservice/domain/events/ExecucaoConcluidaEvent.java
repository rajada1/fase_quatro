package br.com.grupo99.executionservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecucaoConcluidaEvent {
    private UUID execucaoId;
    private UUID osId;
    private String mecanico;
    private String observacoes;
    private LocalDateTime timestamp;
    @Builder.Default
    private String eventType = "EXECUCAO_CONCLUIDA";
}
