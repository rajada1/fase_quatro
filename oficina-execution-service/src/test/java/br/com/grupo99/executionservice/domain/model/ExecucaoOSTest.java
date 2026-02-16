package br.com.grupo99.executionservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExecucaoOS - Testes Unitários")
class ExecucaoOSTest {

    @Test
    @DisplayName("Deve criar execução com status AGUARDANDO_INICIO")
    void deveCriarExecucaoComStatusAguardandoInicio() {
        // Arrange & Act
        UUID osId = UUID.randomUUID();
        ExecucaoOS execucao = ExecucaoOS.criar(osId, "Mecânico João");

        // Assert
        assertNotNull(execucao);
        assertNotNull(execucao.getId());
        assertEquals(osId, execucao.getOsId());
        assertEquals("Mecânico João", execucao.getMecanico());
        assertEquals(StatusExecucao.AGUARDANDO_INICIO, execucao.getStatus());
        assertNotNull(execucao.getDataCriacao()); // Factory method inicializa createdAt para testes
        assertNotNull(execucao.getDataInicio()); // Factory method inicializa dataInicio
        assertNull(execucao.getDataConclusao());
        assertTrue(execucao.getDiagnosticos().isEmpty());
        assertTrue(execucao.getTarefas().isEmpty());
        assertTrue(execucao.getPecas().isEmpty());
    }

    @Test
    @DisplayName("Deve iniciar execução")
    void deveIniciarExecucao() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico José");

        // Act
        execucao.iniciar();

        // Assert
        assertEquals(StatusExecucao.EM_ANDAMENTO, execucao.getStatus());
        assertNotNull(execucao.getDataInicio());
    }

    @Test
    @DisplayName("Deve finalizar execução")
    void deveFinalizarExecucao() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Pedro");
        execucao.iniciar();

        // Act
        execucao.finalizar();

        // Assert
        assertEquals(StatusExecucao.CONCLUIDA, execucao.getStatus());
        assertNotNull(execucao.getDataConclusao());
    }

    @Test
    @DisplayName("Deve cancelar execução")
    void deveCancelarExecucao() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Ana");

        // Act
        execucao.cancelar();

        // Assert
        assertEquals(StatusExecucao.CANCELADA, execucao.getStatus());
    }

    @Test
    @DisplayName("Não deve iniciar execução já iniciada")
    void naoDeveIniciarExecucaoJaIniciada() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Carlos");
        execucao.iniciar();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> execucao.iniciar());
    }

    @Test
    @DisplayName("Não deve finalizar execução não iniciada")
    void naoDeveFinalizarExecucaoNaoIniciada() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Maria");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> execucao.finalizar());
    }

    @Test
    @DisplayName("Deve adicionar diagnóstico")
    void deveAdicionarDiagnostico() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Luis");
        Diagnostico diagnostico = new Diagnostico("Problema no motor", "Mecânico Luis");

        // Act
        execucao.adicionarDiagnostico(diagnostico);

        // Assert
        assertEquals(1, execucao.getDiagnosticos().size());
        assertEquals("Problema no motor", execucao.getDiagnosticos().get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve adicionar tarefa")
    void deveAdicionarTarefa() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Sofia");
        Tarefa tarefa = new Tarefa("Troca de óleo", "Mecânico Sofia", 60);

        // Act
        execucao.adicionarTarefa(tarefa);

        // Assert
        assertEquals(1, execucao.getTarefas().size());
        assertEquals("Troca de óleo", execucao.getTarefas().get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve adicionar uso de peça")
    void deveAdicionarUsoPeca() {
        // Arrange
        ExecucaoOS execucao = ExecucaoOS.criar(UUID.randomUUID(), "Mecânico Roberto");
        UsoPeca usoPeca = new UsoPeca(UUID.randomUUID(), 2, new BigDecimal("35.50"));

        // Act
        execucao.adicionarUsoPeca(usoPeca);

        // Assert
        assertEquals(1, execucao.getPecas().size());
        assertEquals(new BigDecimal("71.00"), execucao.getPecas().get(0).getValorTotal());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios na criação")
    void deveValidarCamposObrigatoriosNaCriacao() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> ExecucaoOS.criar(null, "Mecânico"));

        assertThrows(IllegalArgumentException.class,
                () -> ExecucaoOS.criar(UUID.randomUUID(), null));

        assertThrows(IllegalArgumentException.class,
                () -> ExecucaoOS.criar(UUID.randomUUID(), ""));
    }
}
