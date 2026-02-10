package br.com.grupo99.hrservice.infrastructure.messaging;

import br.com.grupo99.hrservice.domain.events.FuncionarioCriadoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;

/**
 * Publicador de eventos para o SQS (Saga Pattern - Event Publisher)
 * HR Service publica eventos de funcionários criados
 */
@Slf4j
@Service
public class HREventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queues.hr-events:hr-events-queue}")
    private String hrEventsQueueUrl;

    public HREventPublisher(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica evento de funcionário criado (Saga Choreography)
     */
    public void publishFuncionarioCriado(FuncionarioCriadoEvent event) {
        try {
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }
            
            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(hrEventsQueueUrl)
                    .messageBody(messageBody)
                                        .messageDeduplicationId(event.getFuncionarioId().toString() + "-" + event.getTimestamp())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            log.info("Evento FUNCIONARIO_CRIADO publicado: {}", event.getFuncionarioId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento FUNCIONARIO_CRIADO", e);
            throw new RuntimeException("Falha ao publicar evento de funcionário criado", e);
        }
    }
}
