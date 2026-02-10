package br.com.grupo99.customerservice.adapter.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Configuração para testes que fornece mocks de dependências externas.
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public SqsTemplate sqsTemplate() {
        return mock(SqsTemplate.class);
    }
}
