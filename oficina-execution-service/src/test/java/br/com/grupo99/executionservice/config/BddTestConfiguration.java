package br.com.grupo99.executionservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test configuration for BDD tests
 */
@TestConfiguration
@ActiveProfiles("test")
public class BddTestConfiguration {
    // Configuração limpa - sem mocks legacy
}
