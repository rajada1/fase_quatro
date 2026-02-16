package br.com.grupo99.executionservice.infrastructure.persistence.entity;

import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity para Execução de OS (persistência PostgreSQL).
 * 
 * Esta entidade é responsável apenas por persistência.
 * O mapeamento entre domínio ↔ entidade é feito pela camada de infraestrutura.
 */
@Entity
@Table(name = "execucoes_os")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ExecucaoOSEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "os_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID osId;

    @Column(name = "orcamento_id", nullable = false, columnDefinition = "UUID")
    private UUID orcamentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusExecucao status = StatusExecucao.AGUARDANDO_INICIO;

    @Column(nullable = false, length = 100)
    private String mecanico;

    @Column(name = "data_inicio", nullable = false)
    private Instant dataInicio;

    @Column(name = "data_finalizacao")
    private Instant dataFinalizacao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Version
    @Builder.Default
    private Long version = 0L;

    @OneToMany(mappedBy = "execucaoOSEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiagnosticoEntity> diagnosticos = new ArrayList<>();

    @OneToMany(mappedBy = "execucaoOSEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TarefaEntity> tarefas = new ArrayList<>();

    @OneToMany(mappedBy = "execucaoOSEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsoPecaEntity> pecas = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
