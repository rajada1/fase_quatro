package br.com.grupo99.operacoes.application.service;

import br.com.grupo99.operacoes.adapter.event.EventPublishingService;
import br.com.grupo99.operacoes.adapter.repository.OperacaoRepository;
import br.com.grupo99.operacoes.application.dto.OperacaoRequestDTO;
import br.com.grupo99.operacoes.application.dto.OperacaoResponseDTO;
import br.com.grupo99.operacoes.application.exception.BusinessException;
import br.com.grupo99.operacoes.application.exception.ResourceNotFoundException;
import br.com.grupo99.operacoes.domain.Operacao;
import br.com.grupo99.operacoes.domain.PrioridadeOperacao;
import br.com.grupo99.operacoes.domain.StatusOperacao;
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

class OperacaoApplicationServiceTest {
    @Mock
    private OperacaoRepository repository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private OperacaoApplicationService service;

    private UUID operacaoId;
    private UUID orcamentoId;
    private Operacao operacao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        operacaoId = UUID.randomUUID();
        orcamentoId = UUID.randomUUID();
        operacao = Operacao.builder()
                .id(operacaoId)
                .numeroOperacao("OP-123456")
                .orcamentoId(orcamentoId)
                .descricao("Operação de manutenção completa")
                .status(StatusOperacao.PLANEJADA)
                .prioridade(PrioridadeOperacao.NORMAL)
                .tempoEstimadoHoras(8)
                .build();
        setCreatedAt(operacao);
    }

    @Test
    void criarOperacao_Success() {
        OperacaoRequestDTO requestDTO = OperacaoRequestDTO.builder()
                .orcamentoId(orcamentoId)
                .descricao("Operação de manutenção completa")
                .prioridade("NORMAL")
                .tempoEstimadoHoras(8)
                .build();

        when(repository.save(any(Operacao.class))).thenReturn(operacao);

        OperacaoResponseDTO result = service.criarOperacao(requestDTO);

        assertNotNull(result);
        assertEquals(orcamentoId, result.getOrcamentoId());
        assertEquals("NORMAL", result.getPrioridade());
        verify(repository).save(any(Operacao.class));
        verify(eventPublishingService).publishOperacaoCriada(any(), any());
    }

    @Test
    void buscarPorId_Success() {
        when(repository.findById(operacaoId)).thenReturn(Optional.of(operacao));

        OperacaoResponseDTO result = service.buscarPorId(operacaoId);

        assertNotNull(result);
        assertEquals(operacaoId, result.getId());
        verify(repository).findById(operacaoId);
    }

    @Test
    void buscarPorId_NotFound() {
        when(repository.findById(operacaoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(operacaoId));
    }

    @Test
    void listarTodas_Success() {
        Operacao operacao2 = Operacao.builder()
                .id(UUID.randomUUID())
                .numeroOperacao("OP-789012")
                .orcamentoId(orcamentoId)
                .descricao("Outra operação")
                .status(StatusOperacao.EXECUTANDO)
                .prioridade(PrioridadeOperacao.ALTA)
                .build();
        setCreatedAt(operacao2);

        when(repository.findAll()).thenReturn(Arrays.asList(operacao, operacao2));

        List<OperacaoResponseDTO> result = service.listarTodas();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void listarPorStatus_Success() {
        when(repository.findByStatus(StatusOperacao.PLANEJADA)).thenReturn(Arrays.asList(operacao));

        List<OperacaoResponseDTO> result = service.listarPorStatus("PLANEJADA");

        assertEquals(1, result.size());
        verify(repository).findByStatus(StatusOperacao.PLANEJADA);
    }

    @Test
    void iniciarOperacao_Success() {
        UUID responsavelId = UUID.randomUUID();
        when(repository.findById(operacaoId)).thenReturn(Optional.of(operacao));
        when(repository.save(any(Operacao.class))).thenReturn(operacao);

        OperacaoResponseDTO result = service.iniciarOperacao(operacaoId, responsavelId);

        assertNotNull(result);
        verify(repository).findById(operacaoId);
        verify(repository).save(any(Operacao.class));
        verify(eventPublishingService).publishOperacaoIniciada(any(), any());
    }

    @Test
    void pausarOperacao_Success() {
        Operacao operacaoEmExecucao = Operacao.builder()
                .id(operacaoId)
                .numeroOperacao("OP-123456")
                .orcamentoId(orcamentoId)
                .descricao("Operação")
                .status(StatusOperacao.EXECUTANDO)
                .prioridade(PrioridadeOperacao.NORMAL)
                .build();
        setCreatedAt(operacaoEmExecucao);

        when(repository.findById(operacaoId)).thenReturn(Optional.of(operacaoEmExecucao));
        when(repository.save(any(Operacao.class))).thenReturn(operacaoEmExecucao);

        OperacaoResponseDTO result = service.pausarOperacao(operacaoId);

        assertNotNull(result);
        verify(repository).findById(operacaoId);
        verify(repository).save(any(Operacao.class));
    }

    private void setCreatedAt(Operacao operacao) {
        try {
            Field field = Operacao.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(operacao, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
