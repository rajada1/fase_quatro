package br.com.grupo99.peopleservice.application.service;

import br.com.grupo99.peopleservice.domain.model.Pessoa;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço responsável por publicar eventos de Pessoa em SQS.
 */
@Service
@Slf4j
public class EventPublishingService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue.pessoas-events:pessoas-events-queue}")
    private String pessoasEventQueueName;

    public EventPublishingService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void publicarPessoaCriada(Pessoa pessoa) {
        publicarEvento("PESSOA_CRIADA", pessoa);
    }

    public void publicarPessoaAtualizada(Pessoa pessoa) {
        publicarEvento("PESSOA_ATUALIZADA", pessoa);
    }

    public void publicarPessoaDeletada(Pessoa pessoa) {
        publicarEvento("PESSOA_DELETADA", pessoa);
    }

    private void publicarEvento(String tipoEvento, Pessoa pessoa) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("tipoEvento", tipoEvento);
            evento.put("pessoaId", pessoa.getId());
            evento.put("numeroDocumento", pessoa.getNumeroDocumento());
            evento.put("tipoPessoa", pessoa.getTipoPessoa());
            evento.put("name", pessoa.getName());
            evento.put("email", pessoa.getEmail());
            evento.put("perfil", pessoa.getPerfil());
            evento.put("timestamp", System.currentTimeMillis());

            String messageBody = objectMapper.writeValueAsString(evento);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(pessoasEventQueueName)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            log.info("Evento {} publicado para pessoa: {}", tipoEvento, pessoa.getId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento: {}", tipoEvento, e);
            throw new RuntimeException("Erro ao publicar evento na fila SQS", e);
        }
    }
}
