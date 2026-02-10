package br.com.grupo99.executionservice.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Apache Kafka para o Execution Service
 * Inclui Dead Letter Topics e retry com backoff exponencial.
 * 
 * Consome:
 * - os-events: OS_CRIADA (criar registro de execução)
 * - billing-events: ORCAMENTO_APROVADO (iniciar execução)
 * 
 * Publica:
 * - execution-events: DIAGNOSTICO_CONCLUIDO, EXECUCAO_CONCLUIDA,
 * EXECUCAO_FALHOU
 */
@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    // Nomes dos tópicos
    public static final String TOPIC_OS_EVENTS = "os-events";
    public static final String TOPIC_BILLING_EVENTS = "billing-events";
    public static final String TOPIC_EXECUTION_EVENTS = "execution-events";

    // Dead Letter Topics
    public static final String DLT_OS_EVENTS = "os-events.DLT";
    public static final String DLT_BILLING_EVENTS = "billing-events.DLT";
    public static final String DLT_EXECUTION_EVENTS = "execution-events.DLT";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // ===================== PRODUCER CONFIGURATION =====================

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Garantias de entrega
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ===================== CONSUMER CONFIGURATION =====================

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Configuração do JsonDeserializer
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Map.class);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);

        // Manual Acknowledgment para garantir processamento
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // Error handler com DLT e backoff exponencial
        factory.setCommonErrorHandler(kafkaErrorHandler());

        return factory;
    }

    /**
     * Error Handler com Dead Letter Topic e Exponential Backoff
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate(),
                (record, ex) -> {
                    String dltTopic = record.topic() + ".DLT";
                    log.error("🔴 Enviando mensagem para DLT: {}. Erro: {}", dltTopic, ex.getMessage());
                    return new org.apache.kafka.common.TopicPartition(dltTopic, record.partition());
                });

        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(30000L);
        backOff.setMaxInterval(16000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(
                org.apache.kafka.common.errors.SerializationException.class,
                org.springframework.messaging.converter.MessageConversionException.class);

        return errorHandler;
    }

    // ===================== TOPIC CONFIGURATION =====================

    /**
     * Tópico execution-events (publicado pelo execution-service)
     * - DIAGNOSTICO_CONCLUIDO
     * - EXECUCAO_CONCLUIDA
     * - EXECUCAO_FALHOU
     */
    @Bean
    public NewTopic executionEventsTopic() {
        return TopicBuilder.name(TOPIC_EXECUTION_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "2592000000") // 30 dias
                .build();
    }

    // ===================== DEAD LETTER TOPICS =====================

    @Bean
    public NewTopic osEventsDltTopic() {
        return TopicBuilder.name(DLT_OS_EVENTS)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "604800000") // 7 dias
                .build();
    }

    @Bean
    public NewTopic billingEventsDltTopic() {
        return TopicBuilder.name(DLT_BILLING_EVENTS)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "604800000")
                .build();
    }

    @Bean
    public NewTopic executionEventsDltTopic() {
        return TopicBuilder.name(DLT_EXECUTION_EVENTS)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "604800000")
                .build();
    }
}
