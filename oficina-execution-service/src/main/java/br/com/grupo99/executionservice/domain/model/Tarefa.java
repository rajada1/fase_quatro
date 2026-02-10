package br.com.grupo99.executionservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando uma Tarefa de execução.
 * 
 * Princípios de Clean Architecture:
 * - Sem dependências de Spring ou framework de persistência
 * - Lógica de negócio pura e independente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {

    private UUID id;
    private String descricao;
    private String mecanico;
    private Integer tempoEstimadoMinutos;
    private Integer tempoRealMinutos;
    private StatusTarefa status;
    private Instant dataInicio;
    private Instant dataFinalizacao;
    private Instant createdAt;

    /**
     * Construtor para testes (compatibilidade).
     *
     * @param descricao            descrição da tarefa
     * @param mecanico             mecânico responsável
     * @param tempoEstimadoMinutos tempo estimado em minutos
     */
    public Tarefa(String descricao, String mecanico, Integer tempoEstimadoMinutos) {
        if (descricao == null) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico é obrigatório");
        }

        if (descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        if (mecanico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mecânico não pode ser vazio");
        }
        if (tempoEstimadoMinutos != null && tempoEstimadoMinutos <= 0) {
            throw new IllegalArgumentException("Tempo estimado deve ser maior que zero");
        }

        this.id = UUID.randomUUID();
        this.descricao = descricao;
        this.mecanico = mecanico;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.status = StatusTarefa.PENDENTE;
        this.dataInicio = Instant.now();
        this.createdAt = Instant.now();
    }

    /**
     * Factory method para criar nova tarefa.
     *
     * @param descricao            descrição da tarefa
     * @param mecanico             mecânico responsável
     * @param tempoEstimadoMinutos tempo estimado em minutos
     * @return nova Tarefa
     */
    public static Tarefa criar(String descricao, String mecanico, Integer tempoEstimadoMinutos) {
        if (descricao == null) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico é obrigatório");
        }

        if (descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        if (mecanico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mecânico não pode ser vazio");
        }
        if (tempoEstimadoMinutos != null && tempoEstimadoMinutos <= 0) {
            throw new IllegalArgumentException("Tempo estimado deve ser maior que zero");
        }

        return Tarefa.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .mecanico(mecanico)
                .tempoEstimadoMinutos(tempoEstimadoMinutos)
                .status(StatusTarefa.PENDENTE)
                .dataInicio(Instant.now())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Inicia a tarefa.
     */
    public void iniciar() {
        if (this.status == StatusTarefa.EM_ANDAMENTO) {
            throw new IllegalStateException("Tarefa já está em andamento");
        }
        if (this.status == StatusTarefa.CONCLUIDA) {
            throw new IllegalStateException("Tarefa já foi concluída");
        }
        if (this.status == StatusTarefa.CANCELADA) {
            throw new IllegalStateException("Tarefa foi cancelada");
        }
        this.status = StatusTarefa.EM_ANDAMENTO;
    }

    /**
     * Finaliza a tarefa.
     *
     * @param tempoRealMinutos tempo real gasto
     */
    public void finalizar(Integer tempoRealMinutos) {
        if (this.status != StatusTarefa.EM_ANDAMENTO) {
            throw new IllegalStateException("Tarefa não está em andamento");
        }
        if (tempoRealMinutos != null && tempoRealMinutos <= 0) {
            throw new IllegalArgumentException("Tempo real deve ser maior que zero");
        }
        this.status = StatusTarefa.CONCLUIDA;
        this.tempoRealMinutos = tempoRealMinutos;
        this.dataFinalizacao = Instant.now();
    }

    /**
     * Finaliza a tarefa (sobrecarga para testes sem tempo real).
     */
    public void finalizar() {
        finalizar(this.tempoEstimadoMinutos);
    }

    /**
     * Cancela a tarefa.
     */
    public void cancelar() {
        this.status = StatusTarefa.CANCELADA;
    }

    /**
     * Verifica se a tarefa está em andamento.
     *
     * @return true se status é EM_ANDAMENTO
     */
    public boolean isEmAndamento() {
        return this.status == StatusTarefa.EM_ANDAMENTO;
    }

    /**
     * Verifica se a tarefa foi concluída.
     *
     * @return true se status é CONCLUIDA
     */
    public boolean isConcluida() {
        return this.status == StatusTarefa.CONCLUIDA;
    }

    /**
     * Verifica se a tarefa está pendente.
     *
     * @return true se status é PENDENTE
     */
    public boolean isPendente() {
        return this.status == StatusTarefa.PENDENTE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tarefa tarefa = (Tarefa) o;
        return Objects.equals(id, tarefa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", status=" + status +
                '}';
    }
}
