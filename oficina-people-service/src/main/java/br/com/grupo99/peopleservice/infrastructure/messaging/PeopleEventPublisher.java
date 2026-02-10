package br.com.grupo99.peopleservice.infrastructure.messaging;

import br.com.grupo99.peopleservice.domain.events.PessoaCriadaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;

/**
 * Publicador de eventos para o SQS (Saga Pattern - Event Publisher)
 * People Service publica eventos de pessoas criadas
 */
@Slf4j
@Service
public class PeopleEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queues.people-events:people-events-queue}")
    private String peopleEventsQueueUrl;

    public PeopleEventPublisher(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica evento de pessoa criada (Saga Choreography)
     */
    public void publishPessoaCriada(PessoaCriadaEvent event) {
        try {
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }

            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(peopleEventsQueueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            log.info("Evento PESSOA_CRIADA publicado: {}", event.getPessoaId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento PESSOA_CRIADA", e);
            throw new RuntimeException("Falha ao publicar evento de pessoa criada", e);
        }
    }
}
