package br.com.grupo99.executionservice.domain.model;

import lombok.Getter;

/**
 * Enum representando os possíveis status de uma Tarefa.
 */
@Getter
public enum StatusTarefa {

    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
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
