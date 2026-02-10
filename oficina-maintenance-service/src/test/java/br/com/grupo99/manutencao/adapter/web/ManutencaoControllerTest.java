package br.com.grupo99.manutencao.adapter.web;

import br.com.grupo99.manutencao.application.dto.ManutencaoRequestDTO;
import br.com.grupo99.manutencao.application.dto.ManutencaoResponseDTO;
import br.com.grupo99.manutencao.application.service.ManutencaoApplicationService;
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

@WebMvcTest(ManutencaoController.class)
@AutoConfigureMockMvc
class ManutencaoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManutencaoApplicationService service;

    private UUID manutencaoId;
    private UUID veiculoId;
    private ManutencaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        manutencaoId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        responseDTO = ManutencaoResponseDTO.builder()
                .id(manutencaoId)
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(1))
                .tipoManutencao("PREVENCAO")
                .descricao("Manutenção preventiva")
                .status("AGENDADA")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void criarManutencao_Success() throws Exception {
        ManutencaoRequestDTO requestDTO = ManutencaoRequestDTO.builder()
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(1))
                .tipoManutencao("PREVENCAO")
                .descricao("Manutenção preventiva")
                .build();

        when(service.criar(any(ManutencaoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/manutencoes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(manutencaoId.toString()));

        verify(service).criar(any(ManutencaoRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void listarTodas_Success() throws Exception {
        when(service.listarTodas()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/manutencoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(manutencaoId.toString()));

        verify(service).listarTodas();
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void buscarPorId_Success() throws Exception {
        when(service.buscarPorId(manutencaoId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/manutencoes/{id}", manutencaoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(manutencaoId.toString()));

        verify(service).buscarPorId(manutencaoId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void atualizarManutencao_Success() throws Exception {
        ManutencaoRequestDTO requestDTO = ManutencaoRequestDTO.builder()
                .veiculoId(veiculoId)
                .dataAgendada(LocalDateTime.now().plusDays(1))
                .tipoManutencao("CORRETIVA")
                .descricao("Manutenção corretiva")
                .build();

        when(service.atualizar(eq(manutencaoId), any(ManutencaoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/manutencoes/{id}", manutencaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(service).atualizar(eq(manutencaoId), any(ManutencaoRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void deletarManutencao_Success() throws Exception {
        doNothing().when(service).deletar(manutencaoId);

        mockMvc.perform(delete("/api/v1/manutencoes/{id}", manutencaoId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).deletar(manutencaoId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void iniciarManutencao_Success() throws Exception {
        when(service.iniciarManutencao(manutencaoId)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/manutencoes/{id}/iniciar", manutencaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).iniciarManutencao(manutencaoId);
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    void concluirManutencao_Success() throws Exception {
        when(service.concluirManutencao(manutencaoId)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/manutencoes/{id}/concluir", manutencaoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).concluirManutencao(manutencaoId);
    }
}
