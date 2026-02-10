package br.com.grupo99.peopleservice.application.service;

import br.com.grupo99.peopleservice.application.dto.PessoaRequestDTO;
import br.com.grupo99.peopleservice.application.dto.PessoaResponseDTO;
import br.com.grupo99.peopleservice.application.exception.BusinessException;
import br.com.grupo99.peopleservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.peopleservice.domain.model.Perfil;
import br.com.grupo99.peopleservice.domain.model.Pessoa;
import br.com.grupo99.peopleservice.domain.model.TipoPessoa;
import br.com.grupo99.peopleservice.domain.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PessoaApplicationService Tests")
class PessoaApplicationServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private PessoaApplicationService pessoaApplicationService;

    private UUID pessoaId;
    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pessoaId = UUID.randomUUID();
        pessoa = new Pessoa(
                "12345678901",
                TipoPessoa.PESSOA_FISICA,
                "João Silva",
                "joao@example.com",
                "11999999999",
                Perfil.CLIENTE);
        pessoa.setId(pessoaId);
    }

    @Test
    @DisplayName("Deve criar Pessoa com sucesso")
    void testCriarPessoaComSucesso() {
        // Arrange
        PessoaRequestDTO request = new PessoaRequestDTO(
                "12345678901",
                TipoPessoa.PESSOA_FISICA,
                "João Silva",
                "joao@example.com",
                "11999999999",
                Perfil.CLIENTE);

        when(pessoaRepository.existsByNumeroDocumento(any())).thenReturn(false);
        when(pessoaRepository.existsByEmail(any())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        // Act
        PessoaResponseDTO response = pessoaApplicationService.criarPessoa(request);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva", response.name());
        assertEquals("joao@example.com", response.email());
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
        verify(eventPublishingService, times(1)).publicarPessoaCriada(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento duplicado")
    void testCriarPessoaComDocumentoDuplicado() {
        // Arrange
        PessoaRequestDTO request = new PessoaRequestDTO(
                "12345678901",
                TipoPessoa.PESSOA_FISICA,
                "João Silva",
                "joao@example.com",
                "11999999999",
                Perfil.CLIENTE);

        when(pessoaRepository.existsByNumeroDocumento(any())).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> pessoaApplicationService.criarPessoa(request));
    }

    @Test
    @DisplayName("Deve buscar Pessoa por ID com sucesso")
    void testBuscarPorIdComSucesso() {
        // Arrange
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.of(pessoa));

        // Act
        PessoaResponseDTO response = pessoaApplicationService.buscarPorId(pessoaId);

        // Assert
        assertNotNull(response);
        assertEquals(pessoaId, response.id());
        assertEquals("João Silva", response.name());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Pessoa não encontrada")
    void testBuscarPorIdNaoEncontrada() {
        // Arrange
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> pessoaApplicationService.buscarPorId(pessoaId));
    }

    @Test
    @DisplayName("Deve deletar Pessoa com sucesso")
    void testDeletarPessoaComSucesso() {
        // Arrange
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.of(pessoa));

        // Act
        pessoaApplicationService.deletarPessoa(pessoaId);

        // Assert
        verify(pessoaRepository, times(1)).delete(any(Pessoa.class));
        verify(eventPublishingService, times(1)).publicarPessoaDeletada(any(Pessoa.class));
    }
}
