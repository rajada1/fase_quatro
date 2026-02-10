package br.com.grupo99.customerservice.adapter.controller;

import br.com.grupo99.customerservice.application.dto.ClienteRequestDTO;
import br.com.grupo99.customerservice.application.dto.ClienteResponseDTO;
import br.com.grupo99.customerservice.application.service.CustomerApplicationService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@ActiveProfiles("test")
@DisplayName("ClienteController Tests")
class ClienteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CustomerApplicationService customerApplicationService;

        @Autowired
        private ObjectMapper objectMapper;

        private UUID pessoaId = UUID.randomUUID();

        @Test
        @DisplayName("POST /api/v1/clientes - Deve criar cliente com sucesso")
        @WithMockUser(username = "test", roles = "ADMIN")
        void testCriarClienteComSucesso() throws Exception {
                // Arrange
                ClienteRequestDTO request = new ClienteRequestDTO(pessoaId);

                ClienteResponseDTO response = new ClienteResponseDTO(
                                pessoaId,
                                new ArrayList<>(),
                                LocalDateTime.now(),
                                LocalDateTime.now());

                when(customerApplicationService.criarCliente(any())).thenReturn(response);

                // Act & Assert
                mockMvc.perform(post("/api/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.pessoaId").value(pessoaId.toString()));
        }

        @Test
        @DisplayName("GET /api/v1/clientes/{id} - Deve buscar cliente por ID")
        @WithMockUser(username = "test", roles = "ADMIN")
        void testBuscarClientePorId() throws Exception {
                // Arrange
                ClienteResponseDTO response = new ClienteResponseDTO(
                                pessoaId,
                                new ArrayList<>(),
                                LocalDateTime.now(),
                                LocalDateTime.now());

                when(customerApplicationService.buscarPorId(pessoaId)).thenReturn(response);

                // Act & Assert
                mockMvc.perform(get("/api/v1/clientes/{id}", pessoaId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.pessoaId").value(pessoaId.toString()));
        }

        @Test
        @DisplayName("DELETE /api/v1/clientes/{id} - Deve deletar cliente")
        @WithMockUser(username = "test", roles = "ADMIN")
        void testDeletarCliente() throws Exception {
                // Act & Assert
                mockMvc.perform(delete("/api/v1/clientes/{id}", pessoaId))
                                .andExpect(status().isNoContent());
        }
}
