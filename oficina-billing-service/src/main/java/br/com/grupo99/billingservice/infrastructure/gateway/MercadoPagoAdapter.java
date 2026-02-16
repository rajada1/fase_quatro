package br.com.grupo99.billingservice.infrastructure.gateway;

import br.com.grupo99.billingservice.domain.gateway.MercadoPagoPort;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adapter que implementa MercadoPagoPort usando o SDK oficial do Mercado Pago.
 *
 * ✅ CLEAN ARCHITECTURE: Implementação de infraestrutura do Port de domínio.
 */
@Slf4j
@Component
public class MercadoPagoAdapter implements MercadoPagoPort {

    private final PaymentClient paymentClient;

    public MercadoPagoAdapter(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public MercadoPagoPaymentResult criarPagamento(String descricao, BigDecimal valor,
            String payerEmail, String formaPagamento) {
        log.info("Criando pagamento no Mercado Pago: descricao={}, valor={}, forma={}",
                descricao, valor, formaPagamento);

        try {
            PaymentCreateRequest request = PaymentCreateRequest.builder()
                    .description(descricao)
                    .transactionAmount(valor)
                    .paymentMethodId(mapFormaPagamento(formaPagamento))
                    .payer(PaymentPayerRequest.builder()
                            .email(payerEmail)
                            .build())
                    .build();

            Payment payment = paymentClient.create(request);

            log.info("Pagamento criado no Mercado Pago: id={}, status={}, statusDetail={}",
                    payment.getId(), payment.getStatus(), payment.getStatusDetail());

            String qrCode = null;
            String qrCodeBase64 = null;
            String ticketUrl = null;

            if (payment.getPointOfInteraction() != null
                    && payment.getPointOfInteraction().getTransactionData() != null) {
                qrCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
                qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
                ticketUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();
            }

            return new MercadoPagoPaymentResult(
                    payment.getId(),
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    qrCode,
                    qrCodeBase64,
                    ticketUrl);

        } catch (MPApiException e) {
            log.error("Erro na API do Mercado Pago: statusCode={}, content={}",
                    e.getStatusCode(), e.getApiResponse() != null ? e.getApiResponse().getContent() : "N/A", e);
            throw new RuntimeException("Erro ao criar pagamento no Mercado Pago: " + e.getMessage(), e);
        } catch (MPException e) {
            log.error("Erro no SDK do Mercado Pago", e);
            throw new RuntimeException("Erro ao criar pagamento no Mercado Pago: " + e.getMessage(), e);
        }
    }

    @Override
    public MercadoPagoPaymentResult consultarPagamento(Long paymentId) {
        log.info("Consultando pagamento no Mercado Pago: id={}", paymentId);

        try {
            Payment payment = paymentClient.get(paymentId);

            log.info("Pagamento consultado: id={}, status={}", payment.getId(), payment.getStatus());

            return new MercadoPagoPaymentResult(
                    payment.getId(),
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    null, null, null);

        } catch (MPApiException e) {
            log.error("Erro ao consultar pagamento no Mercado Pago: statusCode={}",
                    e.getStatusCode(), e);
            throw new RuntimeException("Erro ao consultar pagamento no Mercado Pago: " + e.getMessage(), e);
        } catch (MPException e) {
            log.error("Erro no SDK do Mercado Pago", e);
            throw new RuntimeException("Erro ao consultar pagamento no Mercado Pago: " + e.getMessage(), e);
        }
    }

    /**
     * Mapeia FormaPagamento interna para payment_method_id do Mercado Pago.
     */
    private String mapFormaPagamento(String formaPagamento) {
        if (formaPagamento == null) {
            return "pix"; // default
        }
        return switch (formaPagamento.toUpperCase()) {
            case "PIX" -> "pix";
            case "CARTAO_CREDITO" -> "master"; // Visa/Master aceitos pelo MP
            case "CARTAO_DEBITO" -> "debmaster";
            case "BOLETO" -> "bolbradesco";
            default -> "pix";
        };
    }
}
