package br.com.grupo99.manutencao.adapter.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublishingService {
    private final SqsTemplate sqsTemplate;

    @Value("${app.sqs.maintenance-queue:maintenance-queue}")
    private String maintenanceQueue;

    public void publishManutencaoCriada(String manutencaoId, String veiculoId) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "MANUTENCAO_CRIADA");
        event.put("manutencaoId", manutencaoId);
        event.put("veiculoId", veiculoId);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(maintenanceQueue, event);
            log.info("MANUTENCAO_CRIADA published: {}", manutencaoId);
        } catch (Exception e) {
            log.error("Error publishing MANUTENCAO_CRIADA event", e);
        }
    }

    public void publishManutencaoAtualizada(String manutencaoId, String status) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "MANUTENCAO_ATUALIZADA");
        event.put("manutencaoId", manutencaoId);
        event.put("status", status);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(maintenanceQueue, event);
            log.info("MANUTENCAO_ATUALIZADA published: {}", manutencaoId);
        } catch (Exception e) {
            log.error("Error publishing MANUTENCAO_ATUALIZADA event", e);
        }
    }

    public void publishManutencaoConcluida(String manutencaoId) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "MANUTENCAO_CONCLUIDA");
        event.put("manutencaoId", manutencaoId);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(maintenanceQueue, event);
            log.info("MANUTENCAO_CONCLUIDA published: {}", manutencaoId);
        } catch (Exception e) {
            log.error("Error publishing MANUTENCAO_CONCLUIDA event", e);
        }
    }
}
