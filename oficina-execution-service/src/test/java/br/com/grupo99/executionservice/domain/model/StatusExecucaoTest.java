package br.com.grupo99.executionservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StatusExecucao - Testes Unitários")
class StatusExecucaoTest {

    @Test
    @DisplayName("AGUARDANDO_INICIO pode transicionar para EM_ANDAMENTO")
    void aguardandoInicioPodeTransicionarParaEmAndamento() {
        assertThat(StatusExecucao.AGUARDANDO_INICIO.podeTransicionarPara(StatusExecucao.EM_ANDAMENTO)).isTrue();
    }

    @Test
    @DisplayName("AGUARDANDO_INICIO pode transicionar para CANCELADA")
    void aguardandoInicioPodeTransicionarParaCancelada() {
        assertThat(StatusExecucao.AGUARDANDO_INICIO.podeTransicionarPara(StatusExecucao.CANCELADA)).isTrue();
    }

    @Test
    @DisplayName("AGUARDANDO_INICIO não pode transicionar para CONCLUIDA")
    void aguardandoInicioNaoPodeTransicionarParaConcluida() {
        assertThat(StatusExecucao.AGUARDANDO_INICIO.podeTransicionarPara(StatusExecucao.CONCLUIDA)).isFalse();
    }

    @Test
    @DisplayName("EM_ANDAMENTO pode transicionar para CONCLUIDA")
    void emAndamentoPodeTransicionarParaConcluida() {
        assertThat(StatusExecucao.EM_ANDAMENTO.podeTransicionarPara(StatusExecucao.CONCLUIDA)).isTrue();
    }

    @Test
    @DisplayName("EM_ANDAMENTO pode transicionar para CANCELADA")
    void emAndamentoPodeTransicionarParaCancelada() {
        assertThat(StatusExecucao.EM_ANDAMENTO.podeTransicionarPara(StatusExecucao.CANCELADA)).isTrue();
    }

    @Test
    @DisplayName("EM_ANDAMENTO não pode transicionar para AGUARDANDO_INICIO")
    void emAndamentoNaoPodeTransicionarParaAguardandoInicio() {
        assertThat(StatusExecucao.EM_ANDAMENTO.podeTransicionarPara(StatusExecucao.AGUARDANDO_INICIO)).isFalse();
    }

    @Test
    @DisplayName("CONCLUIDA não pode transicionar para nenhum status")
    void concluidaNaoPodeTransicionar() {
        for (StatusExecucao status : StatusExecucao.values()) {
            assertThat(StatusExecucao.CONCLUIDA.podeTransicionarPara(status)).isFalse();
        }
    }

    @Test
    @DisplayName("CANCELADA não pode transicionar para nenhum status")
    void canceladaNaoPodeTransicionar() {
        for (StatusExecucao status : StatusExecucao.values()) {
            assertThat(StatusExecucao.CANCELADA.podeTransicionarPara(status)).isFalse();
        }
    }

    @Test
    @DisplayName("Deve retornar próximos status permitidos para AGUARDANDO_INICIO")
    void deveRetornarProximosStatusAguardandoInicio() {
        List<StatusExecucao> proximos = StatusExecucao.AGUARDANDO_INICIO.proximosStatusPermitidos();

        assertThat(proximos).containsExactly(StatusExecucao.EM_ANDAMENTO, StatusExecucao.CANCELADA);
    }

    @Test
    @DisplayName("Deve retornar próximos status permitidos para EM_ANDAMENTO")
    void deveRetornarProximosStatusEmAndamento() {
        List<StatusExecucao> proximos = StatusExecucao.EM_ANDAMENTO.proximosStatusPermitidos();

        assertThat(proximos).containsExactly(StatusExecucao.CONCLUIDA, StatusExecucao.CANCELADA);
    }

    @Test
    @DisplayName("Deve retornar lista vazia para CONCLUIDA")
    void deveRetornarListaVaziaParaConcluida() {
        assertThat(StatusExecucao.CONCLUIDA.proximosStatusPermitidos()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia para CANCELADA")
    void deveRetornarListaVaziaParaCancelada() {
        assertThat(StatusExecucao.CANCELADA.proximosStatusPermitidos()).isEmpty();
    }

    @Test
    @DisplayName("CONCLUIDA e CANCELADA são estados finais")
    void concluidaECanceladaSaoEstadosFinais() {
        assertThat(StatusExecucao.CONCLUIDA.isFinal()).isTrue();
        assertThat(StatusExecucao.CANCELADA.isFinal()).isTrue();
    }

    @Test
    @DisplayName("AGUARDANDO_INICIO e EM_ANDAMENTO não são estados finais")
    void aguardandoInicioEEmAndamentoNaoSaoEstadosFinais() {
        assertThat(StatusExecucao.AGUARDANDO_INICIO.isFinal()).isFalse();
        assertThat(StatusExecucao.EM_ANDAMENTO.isFinal()).isFalse();
    }

    @Test
    @DisplayName("Deve ter descrição para cada status")
    void deveTerDescricaoParaCadaStatus() {
        assertThat(StatusExecucao.AGUARDANDO_INICIO.getDescricao()).isEqualTo("Aguardando início");
        assertThat(StatusExecucao.EM_ANDAMENTO.getDescricao()).isEqualTo("Em andamento");
        assertThat(StatusExecucao.CONCLUIDA.getDescricao()).isEqualTo("Concluída");
        assertThat(StatusExecucao.CANCELADA.getDescricao()).isEqualTo("Cancelada");
    }
}
