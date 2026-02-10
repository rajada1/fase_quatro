package br.com.grupo99.executionservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tarefa - Testes Unitários")
class TarefaTest {

    @Test
    @DisplayName("Deve criar tarefa com status PENDENTE")
    void deveCriarTarefaComStatusPendente() {
        // Arrange & Act
        Tarefa tarefa = new Tarefa("Troca de óleo", "Mecânico João", 60);

        // Assert
        assertNotNull(tarefa);
        assertEquals("Troca de óleo", tarefa.getDescricao());
        assertEquals("Mecânico João", tarefa.getMecanico());
        assertEquals(60, tarefa.getTempoEstimadoMinutos());
        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
        assertNull(tarefa.getTempoRealMinutos());
    }

    @Test
    @DisplayName("Deve iniciar tarefa")
    void deveIniciarTarefa() {
        // Arrange
        Tarefa tarefa = new Tarefa("Alinhamento", "Mecânico Pedro", 45);

        // Act
        tarefa.iniciar();

        // Assert
        assertEquals(StatusTarefa.EM_ANDAMENTO, tarefa.getStatus());
    }

    @Test
    @DisplayName("Deve finalizar tarefa com tempo real")
    void deveFinalizarTarefaComTempoReal() {
        // Arrange
        Tarefa tarefa = new Tarefa("Balanceamento", "Mecânico Ana", 30);
        tarefa.iniciar();

        // Act
        tarefa.finalizar(35);

        // Assert
        assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
        assertEquals(35, tarefa.getTempoRealMinutos());
    }

    @Test
    @DisplayName("Deve cancelar tarefa")
    void deveCancelarTarefa() {
        // Arrange
        Tarefa tarefa = new Tarefa("Revisão", "Mecânico Carlos", 90);

        // Act
        tarefa.cancelar();

        // Assert
        assertEquals(StatusTarefa.CANCELADA, tarefa.getStatus());
    }

    @Test
    @DisplayName("Não deve iniciar tarefa já iniciada")
    void naoDeveIniciarTarefaJaIniciada() {
        // Arrange
        Tarefa tarefa = new Tarefa("Teste", "Mecânico", 30);
        tarefa.iniciar();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> tarefa.iniciar());
    }

    @Test
    @DisplayName("Não deve finalizar tarefa não iniciada")
    void naoDeveFinalizarTarefaNaoIniciada() {
        // Arrange
        Tarefa tarefa = new Tarefa("Teste", "Mecânico", 30);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> tarefa.finalizar(30));
    }

    @Test
    @DisplayName("Não deve finalizar tarefa com tempo real negativo")
    void naoDeveFinalizarTarefaComTempoRealNegativo() {
        // Arrange
        Tarefa tarefa = new Tarefa("Teste", "Mecânico", 30);
        tarefa.iniciar();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tarefa.finalizar(-10));
    }
}
