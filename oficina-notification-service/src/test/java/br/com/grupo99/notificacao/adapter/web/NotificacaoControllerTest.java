package br.com.grupo99.notificacao.adapter.web;

import br.com.grupo99.notificacao.application.dto.NotificacaoRequestDTO;
import br.com.grupo99.notificacao.application.dto.NotificacaoResponseDTO;
import br.com.grupo99.notificacao.application.service.NotificacaoApplicationService;
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

@WebMvcTest(NotificacaoController.class)
@AutoConfigureMockMvc
class NotificacaoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificacaoApplicationService service;

    private UUID notificacaoId;
    private NotificacaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        notificacaoId = UUID.randomUUID();
        responseDTO = NotificacaoResponseDTO.builder()
                .id(notificacaoId)
                .destinatarioEmail("cliente@example.com")
                .tipoNotificacao("CONFIRMACAO_MANUTENCAO")
                .assunto("Confirmação")
                .mensagem("Manutenção confirmada!")
                .status("PENDENTE")
                .tentativas(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void criarNotificacao_Success() throws Exception {
        NotificacaoRequestDTO requestDTO = NotificacaoRequestDTO.builder()
                .destinatarioEmail("cliente@example.com")
                .tipoNotificacao("CONFIRMACAO_MANUTENCAO")
                .assunto("Confirmação")
                .mensagem("Manutenção confirmada!")
                .build();

        when(service.criarNotificacao(any(NotificacaoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/notificacoes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notificacaoId.toString()));

        verify(service).criarNotificacao(any(NotificacaoRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void listarTodas_Success() throws Exception {
        when(service.listarTodas()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/notificacoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(notificacaoId.toString()));

        verify(service).listarTodas();
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void buscarPorId_Success() throws Exception {
        when(service.buscarPorId(notificacaoId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/notificacoes/{id}", notificacaoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificacaoId.toString()));

        verify(service).buscarPorId(notificacaoId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void marcarComoEnviada_Success() throws Exception {
        when(service.marcarComoEnviada(notificacaoId)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/notificacoes/{id}/enviada", notificacaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).marcarComoEnviada(notificacaoId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void marcarComoFalha_Success() throws Exception {
        when(service.marcarComoFalha(notificacaoId)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/notificacoes/{id}/falha", notificacaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).marcarComoFalha(notificacaoId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarNotificacao_Success() throws Exception {
        doNothing().when(service).deletar(notificacaoId);

        mockMvc.perform(delete("/api/v1/notificacoes/{id}", notificacaoId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).deletar(notificacaoId);
    }
}
