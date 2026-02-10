package br.com.grupo99.executionservice.infrastructure.persistence.entity;

import br.com.grupo99.executionservice.domain.model.StatusTarefa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity para Tarefa (persistência PostgreSQL).
 * 
 * Esta entidade é responsável apenas por persistência.
 */
@Entity
@Table(name = "tarefas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TarefaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execucao_id", nullable = false)
    private ExecucaoOSEntity execucaoOSEntity;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String mecanico;

    @Column(name = "tempo_estimado_minutos")
    private Integer tempoEstimadoMinutos;

    @Column(name = "tempo_real_minutos")
    private Integer tempoRealMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusTarefa status = StatusTarefa.PENDENTE;

    @Column(name = "data_inicio", nullable = false)
    private Instant dataInicio;

    @Column(name = "data_finalizacao")
    private Instant dataFinalizacao;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
