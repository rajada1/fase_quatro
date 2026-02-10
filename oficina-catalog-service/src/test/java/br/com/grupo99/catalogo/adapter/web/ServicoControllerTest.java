package br.com.grupo99.catalogo.adapter.web;

import br.com.grupo99.catalogo.adapter.config.TestConfig;
import br.com.grupo99.catalogo.application.dto.ServicoRequestDTO;
import br.com.grupo99.catalogo.application.dto.ServicoResponseDTO;
import br.com.grupo99.catalogo.application.service.ServicoApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes atualizados para MongoDB - ID agora é String.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ServicoControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ServicoApplicationService servicoService;

        private ServicoRequestDTO requestDTO;

        @BeforeEach
        void setUp() {
                requestDTO = new ServicoRequestDTO("Troca de Óleo", "Troca completa", new BigDecimal("150.00"), 30);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void criarServico_Success() throws Exception {
                String id = UUID.randomUUID().toString();
                var responseDTO = new ServicoResponseDTO(
                                id, "Troca de Óleo", "Desc", new BigDecimal("150.00"), 30, true,
                                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
                when(servicoService.criarServico(any())).thenReturn(responseDTO);

                mockMvc.perform(post("/api/v1/servicos")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", notNullValue()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void listarTodos_Success() throws Exception {
                when(servicoService.listarTodos()).thenReturn(java.util.List.of());

                mockMvc.perform(get("/api/v1/servicos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void buscarPorId_Success() throws Exception {
                String id = UUID.randomUUID().toString();
                var responseDTO = new ServicoResponseDTO(
                                id, "Troca de Óleo", "Desc", new BigDecimal("150.00"), 30, true,
                                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
                when(servicoService.buscarPorId(id)).thenReturn(responseDTO);

                mockMvc.perform(get("/api/v1/servicos/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", notNullValue()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void atualizar_Success() throws Exception {
                String id = UUID.randomUUID().toString();
                var responseDTO = new ServicoResponseDTO(
                                id, "Novo Nome", "Desc", new BigDecimal("200.00"), 45, true,
                                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
                when(servicoService.atualizar(any(), any())).thenReturn(responseDTO);

                mockMvc.perform(put("/api/v1/servicos/" + id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void desativar_Success() throws Exception {
                String id = UUID.randomUUID().toString();

                mockMvc.perform(patch("/api/v1/servicos/" + id + "/desativar")
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deletar_Success() throws Exception {
                String id = UUID.randomUUID().toString();

                mockMvc.perform(delete("/api/v1/servicos/" + id)
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        void criarServico_Unauthorized() throws Exception {
                mockMvc.perform(post("/api/v1/servicos")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isForbidden());
        }
}
