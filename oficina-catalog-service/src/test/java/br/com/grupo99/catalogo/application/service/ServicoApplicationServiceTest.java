package br.com.grupo99.catalogo.application.service;

import br.com.grupo99.catalogo.application.dto.ServicoRequestDTO;
import br.com.grupo99.catalogo.application.dto.ServicoResponseDTO;
import br.com.grupo99.catalogo.domain.model.Servico;
import br.com.grupo99.catalogo.domain.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes atualizados para MongoDB - ID agora é String.
 */
public class ServicoApplicationServiceTest {
    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ServicoApplicationService servicoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarServico_Success() {
        ServicoRequestDTO request = new ServicoRequestDTO("Troca de Óleo", "Troca completa de óleo",
                new BigDecimal("150.00"), 30);
        Servico servico = new Servico(request.getNome(), request.getDescricao(), request.getPreco(),
                request.getTempoEstimadoMinutos());
        String id = UUID.randomUUID().toString();
        servico.setId(id);

        when(servicoRepository.save(any(Servico.class))).thenReturn(servico);

        ServicoResponseDTO response = servicoService.criarServico(request);

        assertNotNull(response);
        assertEquals(servico.getId(), response.getId());
        assertEquals("Troca de Óleo", response.getNome());
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    void buscarPorId_Success() {
        String id = UUID.randomUUID().toString();
        Servico servico = new Servico("Troca de Óleo", "Troca completa", new BigDecimal("150.00"), 30);
        servico.setId(id);

        when(servicoRepository.findById(id)).thenReturn(Optional.of(servico));

        ServicoResponseDTO response = servicoService.buscarPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(servicoRepository, times(1)).findById(id);
    }

    @Test
    void buscarPorId_NotFound() {
        String id = UUID.randomUUID().toString();
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> servicoService.buscarPorId(id));
        verify(servicoRepository, times(1)).findById(id);
    }

    @Test
    void listarTodos_Success() {
        Servico servico = new Servico("Troca de Óleo", "Desc", new BigDecimal("150.00"), 30);
        when(servicoRepository.findAll()).thenReturn(java.util.List.of(servico));

        var result = servicoService.listarTodos();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(servicoRepository, times(1)).findAll();
    }

    @Test
    void atualizar_Success() {
        String id = UUID.randomUUID().toString();
        Servico servico = new Servico("Troca de Óleo", "Desc", new BigDecimal("150.00"), 30);
        servico.setId(id);
        ServicoRequestDTO request = new ServicoRequestDTO("Novo Nome", "Nova Desc", new BigDecimal("200.00"), 45);

        when(servicoRepository.findById(id)).thenReturn(Optional.of(servico));
        when(servicoRepository.save(any(Servico.class))).thenReturn(servico);

        ServicoResponseDTO response = servicoService.atualizar(id, request);

        assertNotNull(response);
        verify(servicoRepository, times(1)).findById(id);
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    void desativar_Success() {
        String id = UUID.randomUUID().toString();
        Servico servico = new Servico("Troca de Óleo", "Desc", new BigDecimal("150.00"), 30);
        servico.setId(id);

        when(servicoRepository.findById(id)).thenReturn(Optional.of(servico));
        when(servicoRepository.save(any(Servico.class))).thenReturn(servico);

        servicoService.desativar(id);

        assertFalse(servico.getAtivo());
        verify(servicoRepository, times(1)).findById(id);
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    void deletar_Success() {
        String id = UUID.randomUUID().toString();
        when(servicoRepository.existsById(id)).thenReturn(true);

        servicoService.deletar(id);

        verify(servicoRepository, times(1)).existsById(id);
        verify(servicoRepository, times(1)).deleteById(id);
    }
}
