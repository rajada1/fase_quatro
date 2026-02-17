package br.com.grupo99.executionservice.infrastructure.messaging;

import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import br.com.grupo99.executionservice.domain.repository.ExecucaoOSRepository;
import br.com.grupo99.executionservice.infrastructure.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import java.util.UUID;

/**
 * Consumidor de eventos Kafka para o Execution Service
 * 
 * Consome:
 * - os-events: OS_CRIADA → Cria registro de execução
 * - billing-events: ORCAMENTO_APROVADO → Inicia execução
 * 
 * Padrão: Saga Coreografada com Manual Acknowledgment
 */
@Slf4j
@Service
public class KafkaExecutionEventListener {

    private final ExecucaoOSRepository execucaoOSRepository;

    public KafkaExecutionEventListener(
            ExecucaoOSRepository execucaoOSRepository) {
        this.execucaoOSRepository = execucaoOSRepository;
    }

    /**
     * Consome eventos do tópico os-events
     * Saga Step 2.1: OS_CRIADA → Criar registro de execução
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_OS_EVENTS, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory", concurrency = "3")
    public void consumeOSEvents(
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        String eventType = extractHeader(record, "eventType");
        String osId = record.key();

        log.info("📥 Recebido evento Kafka do os-service. " +
                "Type: {}, OS ID: {}, Partition: {}, Offset: {}",
                eventType, osId, partition, offset);

        try {
            switch (eventType) {
                case "OS_CRIADA" -> handleOSCriada(record);
                case "STATUS_MUDADO" -> log.debug("Evento STATUS_MUDADO ignorado pelo execution-service");
                case "OS_CANCELADA" -> handleOSCancelada(record);
                default -> log.warn("⚠️ Tipo de evento desconhecido do os-events: {}", eventType);
            }

            acknowledgment.acknowledge();
            log.debug("✅ Evento {} commitado. Offset: {}", eventType, offset);

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento do os-events. Type: {}, OS ID: {}, Erro: {}",
                    eventType, osId, e.getMessage(), e);
            handleProcessingError(record, e, "os-events");
        }
    }

    /**
     * Consome eventos do tópico billing-events
     * Saga Step 5: ORCAMENTO_APROVADO → Iniciar execução
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_BILLING_EVENTS, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory", concurrency = "2")
    public void consumeBillingEvents(
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        String eventType = extractHeader(record, "eventType");
        String osId = record.key();

        log.info("📥 Recebido evento Kafka do billing-service. " +
                "Type: {}, OS ID: {}, Partition: {}, Offset: {}",
                eventType, osId, partition, offset);

        try {
            switch (eventType) {
                case "ORCAMENTO_APROVADO" -> handleOrcamentoAprovado(record);
                case "ORCAMENTO_REJEITADO" -> handleOrcamentoRejeitado(record);
                case "ORCAMENTO_PRONTO" -> log.debug("Evento ORCAMENTO_PRONTO ignorado pelo execution-service");
                default -> log.warn("⚠️ Tipo de evento desconhecido do billing-events: {}", eventType);
            }

            acknowledgment.acknowledge();
            log.debug("✅ Evento {} commitado. Offset: {}", eventType, offset);

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento do billing-events. Type: {}, OS ID: {}, Erro: {}",
                    eventType, osId, e.getMessage(), e);
            handleProcessingError(record, e, "billing-events");
        }
    }

    // ===================== HANDLERS =====================

    /**
     * Saga Step 2.1: OS criada → Criar registro de execução
     */
    private void handleOSCriada(ConsumerRecord<String, Object> record) {
        try {
            UUID osId = UUID.fromString(record.key());
            log.info("🔧 Processando OS_CRIADA. OS ID: {}", osId);

            // Idempotência: verificar se execução já existe
            if (execucaoOSRepository.findByOsId(osId).isPresent()) {
                log.warn("⚠️ Execução já existe para OS: {}. Evento duplicado ignorado.", osId);
                return;
            }

            // Criar registro de execução
            ExecucaoOS execucaoOS = ExecucaoOS.builder()
                    .id(UUID.randomUUID())
                    .osId(osId)
                    .status(StatusExecucao.AGUARDANDO_INICIO)
                    .mecanico("A definir")
                    .dataInicio(Instant.now())
                    .createdAt(Instant.now())
                    .build();

            execucaoOSRepository.save(execucaoOS);
            log.info("✅ Registro de execução criado. Execução ID: {}, OS ID: {}",
                    execucaoOS.getId(), osId);

        } catch (Exception e) {
            log.error("❌ Erro ao criar execução: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Saga Step 5: Orçamento aprovado → Iniciar execução
     */
    private void handleOrcamentoAprovado(ConsumerRecord<String, Object> record) {
        try {
            UUID osId = UUID.fromString(record.key());
            String orcamentoIdStr = extractHeader(record, "orcamentoId");
            UUID orcamentoId = orcamentoIdStr != null && !orcamentoIdStr.equals("UNKNOWN")
                    ? UUID.fromString(orcamentoIdStr)
                    : null;

            log.info("💰 Processando ORCAMENTO_APROVADO. OS ID: {}, Orçamento ID: {}", osId, orcamentoId);

            ExecucaoOS execucaoOS = execucaoOSRepository.findByOsId(osId)
                    .orElseThrow(() -> new RuntimeException("Execução não encontrada para OS: " + osId));

            // Atualiza execução com dados do orçamento aprovado
            execucaoOS.setOrcamentoId(orcamentoId);
            execucaoOS.setStatus(StatusExecucao.EM_ANDAMENTO);
            execucaoOS.setUpdatedAt(Instant.now());

            execucaoOSRepository.save(execucaoOS);
            log.info("✅ Execução iniciada. Execução ID: {}, OS ID: {}", execucaoOS.getId(), osId);

        } catch (Exception e) {
            log.error("❌ Erro ao iniciar execução: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Saga Compensação: Orçamento rejeitado → Cancelar execução
     */
    private void handleOrcamentoRejeitado(ConsumerRecord<String, Object> record) {
        try {
            UUID osId = UUID.fromString(record.key());
            String motivo = extractHeader(record, "motivo");

            log.warn("🔄 Processando ORCAMENTO_REJEITADO. OS ID: {}, Motivo: {}", osId, motivo);

            execucaoOSRepository.findByOsId(osId).ifPresent(execucaoOS -> {
                execucaoOS.setStatus(StatusExecucao.CANCELADA);
                execucaoOS.setObservacoes("Orçamento rejeitado: " + motivo);
                execucaoOS.setUpdatedAt(Instant.now());
                execucaoOSRepository.save(execucaoOS);
                log.info("✅ Execução cancelada por rejeição de orçamento. OS: {}", osId);
            });

        } catch (Exception e) {
            log.error("❌ Erro ao cancelar execução: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Saga Compensação: OS cancelada → Cancelar execução
     */
    private void handleOSCancelada(ConsumerRecord<String, Object> record) {
        try {
            UUID osId = UUID.fromString(record.key());
            String etapaFalha = extractHeader(record, "etapaFalha");

            log.warn("🔄 Processando OS_CANCELADA. OS ID: {}, Etapa: {}", osId, etapaFalha);

            execucaoOSRepository.findByOsId(osId).ifPresent(execucaoOS -> {
                execucaoOS.setStatus(StatusExecucao.CANCELADA);
                execucaoOS.setObservacoes("OS cancelada: " + etapaFalha);
                execucaoOS.setDataFinalizacao(Instant.now());
                execucaoOS.setUpdatedAt(Instant.now());
                execucaoOSRepository.save(execucaoOS);
                log.info("✅ Execução cancelada por cancelamento de OS. OS: {}", osId);
            });

        } catch (Exception e) {
            log.error("❌ Erro ao processar cancelamento de OS: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ===================== UTILITÁRIOS =====================

    private String extractHeader(ConsumerRecord<String, Object> record, String headerKey) {
        var header = record.headers().lastHeader(headerKey);
        if (header != null && header.value() != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return "UNKNOWN";
    }

    private void handleProcessingError(ConsumerRecord<String, Object> record, Exception e, String source) {
        log.error("🔴 Erro crítico no processamento de evento do {}. " +
                "Topic: {}, Partition: {}, Offset: {}, Key: {}, Erro: {}",
                source,
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                e.getMessage());
        // Re-throw para acionar o DefaultErrorHandler com DeadLetterPublishingRecoverer
        // (configurado em KafkaConfig)
        throw new RuntimeException(e);
    }
}
