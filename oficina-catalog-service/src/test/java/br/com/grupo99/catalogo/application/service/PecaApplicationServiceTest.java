package br.com.grupo99.catalogo.application.service;

import br.com.grupo99.catalogo.application.dto.PecaRequestDTO;
import br.com.grupo99.catalogo.application.dto.PecaResponseDTO;
import br.com.grupo99.catalogo.domain.model.Peca;
import br.com.grupo99.catalogo.domain.repository.PecaRepository;
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
public class PecaApplicationServiceTest {
    @Mock
    private PecaRepository pecaRepository;

    @InjectMocks
    private PecaApplicationService pecaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarPeca_Success() {
        PecaRequestDTO request = new PecaRequestDTO("Filtro de Óleo", "Filtro de qualidade", "FO-001",
                new BigDecimal("50.00"), 20);
        Peca peca = new Peca(request.getNome(), request.getDescricao(), request.getCodigoFabricante(),
                request.getPreco(), request.getQuantidade());
        String id = UUID.randomUUID().toString();
        peca.setId(id);

        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        PecaResponseDTO response = pecaService.criarPeca(request);

        assertNotNull(response);
        assertEquals(peca.getId(), response.getId());
        assertEquals("Filtro de Óleo", response.getNome());
        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    @Test
    void buscarPorId_Success() {
        String id = UUID.randomUUID().toString();
        Peca peca = new Peca("Filtro de Óleo", "Desc", "FO-001", new BigDecimal("50.00"), 20);
        peca.setId(id);

        when(pecaRepository.findById(id)).thenReturn(Optional.of(peca));

        PecaResponseDTO response = pecaService.buscarPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(pecaRepository, times(1)).findById(id);
    }

    @Test
    void buscarPorId_NotFound() {
        String id = UUID.randomUUID().toString();
        when(pecaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pecaService.buscarPorId(id));
        verify(pecaRepository, times(1)).findById(id);
    }

    @Test
    void listarTodas_Success() {
        Peca peca = new Peca("Filtro", "Desc", "FO-001", new BigDecimal("50.00"), 20);
        when(pecaRepository.findAll()).thenReturn(java.util.List.of(peca));

        var result = pecaService.listarTodas();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(pecaRepository, times(1)).findAll();
    }

    @Test
    void atualizar_Success() {
        String id = UUID.randomUUID().toString();
        Peca peca = new Peca("Filtro", "Desc", "FO-001", new BigDecimal("50.00"), 20);
        peca.setId(id);
        PecaRequestDTO request = new PecaRequestDTO("Novo Nome", "Nova Desc", "FO-002", new BigDecimal("60.00"), 25);

        when(pecaRepository.findById(id)).thenReturn(Optional.of(peca));
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        PecaResponseDTO response = pecaService.atualizar(id, request);

        assertNotNull(response);
        verify(pecaRepository, times(1)).findById(id);
        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    @Test
    void desativar_Success() {
        String id = UUID.randomUUID().toString();
        Peca peca = new Peca("Filtro", "Desc", "FO-001", new BigDecimal("50.00"), 20);
        peca.setId(id);

        when(pecaRepository.findById(id)).thenReturn(Optional.of(peca));
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        pecaService.desativar(id);

        assertFalse(peca.getAtivo());
        verify(pecaRepository, times(1)).findById(id);
        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    @Test
    void deletar_Success() {
        String id = UUID.randomUUID().toString();
        when(pecaRepository.existsById(id)).thenReturn(true);

        pecaService.deletar(id);

        verify(pecaRepository, times(1)).existsById(id);
        verify(pecaRepository, times(1)).deleteById(id);
    }
}
