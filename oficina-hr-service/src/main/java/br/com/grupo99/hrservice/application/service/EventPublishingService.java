package br.com.grupo99.hrservice.application.service;

import br.com.grupo99.hrservice.domain.model.Funcionario;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço responsável pela publicação de eventos de domínio na fila SQS.
 */
@Service
@Slf4j
public class EventPublishingService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.hr-events-queue:hr-events-queue}")
    private String hrEventsQueue;

    public EventPublishingService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica evento FUNCIONARIO_CRIADO
     */
    public void publicarFuncionarioCriado(Funcionario funcionario) {
        Map<String, Object> evento = construirEventoFuncionario(funcionario, "FUNCIONARIO_CRIADO");
        publicarEvento(evento);
        log.info("Evento FUNCIONARIO_CRIADO publicado para funcionário: {}", funcionario.getPessoaId());
    }

    /**
     * Publica evento FUNCIONARIO_ATUALIZADO
     */
    public void publicarFuncionarioAtualizado(Funcionario funcionario) {
        Map<String, Object> evento = construirEventoFuncionario(funcionario, "FUNCIONARIO_ATUALIZADO");
        publicarEvento(evento);
        log.info("Evento FUNCIONARIO_ATUALIZADO publicado para funcionário: {}", funcionario.getPessoaId());
    }

    /**
     * Publica evento FUNCIONARIO_DELETADO
     */
    public void publicarFuncionarioDeletado(UUID pessoaId) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", "FUNCIONARIO_DELETADO");
        evento.put("pessoaId", pessoaId.toString());
        evento.put("timestamp", LocalDateTime.now().toString());

        publicarEvento(evento);
        log.info("Evento FUNCIONARIO_DELETADO publicado para funcionário: {}", pessoaId);
    }

    // ===== Métodos auxiliares =====

    private Map<String, Object> construirEventoFuncionario(Funcionario funcionario, String tipoEvento) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", tipoEvento);
        evento.put("pessoaId", funcionario.getPessoaId().toString());
        evento.put("setor", funcionario.getSetor());
        evento.put("cargo", funcionario.getCargo());
        evento.put("salario", funcionario.getSalario().toString());
        evento.put("ativo", funcionario.getAtivo());
        evento.put("timestamp", LocalDateTime.now().toString());

        return evento;
    }

    private void publicarEvento(Map<String, Object> evento) {
        try {
            String mensagem = objectMapper.writeValueAsString(evento);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(hrEventsQueue)
                    .messageBody(mensagem)
                                        .messageDeduplicationId(UUID.randomUUID().toString())
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
        } catch (Exception e) {
            log.error("Erro ao publicar evento: {}", evento, e);
            throw new RuntimeException("Erro ao publicar evento na fila SQS", e);
        }
    }
}
