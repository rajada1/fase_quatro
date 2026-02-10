package br.com.grupo99.notificacao.application.service;

import br.com.grupo99.notificacao.adapter.event.EventPublishingService;
import br.com.grupo99.notificacao.adapter.repository.NotificacaoRepository;
import br.com.grupo99.notificacao.application.dto.NotificacaoRequestDTO;
import br.com.grupo99.notificacao.application.dto.NotificacaoResponseDTO;
import br.com.grupo99.notificacao.application.exception.ResourceNotFoundException;
import br.com.grupo99.notificacao.domain.Notificacao;
import br.com.grupo99.notificacao.domain.StatusNotificacao;
import br.com.grupo99.notificacao.domain.TipoNotificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificacaoApplicationServiceTest {
    @Mock
    private NotificacaoRepository repository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private NotificacaoApplicationService service;

    private UUID notificacaoId;
    private Notificacao notificacao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificacaoId = UUID.randomUUID();
        notificacao = Notificacao.builder()
                .id(notificacaoId)
                .destinatarioEmail("cliente@example.com")
                .tipoNotificacao(TipoNotificacao.CONFIRMACAO_MANUTENCAO)
                .assunto("Confirmação de Manutenção")
                .mensagem("Sua manutenção foi confirmada!")
                .status(StatusNotificacao.PENDENTE)
                .tentativas(0)
                .build();
        setCreatedAt(notificacao);
    }

    @Test
    void criarNotificacao_Success() {
        NotificacaoRequestDTO requestDTO = NotificacaoRequestDTO.builder()
                .destinatarioEmail("cliente@example.com")
                .tipoNotificacao("CONFIRMACAO_MANUTENCAO")
                .assunto("Confirmação de Manutenção")
                .mensagem("Sua manutenção foi confirmada!")
                .build();

        when(repository.save(any(Notificacao.class))).thenReturn(notificacao);

        NotificacaoResponseDTO result = service.criarNotificacao(requestDTO);

        assertNotNull(result);
        assertEquals("cliente@example.com", result.getDestinatarioEmail());
        assertEquals("CONFIRMACAO_MANUTENCAO", result.getTipoNotificacao());
        verify(repository).save(any(Notificacao.class));
        verify(eventPublishingService).publishNotificacaoPendente(any(), any(), any());
    }

    @Test
    void buscarPorId_Success() {
        when(repository.findById(notificacaoId)).thenReturn(Optional.of(notificacao));

        NotificacaoResponseDTO result = service.buscarPorId(notificacaoId);

        assertNotNull(result);
        assertEquals(notificacaoId, result.getId());
        verify(repository).findById(notificacaoId);
    }

    @Test
    void buscarPorId_NotFound() {
        when(repository.findById(notificacaoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(notificacaoId));
    }

    @Test
    void listarTodas_Success() {
        Notificacao notificacao2 = Notificacao.builder()
                .id(UUID.randomUUID())
                .destinatarioEmail("mecanico@example.com")
                .tipoNotificacao(TipoNotificacao.CONCLUSAO_MANUTENCAO)
                .assunto("Manutenção Concluída")
                .mensagem("A manutenção foi concluída!")
                .status(StatusNotificacao.PENDENTE)
                .tentativas(0)
                .build();
        setCreatedAt(notificacao2);

        when(repository.findAll()).thenReturn(Arrays.asList(notificacao, notificacao2));

        List<NotificacaoResponseDTO> result = service.listarTodas();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void listarPorStatus_Success() {
        when(repository.findByStatus(StatusNotificacao.PENDENTE)).thenReturn(Arrays.asList(notificacao));

        List<NotificacaoResponseDTO> result = service.listarPorStatus("PENDENTE");

        assertEquals(1, result.size());
        verify(repository).findByStatus(StatusNotificacao.PENDENTE);
    }

    @Test
    void listarPorEmail_Success() {
        when(repository.findByDestinatarioEmail("cliente@example.com")).thenReturn(Arrays.asList(notificacao));

        List<NotificacaoResponseDTO> result = service.listarPorEmail("cliente@example.com");

        assertEquals(1, result.size());
        verify(repository).findByDestinatarioEmail("cliente@example.com");
    }

    @Test
    void marcarComoEnviada_Success() {
        when(repository.findById(notificacaoId)).thenReturn(Optional.of(notificacao));
        when(repository.save(any(Notificacao.class))).thenReturn(notificacao);

        NotificacaoResponseDTO result = service.marcarComoEnviada(notificacaoId);

        assertNotNull(result);
        verify(repository).findById(notificacaoId);
        verify(repository).save(any(Notificacao.class));
        verify(eventPublishingService).publishNotificacaoEnviada(any());
    }

    @Test
    void marcarComoFalha_Success() {
        when(repository.findById(notificacaoId)).thenReturn(Optional.of(notificacao));
        when(repository.save(any(Notificacao.class))).thenReturn(notificacao);

        NotificacaoResponseDTO result = service.marcarComoFalha(notificacaoId);

        assertNotNull(result);
        verify(repository).findById(notificacaoId);
        verify(repository).save(any(Notificacao.class));
        verify(eventPublishingService).publishNotificacaoFalha(any());
    }

    private void setCreatedAt(Notificacao notificacao) {
        try {
            Field field = Notificacao.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(notificacao, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
