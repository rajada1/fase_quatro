package br.com.grupo99.catalogo.adapter.web;

import br.com.grupo99.catalogo.adapter.config.TestConfig;
import br.com.grupo99.catalogo.application.dto.PecaRequestDTO;
import br.com.grupo99.catalogo.application.dto.PecaResponseDTO;
import br.com.grupo99.catalogo.application.service.PecaApplicationService;
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
public class PecaControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private PecaApplicationService pecaService;

        private PecaRequestDTO requestDTO;

        @BeforeEach
        void setUp() {
                requestDTO = new PecaRequestDTO("Filtro de Óleo", "Filtro de qualidade", "FO-001",
                                new BigDecimal("50.00"), 20);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void criarPeca_Success() throws Exception {
                String id = UUID.randomUUID().toString();
                var responseDTO = new PecaResponseDTO(
                                id, "Filtro de Óleo", "Desc", "FO-001", new BigDecimal("50.00"), 20, 5,
                                true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
                when(pecaService.criarPeca(any())).thenReturn(responseDTO);

                mockMvc.perform(post("/api/v1/pecas")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", notNullValue()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void listarTodas_Success() throws Exception {
                when(pecaService.listarTodas()).thenReturn(java.util.List.of());

                mockMvc.perform(get("/api/v1/pecas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void buscarPorId_Success() throws Exception {
                String id = UUID.randomUUID().toString();
                var responseDTO = new PecaResponseDTO(
                                id, "Filtro", "Desc", "FO-001", new BigDecimal("50.00"), 20, 5, true,
                                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
                when(pecaService.buscarPorId(id)).thenReturn(responseDTO);

                mockMvc.perform(get("/api/v1/pecas/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", notNullValue()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void atualizar_Success() throws Exception {
                String id = UUID.randomUUID().toString();
                var responseDTO = new PecaResponseDTO(
                                id, "Novo Nome", "Desc", "FO-002", new BigDecimal("60.00"), 25, 5, true,
                                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
                when(pecaService.atualizar(any(), any())).thenReturn(responseDTO);

                mockMvc.perform(put("/api/v1/pecas/" + id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void desativar_Success() throws Exception {
                String id = UUID.randomUUID().toString();

                mockMvc.perform(patch("/api/v1/pecas/" + id + "/desativar")
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deletar_Success() throws Exception {
                String id = UUID.randomUUID().toString();

                mockMvc.perform(delete("/api/v1/pecas/" + id)
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void decrementarEstoque_Success() throws Exception {
                String id = UUID.randomUUID().toString();

                mockMvc.perform(patch("/api/v1/pecas/" + id + "/decrementar?quantidade=5")
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void incrementarEstoque_Success() throws Exception {
                String id = UUID.randomUUID().toString();

                mockMvc.perform(patch("/api/v1/pecas/" + id + "/incrementar?quantidade=5")
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        void criarPeca_Unauthorized() throws Exception {
                mockMvc.perform(post("/api/v1/pecas")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isForbidden());
        }
}
