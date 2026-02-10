package br.com.grupo99.customerservice.application.service;

import br.com.grupo99.customerservice.application.dto.ClienteRequestDTO;
import br.com.grupo99.customerservice.application.dto.ClienteResponseDTO;
import br.com.grupo99.customerservice.application.exception.BusinessException;
import br.com.grupo99.customerservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerApplicationService Tests")
class CustomerApplicationServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private CustomerApplicationService service;

    private UUID pessoaId;
    private ClienteRequestDTO validRequestDTO;

    @BeforeEach
    void setup() {
        pessoaId = UUID.randomUUID();
        validRequestDTO = new ClienteRequestDTO(pessoaId);
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void testCriarClienteComSucesso() {
        // Arrange
        when(clienteRepository.existsByPessoaId(pessoaId)).thenReturn(false);

        // Configure mock to simulate @PrePersist
        doAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            setTimestamps(cliente);
            return cliente;
        }).when(clienteRepository).save(any(Cliente.class));

        // Act
        ClienteResponseDTO response = service.criarCliente(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(pessoaId, response.pessoaId());
        assertNotNull(response.createdAt());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(eventPublishingService, times(1)).publicarClienteCriado(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar cliente com pessoaId duplicado")
    void testCriarClienteComPessoaIdDuplicado() {
        // Arrange
        when(clienteRepository.existsByPessoaId(pessoaId)).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> service.criarCliente(validRequestDTO));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar cliente por pessoaId com sucesso")
    void testBuscarPorIdComSucesso() {
        // Arrange
        Cliente cliente = new Cliente(pessoaId);
        setTimestamps(cliente);

        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.of(cliente));

        // Act
        ClienteResponseDTO response = service.buscarPorId(pessoaId);

        // Assert
        assertNotNull(response);
        assertEquals(pessoaId, response.pessoaId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar cliente inexistente")
    void testBuscarPorIdNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(pessoaId));
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void testDeletarClienteComSucesso() {
        // Arrange
        Cliente cliente = new Cliente(pessoaId);
        setTimestamps(cliente);

        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.of(cliente));

        // Act
        service.deletarCliente(pessoaId);

        // Assert
        verify(clienteRepository, times(1)).deleteById(pessoaId);
        verify(eventPublishingService, times(1)).publicarClienteDeletado(pessoaId);
    }

    private void setTimestamps(Cliente cliente) {
        try {
            var createdAtField = Cliente.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(cliente, LocalDateTime.now());

            var updatedAtField = Cliente.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(cliente, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
