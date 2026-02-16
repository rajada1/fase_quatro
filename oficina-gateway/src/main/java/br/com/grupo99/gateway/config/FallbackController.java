package br.com.grupo99.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{serviceName}")
    public Mono<Map<String, Object>> fallback(
            @PathVariable String serviceName,
            ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.just(Map.of(
                "status", 503,
                "error", "Service Unavailable",
                "message", String.format("O serviço '%s' está temporariamente indisponível. Tente novamente em alguns instantes.", serviceName),
                "service", serviceName,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
