package br.com.grupo99.customerservice.application.service;

import br.com.grupo99.customerservice.application.dto.VeiculoRequestDTO;
import br.com.grupo99.customerservice.application.dto.VeiculoResponseDTO;
import br.com.grupo99.customerservice.application.exception.BusinessException;
import br.com.grupo99.customerservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.model.Veiculo;
import br.com.grupo99.customerservice.domain.repository.ClienteRepository;
import br.com.grupo99.customerservice.domain.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VeiculoApplicationService Tests")
class VeiculoApplicationServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EventPublishingService eventPublishingService;

    @InjectMocks
    private VeiculoApplicationService service;

    private UUID pessoaId;
    private UUID veiculoId;
    private VeiculoRequestDTO validRequestDTO;
    private Cliente cliente;

    @BeforeEach
    void setup() {
        pessoaId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        validRequestDTO = new VeiculoRequestDTO(
                "ABC1234",
                "Toyota",
                "Corolla",
                2023,
                "12345678901234",
                "Preto",
                "ABCD1234EFGH5678");
        cliente = new Cliente(pessoaId);
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void testCriarVeiculoComSucesso() {
        // Arrange
        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.existsByPlaca(validRequestDTO.placa())).thenReturn(false);

        Veiculo veiculo = new Veiculo(
                validRequestDTO.placa(),
                validRequestDTO.marca(),
                validRequestDTO.modelo(),
                validRequestDTO.ano());
        veiculo.setId(veiculoId);

        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        VeiculoResponseDTO response = service.criarVeiculo(pessoaId, validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(validRequestDTO.placa(), response.placa());
        assertEquals(validRequestDTO.marca(), response.marca());
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
        verify(eventPublishingService, times(1)).publicarVeiculoCriado(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar veículo com cliente inexistente")
    void testCriarVeiculoClienteInexistente() {
        // Arrange
        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.criarVeiculo(pessoaId, validRequestDTO));
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao criar veículo com placa duplicada")
    void testCriarVeiculoComPlacaDuplicada() {
        // Arrange
        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.existsByPlaca(validRequestDTO.placa())).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> service.criarVeiculo(pessoaId, validRequestDTO));
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar veículo por ID com sucesso")
    void testBuscarVeiculoPorIdComSucesso() {
        // Arrange
        Veiculo veiculo = new Veiculo(
                validRequestDTO.placa(),
                validRequestDTO.marca(),
                validRequestDTO.modelo(),
                validRequestDTO.ano());
        veiculo.setId(veiculoId);

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        VeiculoResponseDTO response = service.buscarPorId(veiculoId);

        // Assert
        assertNotNull(response);
        assertEquals(validRequestDTO.placa(), response.placa());
    }

    @Test
    @DisplayName("Deve falhar ao buscar veículo inexistente")
    void testBuscarVeiculoInexistente() {
        // Arrange
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.buscarPorId(veiculoId));
    }

    @Test
    @DisplayName("Deve listar veículos de um cliente")
    void testListarVeiculosDoCliente() {
        // Arrange
        Veiculo veiculo1 = new Veiculo("ABC1234", "Toyota", "Corolla", 2023);
        Veiculo veiculo2 = new Veiculo("XYZ5678", "Honda", "Civic", 2022);

        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findByClientePessoaId(pessoaId)).thenReturn(List.of(veiculo1, veiculo2));

        // Act
        List<VeiculoResponseDTO> response = service.listarVeiculosDoCliente(pessoaId);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Deve deletar veículo com sucesso")
    void testDeletarVeiculoComSucesso() {
        // Arrange
        Veiculo veiculo = new Veiculo(
                validRequestDTO.placa(),
                validRequestDTO.marca(),
                validRequestDTO.modelo(),
                validRequestDTO.ano());
        veiculo.setId(veiculoId);
        veiculo.setCliente(cliente);

        when(clienteRepository.findById(pessoaId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        // Act
        service.deletarVeiculo(pessoaId, veiculoId);

        // Assert
        verify(veiculoRepository, times(1)).deleteById(veiculoId);
        verify(eventPublishingService, times(1)).publicarVeiculoDeletado(veiculoId);
    }
}
