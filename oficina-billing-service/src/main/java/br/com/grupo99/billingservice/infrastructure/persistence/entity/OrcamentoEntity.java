package br.com.grupo99.billingservice.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity MongoDB para Orcamento
 * 
 * ✅ CLEAN ARCHITECTURE: Entity fica na infrastructure layer
 * Domain models não conhecem JPA/MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orcamentos")
public class OrcamentoEntity {

    @Id
    private String id;

    private String osId;

    @Builder.Default
    private String status = "PENDENTE";

    @Builder.Default
    private List<ItemOrcamentoEntity> itens = new ArrayList<>();

    private BigDecimal valorTotal;

    private Instant dataGeracao;

    private Instant dataAprovacao;

    private Instant dataRejeicao;

    private String observacao;

    private String motivoRejeicao;

    @Builder.Default
    private List<HistoricoStatusEntity> historico = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Item do Orcamento (nested)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrcamentoEntity {
        private String tipo;
        private String descricao;
        private BigDecimal valorUnitario;
        private BigDecimal valorTotal;
        private Integer quantidade;
    }

    /**
     * Histórico de status (nested)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoricoStatusEntity {
        private String statusAnterior;
        private String novoStatus;
        private String usuario;
        private String observacao;
        private Instant data;
    }
}
