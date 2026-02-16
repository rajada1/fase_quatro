package br.com.grupo99.executionservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando o uso de uma peça em uma execução.
 * 
 * Princípios de Clean Architecture:
 * - Sem dependências de Spring ou framework de persistência
 * - Lógica de negócio pura e independente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsoPeca {

    private UUID id;
    private UUID pecaId;
    private String descricao;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private Instant dataUso;
    private Instant createdAt;

    /**
     * Construtor para testes (compatibilidade).
     *
     * @param pecaId        ID da peça
     * @param quantidade    quantidade utilizada
     * @param valorUnitario valor unitário
     */
    public UsoPeca(UUID pecaId, Integer quantidade, BigDecimal valorUnitario) {
        if (pecaId == null) {
            throw new IllegalArgumentException("ID da peça é obrigatório");
        }
        if (quantidade == null) {
            throw new IllegalArgumentException("Quantidade é obrigatória");
        }
        if (valorUnitario == null) {
            throw new IllegalArgumentException("Valor unitário é obrigatório");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor unitário deve ser maior que zero");
        }

        this.id = UUID.randomUUID();
        this.pecaId = pecaId;
        this.descricao = "Peça " + pecaId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
        this.dataUso = Instant.now();
        this.createdAt = Instant.now();
    }

    /**
     * Factory method para criar novo uso de peça.
     *
     * @param pecaId        ID da peça
     * @param descricao     descrição da peça
     * @param quantidade    quantidade utilizada
     * @param valorUnitario valor unitário
     * @return novo UsoPeca
     */
    public static UsoPeca criar(UUID pecaId, String descricao, Integer quantidade, BigDecimal valorUnitario) {
        if (pecaId == null) {
            throw new IllegalArgumentException("ID da peça é obrigatório");
        }
        if (descricao == null) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (quantidade == null) {
            throw new IllegalArgumentException("Quantidade é obrigatória");
        }
        if (valorUnitario == null) {
            throw new IllegalArgumentException("Valor unitário é obrigatório");
        }

        if (descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor unitário deve ser maior que zero");
        }

        BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));

        return UsoPeca.builder()
                .id(UUID.randomUUID())
                .pecaId(pecaId)
                .descricao(descricao)
                .quantidade(quantidade)
                .valorUnitario(valorUnitario)
                .valorTotal(valorTotal)
                .dataUso(Instant.now())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Recalcula o valor total.
     */
    public void recalcularValorTotal() {
        this.valorTotal = this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UsoPeca usoPeca = (UsoPeca) o;
        return Objects.equals(id, usoPeca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UsoPeca{" +
                "id=" + id +
                ", pecaId=" + pecaId +
                ", descricao='" + descricao + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }
}
