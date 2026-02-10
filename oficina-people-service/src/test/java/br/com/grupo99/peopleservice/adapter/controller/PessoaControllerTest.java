package br.com.grupo99.peopleservice.adapter.controller;

import br.com.grupo99.peopleservice.application.dto.PessoaRequestDTO;
import br.com.grupo99.peopleservice.application.dto.PessoaResponseDTO;
import br.com.grupo99.peopleservice.application.service.PessoaApplicationService;
import br.com.grupo99.peopleservice.config.TestConfig;
import br.com.grupo99.peopleservice.domain.model.Perfil;
import br.com.grupo99.peopleservice.domain.model.TipoPessoa;
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

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@ActiveProfiles("test")
@DisplayName("PessoaController Tests")
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PessoaApplicationService pessoaApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID pessoaId = UUID.randomUUID();

    @Test
    @DisplayName("POST /api/v1/pessoas - Deve criar Pessoa com sucesso")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testCriarPessoaComSucesso() throws Exception {
        // Arrange
        PessoaRequestDTO request = new PessoaRequestDTO(
                "12345678901",
                TipoPessoa.PESSOA_FISICA,
                "João Silva",
                "joao@example.com",
                "11999999999",
                Perfil.CLIENTE);

        PessoaResponseDTO response = new PessoaResponseDTO(
                pessoaId,
                "12345678901",
                TipoPessoa.PESSOA_FISICA,
                "João Silva",
                "joao@example.com",
                "11999999999",
                Perfil.CLIENTE,
                null,
                null);

        when(pessoaApplicationService.criarPessoa(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pessoaId.toString()))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/pessoas/{id} - Deve buscar Pessoa por ID")
    @WithMockUser(username = "test", roles = "CLIENTE")
    void testBuscarPessoaPorId() throws Exception {
        // Arrange
        PessoaResponseDTO response = new PessoaResponseDTO(
                pessoaId,
                "12345678901",
                TipoPessoa.PESSOA_FISICA,
                "João Silva",
                "joao@example.com",
                "11999999999",
                Perfil.CLIENTE,
                null,
                null);

        when(pessoaApplicationService.buscarPorId(pessoaId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pessoas/" + pessoaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pessoaId.toString()))
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    @DisplayName("DELETE /api/v1/pessoas/{id} - Deve deletar Pessoa")
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDeletarPessoa() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/pessoas/" + pessoaId))
                .andExpect(status().isNoContent());
    }
}
