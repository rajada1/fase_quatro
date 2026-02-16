package br.com.grupo99.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // OS Service - Ordem de Serviço (porta 8081)
                .route("os-service", r -> r
                        .path("/api/v1/ordens-servico/**", "/api/ordens/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("osServiceCB")
                                        .setFallbackUri("forward:/fallback/os-service"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("${services.os-service.url}"))

                // Billing Service - Orçamento e Pagamento (porta 8082)
                .route("billing-service", r -> r
                        .path("/api/v1/orcamentos/**", "/api/v1/pagamentos/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("billingServiceCB")
                                        .setFallbackUri("forward:/fallback/billing-service"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("${services.billing-service.url}"))

                // Execution Service - Execução e Diagnósticos (porta 8083)
                .route("execution-service", r -> r
                        .path("/api/v1/execucoes/**", "/api/v1/diagnosticos/**", "/api/v1/tarefas/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("executionServiceCB")
                                        .setFallbackUri("forward:/fallback/execution-service"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("${services.execution-service.url}"))

                // Customer Service (porta 8084)
                .route("customer-service", r -> r
                        .path("/api/v1/clientes/**", "/api/v1/veiculos/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("customerServiceCB")
                                        .setFallbackUri("forward:/fallback/customer-service")))
                        .uri("${services.customer-service.url}"))

                // Catalog Service - Peças e Serviços (porta 8085)
                .route("catalog-service", r -> r
                        .path("/api/v1/pecas/**", "/api/v1/servicos/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("catalogServiceCB")
                                        .setFallbackUri("forward:/fallback/catalog-service")))
                        .uri("${services.catalog-service.url}"))

                // People Service (porta 8086)
                .route("people-service", r -> r
                        .path("/api/v1/pessoas/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("peopleServiceCB")
                                        .setFallbackUri("forward:/fallback/people-service")))
                        .uri("${services.people-service.url}"))

                // HR Service (porta 8087)
                .route("hr-service", r -> r
                        .path("/api/v1/funcionarios/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("hrServiceCB")
                                        .setFallbackUri("forward:/fallback/hr-service")))
                        .uri("${services.hr-service.url}"))

                // Maintenance Service (porta 8088)
                .route("maintenance-service", r -> r
                        .path("/api/v1/manutencoes/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("maintenanceServiceCB")
                                        .setFallbackUri("forward:/fallback/maintenance-service")))
                        .uri("${services.maintenance-service.url}"))

                // Notification Service (porta 8089)
                .route("notification-service", r -> r
                        .path("/api/v1/notificacoes/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("notificationServiceCB")
                                        .setFallbackUri("forward:/fallback/notification-service")))
                        .uri("${services.notification-service.url}"))

                // Operations Service (porta 8090)
                .route("operations-service", r -> r
                        .path("/api/v1/operacoes/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("operationsServiceCB")
                                        .setFallbackUri("forward:/fallback/operations-service")))
                        .uri("${services.operations-service.url}"))

                .build();
    }
}
