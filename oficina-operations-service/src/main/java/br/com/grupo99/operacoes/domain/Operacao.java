package br.com.grupo99.operacoes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operacao {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "numero_operacao", nullable = false, unique = true)
    private String numeroOperacao;

    @Column(name = "orcamento_id", nullable = false)
    private UUID orcamentoId;

    @Column(name = "manutencao_id")
    private UUID manutencaoId;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusOperacao status;

    @Column(name = "prioridade", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrioridadeOperacao prioridade;

    @Column(name = "responsavel_id")
    private UUID responsavelId;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "tempo_estimado_horas")
    private Integer tempoEstimadoHoras;

    @Column(name = "tempo_realizado_horas")
    private Integer tempoRealizadoHoras;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = StatusOperacao.PLANEJADA;
        this.prioridade = PrioridadeOperacao.NORMAL;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
