package br.com.grupo99.executionservice.domain.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representando os possíveis status de uma Execução.
 */
@Getter
public enum StatusExecucao {

    AGUARDANDO_INICIO("Aguardando início"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusExecucao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Valida se uma transição de status é permitida.
     *
     * @param novoStatus status destino
     * @return true se transição é válida
     */
    public boolean podeTransicionarPara(StatusExecucao novoStatus) {
        return switch (this) {
            case AGUARDANDO_INICIO -> novoStatus == EM_ANDAMENTO || novoStatus == CANCELADA;
            case EM_ANDAMENTO -> novoStatus == CONCLUIDA || novoStatus == CANCELADA;
            case CONCLUIDA, CANCELADA -> false; // Estados finais
        };
    }

    /**
     * Retorna os próximos status possíveis a partir do status atual.
     *
     * @return lista de status válidos
     */
    public List<StatusExecucao> proximosStatusPermitidos() {
        return switch (this) {
            case AGUARDANDO_INICIO -> Arrays.asList(EM_ANDAMENTO, CANCELADA);
            case EM_ANDAMENTO -> Arrays.asList(CONCLUIDA, CANCELADA);
            case CONCLUIDA, CANCELADA -> List.of();
        };
    }

    /**
     * Verifica se o status é final.
     *
     * @return true se é um status final
     */
    public boolean isFinal() {
        return this == CONCLUIDA || this == CANCELADA;
    }
}
