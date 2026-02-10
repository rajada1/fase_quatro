package br.com.grupo99.customerservice.adapter.controller;

import br.com.grupo99.customerservice.application.dto.VeiculoRequestDTO;
import br.com.grupo99.customerservice.application.dto.VeiculoResponseDTO;
import br.com.grupo99.customerservice.application.service.VeiculoApplicationService;
import br.com.grupo99.customerservice.config.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@ActiveProfiles("test")
@DisplayName("VeiculoController Tests")
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VeiculoApplicationService veiculoApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID pessoaId = UUID.randomUUID();
    private UUID veiculoId = UUID.randomUUID();

    @Test
    @DisplayName("POST /api/v1/clientes/{pessoaId}/veiculos - Deve criar veículo com sucesso")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testCriarVeiculoComSucesso() throws Exception {
        // Arrange
        VeiculoRequestDTO request = new VeiculoRequestDTO(
                "ABC1234",
                "Toyota",
                "Corolla",
                2023,
                "12345678901234",
                "Preto",
                "ABCD1234EFGH5678");

        VeiculoResponseDTO response = new VeiculoResponseDTO(
                veiculoId,
                "ABC1234",
                "12345678901234",
                "Toyota",
                "Corolla",
                2023,
                "Preto",
                "ABCD1234EFGH5678");

        when(veiculoApplicationService.criarVeiculo(eq(pessoaId), any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clientes/{pessoaId}/veiculos", pessoaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{pessoaId}/veiculos - Deve listar veículos do cliente")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testListarVeiculosDoCliente() throws Exception {
        // Arrange
        when(veiculoApplicationService.listarVeiculosDoCliente(pessoaId))
                .thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes/{pessoaId}/veiculos", pessoaId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{pessoaId}/veiculos/{veiculoId} - Deve buscar veículo")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testBuscarVeiculo() throws Exception {
        // Arrange
        VeiculoResponseDTO response = new VeiculoResponseDTO(
                veiculoId,
                "ABC1234",
                "12345678901234",
                "Toyota",
                "Corolla",
                2023,
                "Preto",
                "ABCD1234EFGH5678");

        when(veiculoApplicationService.buscarPorId(veiculoId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes/{pessoaId}/veiculos/{veiculoId}", pessoaId, veiculoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"));
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/{pessoaId}/veiculos/{veiculoId} - Deve deletar veículo")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDeletarVeiculo() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/clientes/{pessoaId}/veiculos/{veiculoId}", pessoaId, veiculoId))
                .andExpect(status().isNoContent());
    }
}
