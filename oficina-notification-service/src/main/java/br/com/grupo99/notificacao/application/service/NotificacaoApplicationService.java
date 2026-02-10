package br.com.grupo99.notificacao.application.service;

import br.com.grupo99.notificacao.application.dto.NotificacaoRequestDTO;
import br.com.grupo99.notificacao.application.dto.NotificacaoResponseDTO;
import br.com.grupo99.notificacao.application.exception.ResourceNotFoundException;
import br.com.grupo99.notificacao.adapter.repository.NotificacaoRepository;
import br.com.grupo99.notificacao.adapter.event.EventPublishingService;
import br.com.grupo99.notificacao.domain.Notificacao;
import br.com.grupo99.notificacao.domain.StatusNotificacao;
import br.com.grupo99.notificacao.domain.TipoNotificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoApplicationService {
    private final NotificacaoRepository notificacaoRepository;
    private final EventPublishingService eventPublishingService;

    @Transactional
    public NotificacaoResponseDTO criarNotificacao(NotificacaoRequestDTO requestDTO) {
        log.info("Criando notificação para: {}", requestDTO.getDestinatarioEmail());

        Notificacao notificacao = Notificacao.builder()
                .destinatarioEmail(requestDTO.getDestinatarioEmail())
                .tipoNotificacao(TipoNotificacao.valueOf(requestDTO.getTipoNotificacao()))
                .assunto(requestDTO.getAssunto())
                .mensagem(requestDTO.getMensagem())
                .build();

        Notificacao saved = notificacaoRepository.save(notificacao);
        eventPublishingService.publishNotificacaoPendente(
                saved.getId().toString(),
                saved.getDestinatarioEmail(),
                saved.getTipoNotificacao().name());

        log.info("Notificação criada com sucesso: {}", saved.getId());
        return NotificacaoResponseDTO.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public NotificacaoResponseDTO buscarPorId(UUID id) {
        log.info("Buscando notificação: {}", id);
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada: " + id));
        return NotificacaoResponseDTO.fromDomain(notificacao);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listarTodas() {
        log.info("Listando todas as notificações");
        return notificacaoRepository.findAll().stream()
                .map(NotificacaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listarPorStatus(String status) {
        log.info("Listando notificações com status: {}", status);
        return notificacaoRepository.findByStatus(StatusNotificacao.valueOf(status)).stream()
                .map(NotificacaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listarPorEmail(String email) {
        log.info("Listando notificações do email: {}", email);
        return notificacaoRepository.findByDestinatarioEmail(email).stream()
                .map(NotificacaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificacaoResponseDTO marcarComoEnviada(UUID id) {
        log.info("Marcando notificação como enviada: {}", id);
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada: " + id));

        notificacao.setStatus(StatusNotificacao.ENVIADA);
        notificacao.setDataEnvio(LocalDateTime.now());
        Notificacao updated = notificacaoRepository.save(notificacao);

        eventPublishingService.publishNotificacaoEnviada(updated.getId().toString());
        log.info("Notificação marcada como enviada: {}", id);
        return NotificacaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public NotificacaoResponseDTO marcarComoFalha(UUID id) {
        log.info("Marcando notificação com falha: {}", id);
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada: " + id));

        notificacao.setStatus(StatusNotificacao.FALHA);
        notificacao.setTentativas(notificacao.getTentativas() + 1);
        Notificacao updated = notificacaoRepository.save(notificacao);

        eventPublishingService.publishNotificacaoFalha(updated.getId().toString());
        log.info("Notificação marcada com falha: {}", id);
        return NotificacaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public void deletar(UUID id) {
        log.info("Deletando notificação: {}", id);
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada: " + id));

        notificacaoRepository.deleteById(id);
        log.info("Notificação deletada com sucesso: {}", id);
    }
}
