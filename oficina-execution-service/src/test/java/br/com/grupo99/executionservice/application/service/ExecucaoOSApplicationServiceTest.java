package br.com.grupo99.executionservice.application.service;

import br.com.grupo99.executionservice.application.dto.ExecucaoOSRequestDTO;
import br.com.grupo99.executionservice.application.dto.ExecucaoOSResponseDTO;
import br.com.grupo99.executionservice.application.mapper.ExecucaoOSMapper;
import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import br.com.grupo99.executionservice.domain.repository.ExecucaoOSRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExecucaoOSApplicationService - Testes Unitários")
class ExecucaoOSApplicationServiceTest {

    @Mock
    private ExecucaoOSRepository repository;

    @Mock
    private ExecucaoOSMapper mapper;

    @InjectMocks
    private ExecucaoOSApplicationService service;

    private ExecucaoOS execucaoOS;
    private UUID execucaoId;
    private UUID osId;
    private UUID orcamentoId;
    private ExecucaoOSResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        execucaoId = UUID.randomUUID();
        osId = UUID.randomUUID();
        orcamentoId = UUID.randomUUID();

        execucaoOS = ExecucaoOS.criar(osId, orcamentoId, "João Mecânico");
        execucaoOS.setId(execucaoId);

        responseDTO = ExecucaoOSResponseDTO.fromDomain(execucaoOS);
    }

    @Test
    @DisplayName("Deve criar execução com sucesso")
    void deveCriarExecucaoComSucesso() {
        ExecucaoOSRequestDTO request = new ExecucaoOSRequestDTO(osId, orcamentoId, "João Mecânico");

        when(mapper.toDomain(request)).thenReturn(execucaoOS);
        when(repository.save(execucaoOS)).thenReturn(execucaoOS);
        when(mapper.toResponseDTO(execucaoOS)).thenReturn(responseDTO);

        ExecucaoOSResponseDTO result = service.criar(request);

        assertThat(result).isNotNull();
        assertThat(result.osId()).isEqualTo(osId);
        verify(repository).save(execucaoOS);
    }

    @Test
    @DisplayName("Deve buscar execução por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(repository.findById(execucaoId)).thenReturn(Optional.of(execucaoOS));
        when(mapper.toResponseDTO(execucaoOS)).thenReturn(responseDTO);

        ExecucaoOSResponseDTO result = service.buscarPorId(execucaoId);

        assertThat(result).isNotNull();
        verify(repository).findById(execucaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando execução não encontrada por ID")
    void deveLancarExcecaoQuandoNaoEncontradaPorId() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Execução não encontrada");
    }

    @Test
    @DisplayName("Deve buscar por osId com sucesso")
    void deveBuscarPorOsIdComSucesso() {
        when(repository.findByOsId(osId)).thenReturn(Optional.of(execucaoOS));
        when(mapper.toResponseDTO(execucaoOS)).thenReturn(responseDTO);

        ExecucaoOSResponseDTO result = service.buscarPorOsId(osId);

        assertThat(result).isNotNull();
        verify(repository).findByOsId(osId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando execução não encontrada por osId")
    void deveLancarExcecaoQuandoNaoEncontradaPorOsId() {
        UUID id = UUID.randomUUID();
        when(repository.findByOsId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorOsId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Execução não encontrada para OS");
    }

    @Test
    @DisplayName("Deve listar todas as execuções")
    void deveListarTodasAsExecucoes() {
        when(repository.findAll()).thenReturn(List.of(execucaoOS));
        when(mapper.toResponseDTO(execucaoOS)).thenReturn(responseDTO);

        List<ExecucaoOSResponseDTO> result = service.listarTodas();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar por status")
    void deveBuscarPorStatus() {
        when(repository.findByStatus(StatusExecucao.AGUARDANDO_INICIO)).thenReturn(List.of(execucaoOS));
        when(mapper.toResponseDTO(execucaoOS)).thenReturn(responseDTO);

        List<ExecucaoOSResponseDTO> result = service.buscarPorStatus(StatusExecucao.AGUARDANDO_INICIO);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar por mecânico")
    void deveBuscarPorMecanico() {
        when(repository.findByMecanico("João Mecânico")).thenReturn(List.of(execucaoOS));
        when(mapper.toResponseDTO(execucaoOS)).thenReturn(responseDTO);

        List<ExecucaoOSResponseDTO> result = service.buscarPorMecanico("João Mecânico");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve iniciar execução com sucesso")
    void deveIniciarExecucaoComSucesso() {
        when(repository.findById(execucaoId)).thenReturn(Optional.of(execucaoOS));
        when(repository.save(any(ExecucaoOS.class))).thenReturn(execucaoOS);
        when(mapper.toResponseDTO(any(ExecucaoOS.class))).thenReturn(responseDTO);

        ExecucaoOSResponseDTO result = service.iniciar(execucaoId);

        assertThat(result).isNotNull();
        verify(repository).save(any(ExecucaoOS.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar execução não encontrada")
    void deveLancarExcecaoAoIniciarExecucaoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.iniciar(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve finalizar execução com sucesso")
    void deveFinalizarExecucaoComSucesso() {
        execucaoOS.iniciar(); // primeiro inicia

        when(repository.findById(execucaoId)).thenReturn(Optional.of(execucaoOS));
        when(repository.save(any(ExecucaoOS.class))).thenReturn(execucaoOS);
        when(mapper.toResponseDTO(any(ExecucaoOS.class))).thenReturn(responseDTO);

        ExecucaoOSResponseDTO result = service.finalizar(execucaoId, "Obs finais");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar execução não encontrada")
    void deveLancarExcecaoAoFinalizarExecucaoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.finalizar(id, "Obs"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve cancelar execução com sucesso")
    void deveCancelarExecucaoComSucesso() {
        when(repository.findById(execucaoId)).thenReturn(Optional.of(execucaoOS));
        when(repository.save(any(ExecucaoOS.class))).thenReturn(execucaoOS);
        when(mapper.toResponseDTO(any(ExecucaoOS.class))).thenReturn(responseDTO);

        ExecucaoOSResponseDTO result = service.cancelar(execucaoId, "Motivo cancelamento");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar execução não encontrada")
    void deveLancarExcecaoAoCancelarExecucaoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelar(id, "Motivo"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve deletar execução com sucesso")
    void deveDeletarExecucaoComSucesso() {
        when(repository.existsById(execucaoId)).thenReturn(true);
        doNothing().when(repository).deleteById(execucaoId);

        service.deletar(execucaoId);

        verify(repository).deleteById(execucaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar execução não encontrada")
    void deveLancarExcecaoAoDeletarExecucaoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deletar(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Execução não encontrada");
    }
}
