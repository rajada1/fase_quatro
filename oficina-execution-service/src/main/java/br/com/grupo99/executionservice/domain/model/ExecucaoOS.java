package br.com.grupo99.executionservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representando uma Execução de Ordem de Serviço.
 * Gerencia diagnósticos, tarefas e uso de peças.
 * 
 * Princípios de Clean Architecture:
 * - Sem dependências de Spring ou framework de persistência
 * - Lógica de negócio pura e independente
 * - IDs utilizados como UUID para identificação tecnológica
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecucaoOS {

    private UUID id;
    private UUID osId;
    private UUID orcamentoId;
    private StatusExecucao status;
    private String mecanico;
    private Instant dataInicio;
    private Instant dataFinalizacao;
    private String observacoes;
    private Long version;

    @Builder.Default
    private List<Diagnostico> diagnosticos = new ArrayList<>();

    @Builder.Default
    private List<Tarefa> tarefas = new ArrayList<>();

    @Builder.Default
    private List<UsoPeca> pecas = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Factory method para criar nova execução.
     *
     * @param osId        ID da OS
     * @param orcamentoId ID do orçamento
     * @param mecanico    mecânico responsável
     * @return nova ExecucaoOS
     */
    public static ExecucaoOS criar(UUID osId, UUID orcamentoId, String mecanico) {
        if (osId == null) {
            throw new IllegalArgumentException("ID da OS é obrigatório");
        }
        if (orcamentoId == null) {
            throw new IllegalArgumentException("ID do orçamento é obrigatório");
        }
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico é obrigatório");
        }

        if (mecanico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mecânico não pode ser vazio");
        }

        return ExecucaoOS.builder()
                .id(UUID.randomUUID())
                .osId(osId)
                .orcamentoId(orcamentoId)
                .status(StatusExecucao.AGUARDANDO_INICIO)
                .mecanico(mecanico)
                .dataInicio(Instant.now())
                .diagnosticos(new ArrayList<>())
                .tarefas(new ArrayList<>())
                .pecas(new ArrayList<>())
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Factory method para criar nova execução (sobrecarga para testes - apenas osId
     * e mecânico).
     *
     * @param osId     ID da OS
     * @param mecanico mecânico responsável
     * @return nova ExecucaoOS
     */
    public static ExecucaoOS criar(UUID osId, String mecanico) {
        if (osId == null) {
            throw new IllegalArgumentException("ID da OS é obrigatório");
        }
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico é obrigatório");
        }

        if (mecanico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mecânico não pode ser vazio");
        }

        return ExecucaoOS.builder()
                .id(UUID.randomUUID())
                .osId(osId)
                .orcamentoId(UUID.randomUUID())
                .status(StatusExecucao.AGUARDANDO_INICIO)
                .mecanico(mecanico)
                .dataInicio(Instant.now())
                .diagnosticos(new ArrayList<>())
                .tarefas(new ArrayList<>())
                .pecas(new ArrayList<>())
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Inicia a execução.
     *
     * @throws IllegalStateException se transição não for válida
     */
    public void iniciar() {
        validarTransicao(StatusExecucao.EM_ANDAMENTO);
        this.status = StatusExecucao.EM_ANDAMENTO;
        this.updatedAt = Instant.now();
    }

    /**
     * Finaliza a execução.
     *
     * @param observacoes observações finais
     * @throws IllegalStateException se transição não for válida
     */
    public void finalizar(String observacoes) {
        validarTransicao(StatusExecucao.CONCLUIDA);
        this.status = StatusExecucao.CONCLUIDA;
        this.dataFinalizacao = Instant.now();
        this.observacoes = observacoes;
        this.updatedAt = Instant.now();
    }

    /**
     * Finaliza a execução (sobrecarga para testes sem observações).
     *
     * @throws IllegalStateException se transição não for válida
     */
    public void finalizar() {
        finalizar("Execução finalizada");
    }

    /**
     * Cancela a execução.
     *
     * @param motivo motivo do cancelamento
     * @throws IllegalStateException se transição não for válida
     */
    public void cancelar(String motivo) {
        validarTransicao(StatusExecucao.CANCELADA);
        this.status = StatusExecucao.CANCELADA;
        this.observacoes = motivo;
        this.updatedAt = Instant.now();
    }

    /**
     * Cancela a execução (sobrecarga para testes sem motivo explícito).
     *
     * @throws IllegalStateException se transição não for válida
     */
    public void cancelar() {
        cancelar("Execução cancelada");
    }

    /**
     * Adiciona um diagnóstico à execução.
     *
     * @param diagnostico diagnóstico a adicionar
     */
    public void adicionarDiagnostico(Diagnostico diagnostico) {
        Objects.requireNonNull(diagnostico, "Diagnóstico não pode ser nulo");
        this.diagnosticos.add(diagnostico);
        this.updatedAt = Instant.now();
    }

    /**
     * Adiciona uma tarefa à execução.
     *
     * @param tarefa tarefa a adicionar
     */
    public void adicionarTarefa(Tarefa tarefa) {
        Objects.requireNonNull(tarefa, "Tarefa não pode ser nula");
        this.tarefas.add(tarefa);
        this.updatedAt = Instant.now();
    }

    /**
     * Adiciona uso de peça à execução.
     *
     * @param usoPeca uso de peça a adicionar
     */
    public void adicionarUsoPeca(UsoPeca usoPeca) {
        Objects.requireNonNull(usoPeca, "Uso de peça não pode ser nulo");
        this.pecas.add(usoPeca);
        this.updatedAt = Instant.now();
    }

    /**
     * Valida se a transição de status é permitida.
     *
     * @param novoStatus novo status desejado
     * @throws IllegalStateException se transição não for válida
     */
    private void validarTransicao(StatusExecucao novoStatus) {
        if (!this.status.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException(
                    String.format("Transição inválida de %s para %s", this.status, novoStatus));
        }
    }

    /**
     * Verifica se a execução está em andamento.
     *
     * @return true se status é EM_ANDAMENTO
     */
    public boolean isEmAndamento() {
        return this.status == StatusExecucao.EM_ANDAMENTO;
    }

    /**
     * Verifica se a execução foi concluída.
     *
     * @return true se status é CONCLUIDA
     */
    public boolean isConcluida() {
        return this.status == StatusExecucao.CONCLUIDA;
    }

    /**
     * Retorna a data de criação.
     *
     * @return data de criação
     */
    public Instant getDataCriacao() {
        return this.createdAt;
    }

    /**
     * Retorna a data de conclusão.
     *
     * @return data de conclusão
     */
    public Instant getDataConclusao() {
        return this.dataFinalizacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ExecucaoOS that = (ExecucaoOS) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExecucaoOS{" +
                "id=" + id +
                ", osId=" + osId +
                ", status=" + status +
                ", mecanico='" + mecanico + '\'' +
                '}';
    }
}
