package br.com.grupo99.billingservice.infrastructure.controller;

import br.com.grupo99.billingservice.application.dto.CreatePagamentoRequest;
import br.com.grupo99.billingservice.application.dto.PagamentoResponse;
import br.com.grupo99.billingservice.application.service.PagamentoApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller para Pagamento
 * 
 * ✅ CLEAN ARCHITECTURE: Controller chama Application Service
 * ✅ MERCADO PAGO: Endpoint de webhook para notificações
 */
@Slf4j
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoApplicationService service;

    public PagamentoController(PagamentoApplicationService service) {
        this.service = service;
    }

    /**
     * POST /api/v1/pagamentos
     * Registrar novo pagamento (cria no Mercado Pago)
     */
    @PostMapping
    public ResponseEntity<PagamentoResponse> registrar(
            @RequestBody CreatePagamentoRequest request) {
        log.info("POST /pagamentos - registrando pagamento para orçamento: {}", request.getOrcamentoId());

        try {
            PagamentoResponse response = service.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao registrar pagamento", e);
            throw e;
        }
    }

    /**
     * POST /api/v1/pagamentos/webhook
     * Webhook do Mercado Pago (IPN - Instant Payment Notification)
     *
     * Endpoint público (sem JWT) para receber notificações do MP.
     * O MP envia:
     * {"action":"payment.updated","data":{"id":"123456"},"type":"payment"}
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook recebido do Mercado Pago: {}", payload);

        try {
            String type = (String) payload.get("type");

            if ("payment".equalsIgnoreCase(type)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data != null && data.get("id") != null) {
                    Long paymentId = Long.valueOf(data.get("id").toString());
                    service.processarWebhook(paymentId);
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Mercado Pago", e);
            // Retorna 200 para o MP não reenviar (evitar loop)
            return ResponseEntity.ok().build();
        }
    }

    /**
     * PUT /api/v1/pagamentos/{id}/confirmar
     * Confirmar pagamento manualmente
     */
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<PagamentoResponse> confirmar(
            @PathVariable UUID id) {
        log.info("PUT /pagamentos/{}/confirmar - confirmando pagamento", id);

        try {
            PagamentoResponse response = service.confirmar(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao confirmar pagamento", e);
            throw e;
        }
    }

    /**
     * PUT /api/v1/pagamentos/{id}/estornar
     * Estornar pagamento
     */
    @PutMapping("/{id}/estornar")
    public ResponseEntity<PagamentoResponse> estornar(
            @PathVariable UUID id,
            @RequestParam(required = false) String motivo) {
        log.info("PUT /pagamentos/{}/estornar - estornando pagamento", id);

        try {
            PagamentoResponse response = service.estornar(id, motivo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao estornar pagamento", e);
            throw e;
        }
    }

    /**
     * DELETE /api/v1/pagamentos/{id}
     * Cancelar pagamento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable UUID id) {
        log.info("DELETE /pagamentos/{} - cancelando pagamento", id);

        try {
            service.cancelar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao cancelar pagamento", e);
            throw e;
        }
    }
}
