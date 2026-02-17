package br.com.grupo99.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${services.os-service.url}")
    private String osServiceUrl;

    @Value("${services.billing-service.url}")
    private String billingServiceUrl;

    @Value("${services.execution-service.url}")
    private String executionServiceUrl;

    @Value("${services.customer-service.url}")
    private String customerServiceUrl;

    @Value("${services.catalog-service.url}")
    private String catalogServiceUrl;

    @Value("${services.people-service.url}")
    private String peopleServiceUrl;

    @Value("${services.hr-service.url}")
    private String hrServiceUrl;

    @Value("${services.maintenance-service.url}")
    private String maintenanceServiceUrl;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    @Value("${services.operations-service.url}")
    private String operationsServiceUrl;

    private static final String DEDUPE_CORS_HEADERS =
            "Access-Control-Allow-Origin Access-Control-Allow-Methods Access-Control-Allow-Headers Access-Control-Allow-Credentials Access-Control-Max-Age";

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // OS Service - Ordem de Serviço (porta 8081)
                .route("os-service", r -> r
                        .path("/api/v1/ordens-servico/**", "/api/ordens/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("osServiceCB")
                                        .setFallbackUri("forward:/fallback/os-service"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri(osServiceUrl))

                // Billing Service - Orçamento e Pagamento (porta 8082)
                .route("billing-service", r -> r
                        .path("/api/v1/orcamentos/**", "/api/v1/pagamentos/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("billingServiceCB")
                                        .setFallbackUri("forward:/fallback/billing-service"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri(billingServiceUrl))

                // Execution Service - Execução e Diagnósticos (porta 8083)
                .route("execution-service", r -> r
                        .path("/api/v1/execucoes/**", "/api/v1/diagnosticos/**", "/api/v1/tarefas/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("executionServiceCB")
                                        .setFallbackUri("forward:/fallback/execution-service"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri(executionServiceUrl))

                // Customer Service (porta 8084)
                .route("customer-service", r -> r
                        .path("/api/v1/clientes/**", "/api/v1/veiculos/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("customerServiceCB")
                                        .setFallbackUri("forward:/fallback/customer-service")))
                        .uri(customerServiceUrl))

                // Catalog Service - Peças e Serviços (porta 8085)
                .route("catalog-service", r -> r
                        .path("/api/v1/pecas/**", "/api/v1/servicos/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("catalogServiceCB")
                                        .setFallbackUri("forward:/fallback/catalog-service")))
                        .uri(catalogServiceUrl))

                // People Service (porta 8086)
                .route("people-service", r -> r
                        .path("/api/v1/pessoas/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("peopleServiceCB")
                                        .setFallbackUri("forward:/fallback/people-service")))
                        .uri(peopleServiceUrl))

                // HR Service (porta 8087)
                .route("hr-service", r -> r
                        .path("/api/v1/funcionarios/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("hrServiceCB")
                                        .setFallbackUri("forward:/fallback/hr-service")))
                        .uri(hrServiceUrl))

                // Maintenance Service (porta 8088)
                .route("maintenance-service", r -> r
                        .path("/api/v1/manutencoes/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("maintenanceServiceCB")
                                        .setFallbackUri("forward:/fallback/maintenance-service")))
                        .uri(maintenanceServiceUrl))

                // Notification Service (porta 8089)
                .route("notification-service", r -> r
                        .path("/api/v1/notificacoes/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("notificationServiceCB")
                                        .setFallbackUri("forward:/fallback/notification-service")))
                        .uri(notificationServiceUrl))

                // Operations Service (porta 8090)
                .route("operations-service", r -> r
                        .path("/api/v1/operacoes/**")
                        .filters(f -> f
                                .dedupeResponseHeader(DEDUPE_CORS_HEADERS, "RETAIN_FIRST")
                                .circuitBreaker(c -> c
                                        .setName("operationsServiceCB")
                                        .setFallbackUri("forward:/fallback/operations-service")))
                        .uri(operationsServiceUrl))

                .build();
    }
}
