package br.com.grupo99.hrservice.application.service;

import br.com.grupo99.hrservice.application.dto.FuncionarioRequestDTO;
import br.com.grupo99.hrservice.application.dto.FuncionarioResponseDTO;
import br.com.grupo99.hrservice.application.exception.BusinessException;
import br.com.grupo99.hrservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.hrservice.domain.model.Funcionario;
import br.com.grupo99.hrservice.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FuncionarioApplicationService Tests")
class FuncionarioApplicationServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private FuncionarioApplicationService service;

    private UUID pessoaId;
    private FuncionarioRequestDTO validRequestDTO;

    @BeforeEach
    void setup() {
        pessoaId = UUID.randomUUID();
        validRequestDTO = new FuncionarioRequestDTO(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("Deve criar funcionário com sucesso")
    void testCriarFuncionarioComSucesso() {
        // Arrange
        when(funcionarioRepository.existsByPessoaId(pessoaId)).thenReturn(false);

        doAnswer(invocation -> {
            Funcionario funcionario = invocation.getArgument(0);
            setTimestamps(funcionario);
            return funcionario;
        }).when(funcionarioRepository).save(any(Funcionario.class));

        // Act
        FuncionarioResponseDTO response = service.criarFuncionario(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(pessoaId, response.pessoaId());
        assertEquals("TI", response.setor());
        assertEquals("Desenvolvedor", response.cargo());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
        verify(eventPublishingService, times(1)).publicarFuncionarioCriado(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar funcionário com pessoaId duplicado")
    void testCriarFuncionarioComPessoaIdDuplicado() {
        // Arrange
        when(funcionarioRepository.existsByPessoaId(pessoaId)).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.criarFuncionario(validRequestDTO));
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar funcionário por pessoaId com sucesso")
    void testBuscarPorIdComSucesso() {
        // Arrange
        Funcionario funcionario = new Funcionario(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"));
        setTimestamps(funcionario);

        when(funcionarioRepository.findById(pessoaId)).thenReturn(Optional.of(funcionario));

        // Act
        FuncionarioResponseDTO response = service.buscarPorId(pessoaId);

        // Assert
        assertNotNull(response);
        assertEquals(pessoaId, response.pessoaId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar funcionário inexistente")
    void testBuscarPorIdNaoEncontrado() {
        // Arrange
        when(funcionarioRepository.findById(pessoaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(pessoaId));
    }

    @Test
    @DisplayName("Deve deletar funcionário com sucesso")
    void testDeletarFuncionarioComSucesso() {
        // Arrange
        Funcionario funcionario = new Funcionario(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"));
        setTimestamps(funcionario);

        when(funcionarioRepository.findById(pessoaId)).thenReturn(Optional.of(funcionario));

        // Act
        service.deletarFuncionario(pessoaId);

        // Assert
        verify(funcionarioRepository, times(1)).deleteById(pessoaId);
        verify(eventPublishingService, times(1)).publicarFuncionarioDeletado(pessoaId);
    }

    @Test
    @DisplayName("Deve desativar funcionário com sucesso")
    void testDesativarFuncionarioComSucesso() {
        // Arrange
        Funcionario funcionario = new Funcionario(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"));
        setTimestamps(funcionario);

        when(funcionarioRepository.findById(pessoaId)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);

        // Act
        FuncionarioResponseDTO response = service.desativarFuncionario(pessoaId);

        // Assert
        assertNotNull(response);
        assertFalse(response.ativo());
        verify(eventPublishingService, times(1)).publicarFuncionarioAtualizado(any(Funcionario.class));
    }

    private void setTimestamps(Funcionario funcionario) {
        try {
            var createdAtField = Funcionario.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(funcionario, LocalDateTime.now());

            var updatedAtField = Funcionario.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(funcionario, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
