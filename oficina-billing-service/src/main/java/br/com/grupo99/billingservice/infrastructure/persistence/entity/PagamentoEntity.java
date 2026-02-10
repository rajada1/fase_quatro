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

/**
 * Entity MongoDB para Pagamento
 * 
 * ✅ CLEAN ARCHITECTURE: Entity fica na infrastructure layer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pagamentos")
public class PagamentoEntity {

    @Id
    private String id;

    private String orcamentoId;

    private String osId;

    @Builder.Default
    private String status = "PENDENTE";

    private BigDecimal valor;

    private String formaPagamento;

    private String comprovante;

    private Instant dataPagamento;

    private Instant dataEstorno;

    private String motivoEstorno;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
