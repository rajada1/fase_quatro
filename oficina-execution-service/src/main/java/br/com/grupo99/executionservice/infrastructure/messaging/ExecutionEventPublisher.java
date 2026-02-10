package br.com.grupo99.executionservice.infrastructure.messaging;

import br.com.grupo99.executionservice.domain.events.DiagnosticoConcluidoEvent;
import br.com.grupo99.executionservice.domain.events.ExecucaoConcluidaEvent;
import br.com.grupo99.executionservice.domain.events.ExecucaoFalhouEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * Publicador de eventos para o SQS (Saga Pattern - Event Publisher)
 */
@Slf4j
@Service
public class ExecutionEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queues.execution-events}")
    private String executionEventsQueueUrl;

    public ExecutionEventPublisher(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica evento de diagnóstico concluído (Saga Step 2.5)
     */
    public void publishDiagnosticoConcluido(DiagnosticoConcluidoEvent event) {
        try {
            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(executionEventsQueueUrl)
                    .messageBody(messageBody)
                                        .messageDeduplicationId(event.getExecucaoId().toString() + "-" + event.getTimestamp())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);

            log.info("Evento DIAGNOSTICO_CONCLUIDO publicado. Execução ID: {}", event.getExecucaoId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento DIAGNOSTICO_CONCLUIDO: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento DIAGNOSTICO_CONCLUIDO", e);
        }
    }

    /**
     * Publica evento de execução concluída (Saga Step Final)
     */
    public void publishExecucaoConcluida(ExecucaoConcluidaEvent event) {
        try {
            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(executionEventsQueueUrl)
                    .messageBody(messageBody)
                                        .messageDeduplicationId(event.getExecucaoId().toString() + "-" + event.getTimestamp())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);

            log.info("Evento EXECUCAO_CONCLUIDA publicado. Execução ID: {}, OS ID: {}",
                    event.getExecucaoId(), event.getOsId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento EXECUCAO_CONCLUIDA: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento EXECUCAO_CONCLUIDA", e);
        }
    }

    /**
     * Publica evento de compensação - Execução falhou (Rollback)
     */
    public void publishExecucaoFalhou(ExecucaoFalhouEvent event) {
        try {
            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(executionEventsQueueUrl)
                    .messageBody(messageBody)
                                        .messageDeduplicationId(event.getExecucaoId().toString() + "-failed-" + event.getTimestamp())
                    .build();

            sqsClient.sendMessage(sendMsgRequest);

            log.error("🔄 Evento de compensação EXECUCAO_FALHOU publicado. Execução ID: {}, Motivo: {}",
                    event.getExecucaoId(), event.getMotivo());
        } catch (Exception e) {
            log.error("Erro crítico ao publicar evento de compensação EXECUCAO_FALHOU: {}", e.getMessage(), e);
        }
    }
}
