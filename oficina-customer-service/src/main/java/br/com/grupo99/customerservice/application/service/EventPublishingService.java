package br.com.grupo99.customerservice.application.service;

import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.model.Veiculo;
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

    @Value("${aws.sqs.customer-events-queue:customer-events-queue}")
    private String customerEventsQueue;

    public EventPublishingService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica evento CLIENTE_CRIADO
     */
    public void publicarClienteCriado(Cliente cliente) {
        Map<String, Object> evento = construirEventoCliente(cliente, "CLIENTE_CRIADO");
        publicarEvento(evento);
        log.info("Evento CLIENTE_CRIADO publicado para cliente: {}", cliente.getPessoaId());
    }

    /**
     * Publica evento CLIENTE_ATUALIZADO
     */
    public void publicarClienteAtualizado(Cliente cliente) {
        Map<String, Object> evento = construirEventoCliente(cliente, "CLIENTE_ATUALIZADO");
        publicarEvento(evento);
        log.info("Evento CLIENTE_ATUALIZADO publicado para cliente: {}", cliente.getPessoaId());
    }

    /**
     * Publica evento CLIENTE_DELETADO
     */
    public void publicarClienteDeletado(UUID clienteId) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", "CLIENTE_DELETADO");
        evento.put("clienteId", clienteId.toString());
        evento.put("timestamp", LocalDateTime.now().toString());

        publicarEvento(evento);
        log.info("Evento CLIENTE_DELETADO publicado para cliente: {}", clienteId);
    }

    /**
     * Publica evento VEICULO_CRIADO
     */
    public void publicarVeiculoCriado(Veiculo veiculo) {
        Map<String, Object> evento = construirEventoVeiculo(veiculo, "VEICULO_CRIADO");
        publicarEvento(evento);
        log.info("Evento VEICULO_CRIADO publicado para veículo: {}", veiculo.getId());
    }

    /**
     * Publica evento VEICULO_ATUALIZADO
     */
    public void publicarVeiculoAtualizado(Veiculo veiculo) {
        Map<String, Object> evento = construirEventoVeiculo(veiculo, "VEICULO_ATUALIZADO");
        publicarEvento(evento);
        log.info("Evento VEICULO_ATUALIZADO publicado para veículo: {}", veiculo.getId());
    }

    /**
     * Publica evento VEICULO_DELETADO
     */
    public void publicarVeiculoDeletado(UUID veiculoId) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", "VEICULO_DELETADO");
        evento.put("veiculoId", veiculoId.toString());
        evento.put("timestamp", LocalDateTime.now().toString());

        publicarEvento(evento);
        log.info("Evento VEICULO_DELETADO publicado para veículo: {}", veiculoId);
    }

    /**
     * Publica evento VEICULO_REGISTRADO
     */
    public void publicarVeiculoRegistrado(Veiculo veiculo, UUID clienteId) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", "VEICULO_REGISTRADO");
        evento.put("veiculoId", veiculo.getId().toString());
        evento.put("clienteId", clienteId.toString());
        evento.put("placa", veiculo.getPlaca());
        evento.put("marca", veiculo.getMarca());
        evento.put("modelo", veiculo.getModelo());
        evento.put("ano", veiculo.getAno());
        evento.put("timestamp", LocalDateTime.now().toString());

        publicarEvento(evento);
        log.info("Evento VEICULO_REGISTRADO publicado para veículo: {} de cliente: {}", veiculo.getId(), clienteId);
    }

    // ===== Métodos auxiliares =====

    private Map<String, Object> construirEventoCliente(Cliente cliente, String tipoEvento) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", tipoEvento);
        evento.put("pessoaId", cliente.getPessoaId().toString());
        evento.put("quantidadeVeiculos", cliente.getVeiculos().size());
        evento.put("timestamp", LocalDateTime.now().toString());

        return evento;
    }

    private Map<String, Object> construirEventoVeiculo(Veiculo veiculo, String tipoEvento) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", tipoEvento);
        evento.put("veiculoId", veiculo.getId().toString());
        evento.put("clienteId", veiculo.getCliente().getPessoaId().toString());
        evento.put("placa", veiculo.getPlaca());
        evento.put("marca", veiculo.getMarca());
        evento.put("modelo", veiculo.getModelo());
        evento.put("ano", veiculo.getAno());
        evento.put("timestamp", LocalDateTime.now().toString());

        return evento;
    }

    private void publicarEvento(Map<String, Object> evento) {
        try {
            String mensagem = objectMapper.writeValueAsString(evento);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(customerEventsQueue)
                    .messageBody(mensagem)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
        } catch (Exception e) {
            log.error("Erro ao publicar evento: {}", evento, e);
            throw new RuntimeException("Erro ao publicar evento na fila SQS", e);
        }
    }
}
