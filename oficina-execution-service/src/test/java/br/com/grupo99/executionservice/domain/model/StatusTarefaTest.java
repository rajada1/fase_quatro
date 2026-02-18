package br.com.grupo99.executionservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StatusTarefa - Testes Unitários")
class StatusTarefaTest {

    @Test
    @DisplayName("PENDENTE não é estado final")
    void pendenteNaoEEstadoFinal() {
        assertThat(StatusTarefa.PENDENTE.isFinal()).isFalse();
    }

    @Test
    @DisplayName("EM_ANDAMENTO não é estado final")
    void emAndamentoNaoEEstadoFinal() {
        assertThat(StatusTarefa.EM_ANDAMENTO.isFinal()).isFalse();
    }

    @Test
    @DisplayName("CONCLUIDA é estado final")
    void concluidaEEstadoFinal() {
        assertThat(StatusTarefa.CONCLUIDA.isFinal()).isTrue();
    }

    @Test
    @DisplayName("CANCELADA é estado final")
    void canceladaEEstadoFinal() {
        assertThat(StatusTarefa.CANCELADA.isFinal()).isTrue();
    }

    @Test
    @DisplayName("Deve ter descrição para cada status")
    void deveTerDescricaoParaCadaStatus() {
        assertThat(StatusTarefa.PENDENTE.getDescricao()).isEqualTo("Pendente");
        assertThat(StatusTarefa.EM_ANDAMENTO.getDescricao()).isEqualTo("Em andamento");
        assertThat(StatusTarefa.CONCLUIDA.getDescricao()).isEqualTo("Concluída");
        assertThat(StatusTarefa.CANCELADA.getDescricao()).isEqualTo("Cancelada");
    }

    @Test
    @DisplayName("Deve ter 4 valores no enum")
    void deveTer4ValoresNoEnum() {
        assertThat(StatusTarefa.values()).hasSize(4);
    }

    @Test
    @DisplayName("Deve converter de string via valueOf")
    void deveConverterDeStringViaValueOf() {
        assertThat(StatusTarefa.valueOf("PENDENTE")).isEqualTo(StatusTarefa.PENDENTE);
        assertThat(StatusTarefa.valueOf("EM_ANDAMENTO")).isEqualTo(StatusTarefa.EM_ANDAMENTO);
        assertThat(StatusTarefa.valueOf("CONCLUIDA")).isEqualTo(StatusTarefa.CONCLUIDA);
        assertThat(StatusTarefa.valueOf("CANCELADA")).isEqualTo(StatusTarefa.CANCELADA);
    }
}
