package br.com.grupo99.billingservice.application.service;

import br.com.grupo99.billingservice.application.dto.CreatePagamentoRequest;
import br.com.grupo99.billingservice.application.dto.PagamentoResponse;
import br.com.grupo99.billingservice.application.mapper.PagamentoMapper;
import br.com.grupo99.billingservice.domain.gateway.MercadoPagoPort;
import br.com.grupo99.billingservice.domain.gateway.MercadoPagoPort.MercadoPagoPaymentResult;
import br.com.grupo99.billingservice.domain.model.Pagamento;
import br.com.grupo99.billingservice.domain.model.StatusPagamento;
import br.com.grupo99.billingservice.domain.repository.PagamentoRepository;
import br.com.grupo99.billingservice.infrastructure.messaging.BillingEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application Service para Pagamento
 * 
 * ✅ CLEAN ARCHITECTURE: Orquestração de use cases na camada application
 * ✅ MERCADO PAGO: Integração com gateway de pagamento externo
 */
@Slf4j
@Service
@Transactional
public class PagamentoApplicationService {

    private final PagamentoRepository pagamentoRepository;
    private final BillingEventPublisherPort eventPublisher;
    private final MercadoPagoPort mercadoPagoPort;
    private final PagamentoMapper mapper;

    public PagamentoApplicationService(
            PagamentoRepository pagamentoRepository,
            BillingEventPublisherPort eventPublisher,
            MercadoPagoPort mercadoPagoPort,
            PagamentoMapper mapper) {
        this.pagamentoRepository = pagamentoRepository;
        this.eventPublisher = eventPublisher;
        this.mercadoPagoPort = mercadoPagoPort;
        this.mapper = mapper;
    }

    /**
     * Use Case: Registrar Pagamento via Mercado Pago
     *
     * Fluxo:
     * 1. Cria o pagamento no domain (status PENDENTE)
     * 2. Envia para o Mercado Pago via SDK
     * 3. Atualiza com o ID do MP (status PROCESSANDO)
     * 4. Persiste no DynamoDB
     * 5. Publica evento via Kafka
     */
    public PagamentoResponse registrar(CreatePagamentoRequest request) {
        log.info("Registrando pagamento para orçamento: {}", request.getOrcamentoId());

        // 1. Domain: criar pagamento
        Pagamento pagamento = mapper.toDomain(request);

        // 2. Integração Mercado Pago
        String descricao = String.format("Pagamento OS - Orçamento %s", request.getOrcamentoId());
        String payerEmail = request.getPayerEmail() != null ? request.getPayerEmail() : "test@test.com";

        MercadoPagoPaymentResult mpResult = mercadoPagoPort.criarPagamento(
                descricao,
                request.getValor(),
                payerEmail,
                request.getFormaPagamento());

        // 3. Atualizar pagamento com dados do MP
        pagamento.processar(mpResult.paymentId());

        // Se o MP já aprovou (ex: PIX instantâneo em sandbox), confirmar
        if ("approved".equalsIgnoreCase(mpResult.status())) {
            pagamento.confirmar();
        }

        // 4. Persistência
        Pagamento saved = pagamentoRepository.save(pagamento);
        log.info("Pagamento registrado com ID: {}, MP ID: {}, Status MP: {}",
                saved.getId(), mpResult.paymentId(), mpResult.status());

        // 5. Events
        eventPublisher.publicarPagamentoRegistrado(saved);

        // 6. Response com dados do MP
        PagamentoResponse response = mapper.toResponse(saved);
        response.setQrCode(mpResult.qrCode());
        response.setTicketUrl(mpResult.ticketUrl());

        return response;
    }

    /**
     * Use Case: Processar Webhook do Mercado Pago
     *
     * Chamado quando o MP envia notificação de atualização de pagamento.
     */
    public void processarWebhook(Long mercadoPagoPaymentId) {
        log.info("Processando webhook do Mercado Pago para payment_id: {}", mercadoPagoPaymentId);

        // 1. Consultar status no MP
        MercadoPagoPaymentResult mpResult = mercadoPagoPort.consultarPagamento(mercadoPagoPaymentId);

        // 2. Buscar pagamento local pelo MP ID
        Pagamento pagamento = pagamentoRepository.findByMercadoPagoPaymentId(mercadoPagoPaymentId)
                .orElseThrow(() -> new RuntimeException(
                        "Pagamento não encontrado para MP ID: " + mercadoPagoPaymentId));

        // 3. Atualizar status baseado no retorno do MP
        String mpStatus = mpResult.status();
        log.info("Status MP: {} para pagamento local: {}", mpStatus, pagamento.getId());

        switch (mpStatus.toLowerCase()) {
            case "approved" -> {
                if (pagamento.getStatus() != StatusPagamento.CONFIRMADO) {
                    pagamento.confirmar();
                    pagamentoRepository.save(pagamento);
                    eventPublisher.publicarPagamentoConfirmado(pagamento);
                    log.info("Pagamento {} confirmado via webhook", pagamento.getId());
                }
            }
            case "rejected", "cancelled" -> {
                if (pagamento.getStatus() != StatusPagamento.CANCELADO) {
                    pagamento.cancelar();
                    pagamentoRepository.save(pagamento);
                    log.info("Pagamento {} cancelado via webhook (MP status: {})",
                            pagamento.getId(), mpStatus);
                }
            }
            case "refunded" -> {
                if (pagamento.getStatus() != StatusPagamento.ESTORNADO) {
                    pagamento.estornar("Estorno via Mercado Pago");
                    pagamentoRepository.save(pagamento);
                    eventPublisher.publicarPagamentoEstornado(pagamento);
                    log.info("Pagamento {} estornado via webhook", pagamento.getId());
                }
            }
            default -> log.info("Status MP '{}' não requer ação no pagamento {}", mpStatus, pagamento.getId());
        }
    }

    /**
     * Use Case: Confirmar Pagamento
     */
    public PagamentoResponse confirmar(UUID id) {
        log.info("Confirmando pagamento: {}", id);

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + id));

        // Lógica de domínio
        pagamento.confirmar();

        // Persistir
        Pagamento updated = pagamentoRepository.save(pagamento);

        // Publicar evento
        eventPublisher.publicarPagamentoConfirmado(updated);

        log.info("Pagamento {} confirmado", id);
        return mapper.toResponse(updated);
    }

    /**
     * Use Case: Estornar Pagamento
     */
    public PagamentoResponse estornar(UUID id, String motivo) {
        log.info("Estornando pagamento: {}, motivo: {}", id, motivo);

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + id));

        pagamento.estornar(motivo);
        Pagamento updated = pagamentoRepository.save(pagamento);

        eventPublisher.publicarPagamentoEstornado(updated);

        return mapper.toResponse(updated);
    }

    /**
     * Use Case: Cancelar Pagamento
     */
    public void cancelar(UUID id) {
        log.info("Cancelando pagamento: {}", id);

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + id));

        pagamento.cancelar();
        pagamentoRepository.save(pagamento);

        log.info("Pagamento {} cancelado", id);
    }
}
