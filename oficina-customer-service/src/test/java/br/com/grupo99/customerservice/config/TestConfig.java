package br.com.grupo99.customerservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.sqs.SqsClient;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public SqsClient sqsClient() {
        return Mockito.mock(SqsClient.class);
    }
}
