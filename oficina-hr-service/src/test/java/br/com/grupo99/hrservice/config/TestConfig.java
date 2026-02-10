package br.com.grupo99.hrservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Configuração para testes.
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public SqsClient sqsClient() {
        return Mockito.mock(SqsClient.class);
    }
}
