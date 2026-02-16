package br.com.grupo99.billingservice.domain.gateway;

import java.math.BigDecimal;

/**
 * Port (interface) para integração com gateway de pagamento externo.
 *
 * ✅ CLEAN ARCHITECTURE: Interface no domínio, implementação na infraestrutura.
 */
public interface MercadoPagoPort {

    /**
     * Cria um pagamento no Mercado Pago.
     *
     * @param descricao      descrição do pagamento (ex: "Pagamento OS #123")
     * @param valor          valor do pagamento
     * @param payerEmail     email do pagador
     * @param formaPagamento forma de pagamento (PIX, CARTAO_CREDITO, etc)
     * @return resultado com dados do pagamento criado no MP
     */
    MercadoPagoPaymentResult criarPagamento(String descricao, BigDecimal valor,
            String payerEmail, String formaPagamento);

    /**
     * Consulta o status de um pagamento no Mercado Pago.
     *
     * @param paymentId ID do pagamento no MP
     * @return resultado com status atualizado
     */
    MercadoPagoPaymentResult consultarPagamento(Long paymentId);

    /**
     * Resultado de uma operação no Mercado Pago.
     */
    record MercadoPagoPaymentResult(
            Long paymentId,
            String status,
            String statusDetail,
            String qrCode,
            String qrCodeBase64,
            String ticketUrl) {
    }
}
