package br.com.grupo99.operacoes.adapter.web;

import br.com.grupo99.operacoes.application.dto.OperacaoRequestDTO;
import br.com.grupo99.operacoes.application.dto.OperacaoResponseDTO;
import br.com.grupo99.operacoes.application.service.OperacaoApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperacaoController.class)
@AutoConfigureMockMvc
class OperacaoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OperacaoApplicationService service;

    private UUID operacaoId;
    private UUID orcamentoId;
    private OperacaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        operacaoId = UUID.randomUUID();
        orcamentoId = UUID.randomUUID();
        responseDTO = OperacaoResponseDTO.builder()
                .id(operacaoId)
                .numeroOperacao("OP-123456")
                .orcamentoId(orcamentoId)
                .descricao("Operação de manutenção")
                .status("PLANEJADA")
                .prioridade("NORMAL")
                .tempoEstimadoHoras(8)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void criarOperacao_Success() throws Exception {
        OperacaoRequestDTO requestDTO = OperacaoRequestDTO.builder()
                .orcamentoId(orcamentoId)
                .descricao("Operação de manutenção")
                .prioridade("NORMAL")
                .tempoEstimadoHoras(8)
                .build();

        when(service.criarOperacao(any(OperacaoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/operacoes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(operacaoId.toString()));

        verify(service).criarOperacao(any(OperacaoRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void listarTodas_Success() throws Exception {
        when(service.listarTodas()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/operacoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(operacaoId.toString()));

        verify(service).listarTodas();
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void buscarPorId_Success() throws Exception {
        when(service.buscarPorId(operacaoId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/operacoes/{id}", operacaoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(operacaoId.toString()));

        verify(service).buscarPorId(operacaoId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void iniciarOperacao_Success() throws Exception {
        UUID responsavelId = UUID.randomUUID();
        when(service.iniciarOperacao(operacaoId, responsavelId)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/operacoes/{id}/iniciar", operacaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("responsavelId", responsavelId.toString()))
                .andExpect(status().isOk());

        verify(service).iniciarOperacao(operacaoId, responsavelId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void concluirOperacao_Success() throws Exception {
        when(service.concluirOperacao(operacaoId, 8)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/operacoes/{id}/concluir", operacaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("tempoRealizadoHoras", "8"))
                .andExpect(status().isOk());

        verify(service).concluirOperacao(operacaoId, 8);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void pausarOperacao_Success() throws Exception {
        when(service.pausarOperacao(operacaoId)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/operacoes/{id}/pausar", operacaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).pausarOperacao(operacaoId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarOperacao_Success() throws Exception {
        doNothing().when(service).deletar(operacaoId);

        mockMvc.perform(delete("/api/v1/operacoes/{id}", operacaoId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).deletar(operacaoId);
    }
}
