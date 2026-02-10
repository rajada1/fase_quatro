package br.com.grupo99.operacoes.adapter.event;

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

    @Value("${app.sqs.operations-queue:operations-queue}")
    private String operationsQueue;

    public void publishOperacaoCriada(String operacaoId, String numeroOperacao) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "OPERACAO_CRIADA");
        event.put("operacaoId", operacaoId);
        event.put("numeroOperacao", numeroOperacao);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(operationsQueue, event);
            log.info("OPERACAO_CRIADA published: {}", operacaoId);
        } catch (Exception e) {
            log.error("Error publishing OPERACAO_CRIADA event", e);
        }
    }

    public void publishOperacaoIniciada(String operacaoId, String status) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "OPERACAO_INICIADA");
        event.put("operacaoId", operacaoId);
        event.put("status", status);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(operationsQueue, event);
            log.info("OPERACAO_INICIADA published: {}", operacaoId);
        } catch (Exception e) {
            log.error("Error publishing OPERACAO_INICIADA event", e);
        }
    }

    public void publishOperacaoConcluida(String operacaoId) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "OPERACAO_CONCLUIDA");
        event.put("operacaoId", operacaoId);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(operationsQueue, event);
            log.info("OPERACAO_CONCLUIDA published: {}", operacaoId);
        } catch (Exception e) {
            log.error("Error publishing OPERACAO_CONCLUIDA event", e);
        }
    }
}
