package br.com.grupo99.manutencao.application.service;

import br.com.grupo99.manutencao.adapter.event.EventPublishingService;
import br.com.grupo99.manutencao.adapter.repository.ManutencaoRepository;
import br.com.grupo99.manutencao.application.dto.ManutencaoRequestDTO;
import br.com.grupo99.manutencao.application.dto.ManutencaoResponseDTO;
import br.com.grupo99.manutencao.application.exception.ResourceNotFoundException;
import br.com.grupo99.manutencao.domain.Manutencao;
import br.com.grupo99.manutencao.domain.StatusManutencao;
import br.com.grupo99.manutencao.domain.TipoManutencao;
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

class ManutencaoApplicationServiceTest {
    @Mock
    private ManutencaoRepository repository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private ManutencaoApplicationService service;

    private UUID veiculoId;
    private UUID manutencaoId;
    private Manutencao manutencao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        veiculoId = UUID.randomUUID();
        manutencaoId = UUID.randomUUID();
        manutencao = Manutencao.builder()
                .id(manutencaoId)
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(1))
                .tipoManutencao(TipoManutencao.PREVENCAO)
                .descricao("Manutenção preventiva completa")
                .status(StatusManutencao.AGENDADA)
                .build();
        setCreatedAt(manutencao);
    }

    @Test
    void criarManutencao_Success() {
        ManutencaoRequestDTO requestDTO = ManutencaoRequestDTO.builder()
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(1))
                .tipoManutencao("PREVENCAO")
                .descricao("Manutenção preventiva completa")
                .build();

        when(repository.save(any(Manutencao.class))).thenReturn(manutencao);

        ManutencaoResponseDTO result = service.criar(requestDTO);

        assertNotNull(result);
        assertEquals(veiculoId, result.getVeiculoId());
        assertEquals("PREVENCAO", result.getTipoManutencao());
        verify(repository).save(any(Manutencao.class));
        verify(eventPublishingService).publishManutencaoCriada(any(), any());
    }

    @Test
    void buscarPorId_Success() {
        when(repository.findById(manutencaoId)).thenReturn(Optional.of(manutencao));

        ManutencaoResponseDTO result = service.buscarPorId(manutencaoId);

        assertNotNull(result);
        assertEquals(manutencaoId, result.getId());
        verify(repository).findById(manutencaoId);
    }

    @Test
    void buscarPorId_NotFound() {
        when(repository.findById(manutencaoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(manutencaoId));
    }

    @Test
    void listarTodas_Success() {
        Manutencao manutencao2 = Manutencao.builder()
                .id(UUID.randomUUID())
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(2))
                .tipoManutencao(TipoManutencao.CORRETIVA)
                .descricao("Conserto de motor")
                .status(StatusManutencao.AGENDADA)
                .build();
        setCreatedAt(manutencao2);

        when(repository.findAll()).thenReturn(Arrays.asList(manutencao, manutencao2));

        List<ManutencaoResponseDTO> result = service.listarTodas();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void listarPorVeiculo_Success() {
        when(repository.findByVeiculoId(veiculoId)).thenReturn(Arrays.asList(manutencao));

        List<ManutencaoResponseDTO> result = service.listarPorVeiculo(veiculoId);

        assertEquals(1, result.size());
        verify(repository).findByVeiculoId(veiculoId);
    }

    @Test
    void atualizar_Success() {
        ManutencaoRequestDTO requestDTO = ManutencaoRequestDTO.builder()
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(2))
                .tipoManutencao("CORRETIVA")
                .descricao("Manutenção corretiva")
                .build();

        when(repository.findById(manutencaoId)).thenReturn(Optional.of(manutencao));
        when(repository.save(any(Manutencao.class))).thenReturn(manutencao);

        ManutencaoResponseDTO result = service.atualizar(manutencaoId, requestDTO);

        assertNotNull(result);
        verify(repository).findById(manutencaoId);
        verify(repository).save(any(Manutencao.class));
    }

    @Test
    void iniciarManutencao_Success() {
        when(repository.findById(manutencaoId)).thenReturn(Optional.of(manutencao));
        when(repository.save(any(Manutencao.class))).thenReturn(manutencao);

        ManutencaoResponseDTO result = service.iniciarManutencao(manutencaoId);

        assertNotNull(result);
        verify(repository).findById(manutencaoId);
        verify(repository).save(any(Manutencao.class));
    }

    private void setCreatedAt(Manutencao manutencao) {
        try {
            Field field = Manutencao.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(manutencao, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
