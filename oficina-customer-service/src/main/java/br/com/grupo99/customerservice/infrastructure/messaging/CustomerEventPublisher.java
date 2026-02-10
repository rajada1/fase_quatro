package br.com.grupo99.customerservice.infrastructure.messaging;

import br.com.grupo99.customerservice.domain.events.ClienteCriadoEvent;
import br.com.grupo99.customerservice.domain.events.VeiculoAdicionadoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;

/**
 * Publicador de eventos para o SQS (Saga Pattern - Event Publisher)
 * Customer Service publica eventos de clientes e veículos criados
 */
@Slf4j
@Service
public class CustomerEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queues.customer-events:customer-events-queue}")
    private String customerEventsQueueUrl;

    public CustomerEventPublisher(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica evento de cliente criado (Saga Choreography)
     */
    public void publishClienteCriado(ClienteCriadoEvent event) {
        try {
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }

            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(customerEventsQueueUrl)
                    .messageBody(messageBody)
                                        .messageDeduplicationId(event.getClienteId().toString() + "-" + event.getTimestamp())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            log.info("Evento CLIENTE_CRIADO publicado: {}", event.getClienteId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento CLIENTE_CRIADO", e);
            throw new RuntimeException("Falha ao publicar evento de cliente criado", e);
        }
    }

    /**
     * Publica evento de veículo adicionado (Saga Choreography)
     */
    public void publishVeiculoAdicionado(VeiculoAdicionadoEvent event) {
        try {
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }

            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(customerEventsQueueUrl)
                    .messageBody(messageBody)
                                        .messageDeduplicationId(event.getVeiculoId().toString() + "-" + event.getTimestamp())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            log.info("Evento VEICULO_ADICIONADO publicado: {}", event.getVeiculoId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento VEICULO_ADICIONADO", e);
            throw new RuntimeException("Falha ao publicar evento de veículo adicionado", e);
        }
    }
}
