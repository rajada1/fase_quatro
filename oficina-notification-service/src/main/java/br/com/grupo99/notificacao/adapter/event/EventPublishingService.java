package br.com.grupo99.notificacao.adapter.event;

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

    @Value("${app.sqs.notification-queue:notification-queue}")
    private String notificationQueue;

    public void publishNotificacaoPendente(String notificacaoId, String email, String tipo) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "NOTIFICACAO_PENDENTE");
        event.put("notificacaoId", notificacaoId);
        event.put("email", email);
        event.put("tipo", tipo);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(notificationQueue, event);
            log.info("NOTIFICACAO_PENDENTE published: {}", notificacaoId);
        } catch (Exception e) {
            log.error("Error publishing NOTIFICACAO_PENDENTE event", e);
        }
    }

    public void publishNotificacaoEnviada(String notificacaoId) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "NOTIFICACAO_ENVIADA");
        event.put("notificacaoId", notificacaoId);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(notificationQueue, event);
            log.info("NOTIFICACAO_ENVIADA published: {}", notificacaoId);
        } catch (Exception e) {
            log.error("Error publishing NOTIFICACAO_ENVIADA event", e);
        }
    }

    public void publishNotificacaoFalha(String notificacaoId) {
        Map<String, String> event = new HashMap<>();
        event.put("eventType", "NOTIFICACAO_FALHA");
        event.put("notificacaoId", notificacaoId);
        event.put("timestamp", String.valueOf(System.currentTimeMillis()));

        try {
            sqsTemplate.send(notificationQueue, event);
            log.info("NOTIFICACAO_FALHA published: {}", notificacaoId);
        } catch (Exception e) {
            log.error("Error publishing NOTIFICACAO_FALHA event", e);
        }
    }
}
