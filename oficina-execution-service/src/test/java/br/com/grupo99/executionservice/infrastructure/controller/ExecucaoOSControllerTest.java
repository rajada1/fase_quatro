package br.com.grupo99.executionservice.infrastructure.controller;

import br.com.grupo99.executionservice.application.dto.ExecucaoOSRequestDTO;
import br.com.grupo99.executionservice.application.dto.ExecucaoOSResponseDTO;
import br.com.grupo99.executionservice.application.service.ExecucaoOSApplicationService;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExecucaoOSController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("ExecucaoOSController - Testes Unitários")
class ExecucaoOSControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ExecucaoOSApplicationService applicationService;

        @MockBean
        private br.com.grupo99.executionservice.infrastructure.security.jwt.JwtUtil jwtUtil;

        private static final String BASE_URL = "/execucoes-os";

        private UUID execucaoId;
        private UUID osId;
        private UUID orcamentoId;
        private ExecucaoOSResponseDTO responseDTO;

        @BeforeEach
        void setUp() {
                execucaoId = UUID.randomUUID();
                osId = UUID.randomUUID();
                orcamentoId = UUID.randomUUID();

                responseDTO = new ExecucaoOSResponseDTO(
                                execucaoId, osId, orcamentoId,
                                StatusExecucao.AGUARDANDO_INICIO, "João Mecânico",
                                Instant.now(), null, null,
                                0, 0, 0, Instant.now(), Instant.now());
        }

        @Test
        @DisplayName("POST - Deve criar nova execução")
        void deveCriarNovaExecucao() throws Exception {
                ExecucaoOSRequestDTO request = new ExecucaoOSRequestDTO(osId, orcamentoId, "João Mecânico");
                when(applicationService.criar(any(ExecucaoOSRequestDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(execucaoId.toString()))
                                .andExpect(jsonPath("$.status").value("AGUARDANDO_INICIO"));
        }

        @Test
        @DisplayName("GET /{id} - Deve buscar execução por ID")
        void deveBuscarPorId() throws Exception {
                when(applicationService.buscarPorId(execucaoId)).thenReturn(responseDTO);

                mockMvc.perform(get(BASE_URL + "/" + execucaoId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(execucaoId.toString()));
        }

        @Test
        @DisplayName("GET /os/{osId} - Deve buscar execução por osId")
        void deveBuscarPorOsId() throws Exception {
                when(applicationService.buscarPorOsId(osId)).thenReturn(responseDTO);

                mockMvc.perform(get(BASE_URL + "/os/" + osId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.osId").value(osId.toString()));
        }

        @Test
        @DisplayName("GET - Deve listar todas as execuções")
        void deveListarTodas() throws Exception {
                when(applicationService.listarTodas()).thenReturn(List.of(responseDTO));

                mockMvc.perform(get(BASE_URL))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("GET - Deve retornar lista vazia quando não há execuções")
        void deveRetornarListaVazia() throws Exception {
                when(applicationService.listarTodas()).thenReturn(Collections.emptyList());

                mockMvc.perform(get(BASE_URL))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("GET /status/{status} - Deve buscar por status")
        void deveBuscarPorStatus() throws Exception {
                when(applicationService.buscarPorStatus(StatusExecucao.AGUARDANDO_INICIO))
                                .thenReturn(List.of(responseDTO));

                mockMvc.perform(get(BASE_URL + "/status/AGUARDANDO_INICIO"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("GET /mecanico/{mecanico} - Deve buscar por mecânico")
        void deveBuscarPorMecanico() throws Exception {
                when(applicationService.buscarPorMecanico("João Mecânico"))
                                .thenReturn(List.of(responseDTO));

                mockMvc.perform(get(BASE_URL + "/mecanico/João Mecânico"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("PUT /{id}/iniciar - Deve iniciar execução")
        void deveIniciarExecucao() throws Exception {
                when(applicationService.iniciar(execucaoId)).thenReturn(responseDTO);

                mockMvc.perform(put(BASE_URL + "/" + execucaoId + "/iniciar"))
                                .andExpect(status().isOk());

                verify(applicationService).iniciar(execucaoId);
        }

        @Test
        @DisplayName("PUT /{id}/finalizar - Deve finalizar execução")
        void deveFinalizarExecucao() throws Exception {
                when(applicationService.finalizar(eq(execucaoId), any())).thenReturn(responseDTO);

                mockMvc.perform(put(BASE_URL + "/" + execucaoId + "/finalizar")
                                .param("observacoes", "Tudo OK"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PUT /{id}/cancelar - Deve cancelar execução")
        void deveCancelarExecucao() throws Exception {
                when(applicationService.cancelar(eq(execucaoId), any())).thenReturn(responseDTO);

                mockMvc.perform(put(BASE_URL + "/" + execucaoId + "/cancelar")
                                .param("motivo", "Cliente desistiu"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DELETE /{id} - Deve deletar execução")
        void deveDeletarExecucao() throws Exception {
                doNothing().when(applicationService).deletar(execucaoId);

                mockMvc.perform(delete(BASE_URL + "/" + execucaoId))
                                .andExpect(status().isNoContent());

                verify(applicationService).deletar(execucaoId);
        }
}
