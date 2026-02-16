package br.com.grupo99.executionservice.infrastructure.persistence.entity;

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
 * JPA Entity para Diagnóstico (persistência PostgreSQL).
 * 
 * Esta entidade é responsável apenas por persistência.
 */
@Entity
@Table(name = "diagnosticos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DiagnosticoEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execucao_id", nullable = false)
    private ExecucaoOSEntity execucaoOSEntity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 100)
    private String mecanico;

    @Column(name = "data_diagnostico", nullable = false)
    private Instant dataDiagnostico;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
