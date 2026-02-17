package br.com.grupo99.executionservice.infrastructure.messaging;

import br.com.grupo99.executionservice.domain.events.DiagnosticoConcluidoEvent;
import br.com.grupo99.executionservice.domain.events.ExecucaoConcluidaEvent;
import br.com.grupo99.executionservice.domain.events.ExecucaoFalhouEvent;
import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.infrastructure.config.KafkaConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Publicador de eventos Kafka para o Execution Service
 * Implementa o padrão Saga Coreografada para eventos de execução.
 * 
 * Tópico: execution-events
 * Partition Key: osId (garante ordenação por OS)
 * 
 * Resiliência:
 * - Circuit Breaker para proteção contra falhas do broker
 * - Retry com backoff exponencial
 */
@Slf4j
@Service
@Primary
public class KafkaExecutionEventPublisher implements ExecutionEventPublisherPort {

    private static final String CIRCUIT_BREAKER_NAME = "kafkaPublisher";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaExecutionEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "publishDiagnosticoConcluidoFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public void publishDiagnosticoConcluido(DiagnosticoConcluidoEvent event) {
        String key = event.getOsId().toString();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                KafkaConfig.TOPIC_EXECUTION_EVENTS, key, event);

        record.headers()
                .add(new RecordHeader("eventType", "DIAGNOSTICO_CONCLUIDO".getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("osId", key.getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("execucaoId",
                        event.getExecucaoId().toString().getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("valorEstimado",
                        event.getValorEstimado().toString().getBytes(StandardCharsets.UTF_8)));

        // Evento importante para o fluxo - envio síncrono
        sendSync(record, "DIAGNOSTICO_CONCLUIDO", event.getExecucaoId().toString());
    }

    public void publishDiagnosticoConcluidoFallback(DiagnosticoConcluidoEvent event, Throwable t) {
        log.error("🔴 Circuit Breaker ABERTO - Evento DIAGNOSTICO_CONCLUIDO não publicado. OS ID: {}, Erro: {}",
                event.getOsId(), t.getMessage());
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "publishExecucaoConcluidaFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public void publishExecucaoConcluida(ExecucaoConcluidaEvent event) {
        String key = event.getOsId().toString();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                KafkaConfig.TOPIC_EXECUTION_EVENTS, key, event);

        record.headers()
                .add(new RecordHeader("eventType", "EXECUCAO_CONCLUIDA".getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("osId", key.getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("execucaoId",
                        event.getExecucaoId().toString().getBytes(StandardCharsets.UTF_8)));

        // Evento final da Saga - envio síncrono
        sendSync(record, "EXECUCAO_CONCLUIDA", event.getExecucaoId().toString());
    }

    public void publishExecucaoConcluidaFallback(ExecucaoConcluidaEvent event, Throwable t) {
        log.error("🔴 Circuit Breaker ABERTO - Evento EXECUCAO_CONCLUIDA não publicado. OS ID: {}, Erro: {}",
                event.getOsId(), t.getMessage());
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "publishExecucaoFalhouFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public void publishExecucaoFalhou(ExecucaoFalhouEvent event) {
        String key = event.getOsId().toString();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                KafkaConfig.TOPIC_EXECUTION_EVENTS, key, event);

        record.headers()
                .add(new RecordHeader("eventType", "EXECUCAO_FALHOU".getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("osId", key.getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("motivo", event.getMotivo().getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("requerRetrabalho",
                        event.getRequerRetrabalho().toString().getBytes(StandardCharsets.UTF_8)));

        // Evento de compensação - envio síncrono obrigatório
        sendSync(record, "EXECUCAO_FALHOU", event.getExecucaoId().toString());
    }

    public void publishExecucaoFalhouFallback(ExecucaoFalhouEvent event, Throwable t) {
        log.error("🔴 Circuit Breaker ABERTO - Evento CRÍTICO EXECUCAO_FALHOU não publicado. OS ID: {}, Erro: {}",
                event.getOsId(), t.getMessage());
    }

    // ===================== MÉTODOS DE CONVENIÊNCIA =====================

    @Override
    public void publicarDiagnosticoConcluido(ExecucaoOS execucao) {
        // Valor estimado padrão (pode ser calculado de outra forma no futuro)
        BigDecimal valorEstimado = BigDecimal.ZERO;

        String diagnosticoResumo = execucao.getDiagnosticos() != null && !execucao.getDiagnosticos().isEmpty()
                ? execucao.getDiagnosticos().get(execucao.getDiagnosticos().size() - 1).getDescricao()
                : "Diagnóstico concluído";

        DiagnosticoConcluidoEvent event = DiagnosticoConcluidoEvent.builder()
                .osId(execucao.getOsId())
                .execucaoId(execucao.getId())
                .diagnostico(diagnosticoResumo)
                .valorEstimado(valorEstimado)
                .timestamp(LocalDateTime.now())
                .build();

        publishDiagnosticoConcluido(event);
    }

    @Override
    public void publicarExecucaoConcluida(ExecucaoOS execucao) {
        ExecucaoConcluidaEvent event = ExecucaoConcluidaEvent.builder()
                .execucaoId(execucao.getId())
                .osId(execucao.getOsId())
                .mecanico(execucao.getMecanico())
                .observacoes(execucao.getObservacoes())
                .timestamp(LocalDateTime.now())
                .build();

        publishExecucaoConcluida(event);
    }

    @Override
    public void publicarExecucaoFalhou(ExecucaoOS execucao, String motivo, boolean requerRetrabalho) {
        ExecucaoFalhouEvent event = ExecucaoFalhouEvent.builder()
                .execucaoId(execucao.getId())
                .osId(execucao.getOsId())
                .motivo(motivo)
                .etapaFalha(execucao.getStatus() != null ? execucao.getStatus().name() : "DESCONHECIDA")
                .requerRetrabalho(requerRetrabalho)
                .timestamp(LocalDateTime.now())
                .build();

        publishExecucaoFalhou(event);
    }

    // ===================== MÉTODOS AUXILIARES =====================

    @SuppressWarnings("null")
    private void sendSync(ProducerRecord<String, Object> record, String eventType, String id) {
        try {
            SendResult<String, Object> result = kafkaTemplate.send(record).get();
            log.info("✅ Evento {} publicado (síncrono). ID: {}, Partition: {}, Offset: {}",
                    eventType, id,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("❌ ERRO CRÍTICO ao publicar evento {}: {}", eventType, e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento " + eventType, e);
        }
    }
}
