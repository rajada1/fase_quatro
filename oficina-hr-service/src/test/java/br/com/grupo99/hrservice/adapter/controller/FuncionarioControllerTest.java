package br.com.grupo99.hrservice.adapter.controller;

import br.com.grupo99.hrservice.application.dto.FuncionarioRequestDTO;
import br.com.grupo99.hrservice.application.dto.FuncionarioResponseDTO;
import br.com.grupo99.hrservice.application.service.FuncionarioApplicationService;
import br.com.grupo99.hrservice.config.TestConfig;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@DisplayName("FuncionarioController Tests")
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FuncionarioApplicationService funcionarioApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID pessoaId = UUID.randomUUID();

    @Test
    @DisplayName("POST /api/v1/funcionarios - Deve criar funcionário com sucesso")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testCriarFuncionarioComSucesso() throws Exception {
        // Arrange
        FuncionarioRequestDTO request = new FuncionarioRequestDTO(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"));

        FuncionarioResponseDTO response = new FuncionarioResponseDTO(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(funcionarioApplicationService.criarFuncionario(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/funcionarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pessoaId").value(pessoaId.toString()))
                .andExpect(jsonPath("$.setor").value("TI"));
    }

    @Test
    @DisplayName("GET /api/v1/funcionarios - Deve listar funcionários")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testListarTodos() throws Exception {
        // Arrange
        when(funcionarioApplicationService.listarTodos()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/v1/funcionarios"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/funcionarios/{pessoaId} - Deve buscar funcionário")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testBuscarFuncionario() throws Exception {
        // Arrange
        FuncionarioResponseDTO response = new FuncionarioResponseDTO(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(funcionarioApplicationService.buscarPorId(pessoaId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/funcionarios/{pessoaId}", pessoaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setor").value("TI"));
    }

    @Test
    @DisplayName("DELETE /api/v1/funcionarios/{pessoaId} - Deve deletar funcionário")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDeletarFuncionario() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/funcionarios/{pessoaId}", pessoaId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/v1/funcionarios/{pessoaId}/desativar - Deve desativar funcionário")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDesativarFuncionario() throws Exception {
        // Arrange
        FuncionarioResponseDTO response = new FuncionarioResponseDTO(
                pessoaId,
                LocalDate.now().minusMonths(12),
                "TI",
                "Desenvolvedor",
                new BigDecimal("5000.00"),
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(funcionarioApplicationService.desativarFuncionario(pessoaId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/funcionarios/{pessoaId}/desativar", pessoaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }
}
