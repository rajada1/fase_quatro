package br.com.grupo99.executionservice.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de métricas Kafka para o Execution Service.
 * Integra com Prometheus/Micrometer para dashboards New Relic/Grafana.
 * 
 * Métricas expostas:
 * - kafka.publisher.events.total: Total de eventos publicados por tipo
 * - kafka.publisher.events.failed: Total de falhas por tipo de evento
 * - kafka.publisher.latency: Latência de publicação
 * - kafka.circuitbreaker.state: Estado do Circuit Breaker
 */
@Slf4j
@Configuration
public class KafkaMetricsConfig {

    private final MeterRegistry meterRegistry;

    public KafkaMetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        log.info("📊 Kafka Metrics configurado para monitoramento - Execution Service");
    }

    // ===================== CONTADORES DE EVENTOS =====================

    @Bean
    public Counter executionEventosPublicados() {
        return Counter.builder("kafka.publisher.events.total")
                .description("Total de eventos Execution publicados com sucesso")
                .tag("service", "execution-service")
                .tag("topic", KafkaConfig.TOPIC_EXECUTION_EVENTS)
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter executionEventosFalhos() {
        return Counter.builder("kafka.publisher.events.failed")
                .description("Total de eventos Execution que falharam na publicação")
                .tag("service", "execution-service")
                .tag("topic", KafkaConfig.TOPIC_EXECUTION_EVENTS)
                .tag("status", "failed")
                .register(meterRegistry);
    }

    @Bean
    public Counter executionDltEventosEnviados() {
        return Counter.builder("kafka.dlt.events.total")
                .description("Total de eventos enviados para Dead Letter Topics")
                .tag("service", "execution-service")
                .tag("topic", "dlt")
                .register(meterRegistry);
    }

    // ===================== CONTADORES POR TIPO DE EVENTO =====================

    @Bean
    public Counter diagnosticoConcluidoCounter() {
        return Counter.builder("kafka.publisher.events.by_type")
                .description("Eventos por tipo")
                .tag("service", "execution-service")
                .tag("event_type", "DIAGNOSTICO_CONCLUIDO")
                .tag("saga_action", "step")
                .register(meterRegistry);
    }

    @Bean
    public Counter execucaoConcluidaCounter() {
        return Counter.builder("kafka.publisher.events.by_type")
                .description("Eventos por tipo - Final da Saga")
                .tag("service", "execution-service")
                .tag("event_type", "EXECUCAO_CONCLUIDA")
                .tag("saga_action", "completion")
                .register(meterRegistry);
    }

    @Bean
    public Counter execucaoFalhouCounter() {
        return Counter.builder("kafka.publisher.events.by_type")
                .description("Eventos por tipo - Compensação")
                .tag("service", "execution-service")
                .tag("event_type", "EXECUCAO_FALHOU")
                .tag("saga_action", "compensation")
                .register(meterRegistry);
    }

    // ===================== TIMERS DE LATÊNCIA =====================

    @Bean
    public Timer executionKafkaPublishLatency() {
        return Timer.builder("kafka.publisher.latency")
                .description("Latência de publicação de eventos Kafka")
                .tag("service", "execution-service")
                .tag("topic", KafkaConfig.TOPIC_EXECUTION_EVENTS)
                .publishPercentileHistogram(true)
                .register(meterRegistry);
    }

    @Bean
    public Timer executionSagaLatency() {
        return Timer.builder("saga.step.latency")
                .description("Latência de cada etapa da Saga")
                .tag("service", "execution-service")
                .publishPercentileHistogram(true)
                .register(meterRegistry);
    }

    // ===================== MÉTRICAS DE CIRCUIT BREAKER =====================

    @Bean
    public Counter executionCircuitBreakerOpenCounter() {
        return Counter.builder("kafka.circuitbreaker.opened")
                .description("Quantidade de vezes que o Circuit Breaker abriu")
                .tag("service", "execution-service")
                .tag("circuit_breaker", "kafkaPublisher")
                .register(meterRegistry);
    }

    @Bean
    public Counter executionCircuitBreakerFallbackCounter() {
        return Counter.builder("kafka.circuitbreaker.fallback")
                .description("Quantidade de chamadas ao fallback")
                .tag("service", "execution-service")
                .tag("circuit_breaker", "kafkaPublisher")
                .register(meterRegistry);
    }

    // ===================== MÉTRICAS DE CONSUMER =====================

    @Bean
    public Counter executionEventosConsumidos() {
        return Counter.builder("kafka.consumer.events.total")
                .description("Total de eventos consumidos")
                .tag("service", "execution-service")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter executionEventosConsumidosErro() {
        return Counter.builder("kafka.consumer.events.failed")
                .description("Total de eventos consumidos com erro")
                .tag("service", "execution-service")
                .tag("status", "failed")
                .register(meterRegistry);
    }

    @Bean
    public Timer executionKafkaConsumeLatency() {
        return Timer.builder("kafka.consumer.latency")
                .description("Latência de processamento de eventos consumidos")
                .tag("service", "execution-service")
                .publishPercentileHistogram(true)
                .register(meterRegistry);
    }
}
