package br.com.grupo99.catalogo.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Documento MongoDB para Peça.
 * 
 * Migrado de JPA/PostgreSQL para MongoDB/DocumentDB para:
 * - Schema flexível (especificações variam por peça)
 * - Melhor performance em leituras de catálogo
 * - Suporte nativo a campos aninhados e arrays
 */
@Document(collection = "pecas")
public class Peca {

    @Id
    private String id;

    @Field("nome")
    @Indexed
    private String nome;

    @Field("descricao")
    private String descricao;

    @Field("codigo_fabricante")
    @Indexed(unique = true)
    private String codigoFabricante;

    @Field("preco")
    private BigDecimal preco;

    @Field("quantidade")
    private Integer quantidade;

    @Field("quantidade_minima")
    private Integer quantidadeMinima = 5;

    @Field("ativo")
    @Indexed
    private Boolean ativo = true;

    // Campos flexíveis para MongoDB - aproveitando schema-less
    @Field("categorias")
    private List<String> categorias;

    @Field("especificacoes")
    private Map<String, Object> especificacoes;

    @Field("compatibilidade")
    private List<String> compatibilidade;

    @Field("marca")
    private String marca;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public Peca() {
        this.id = UUID.randomUUID().toString();
    }

    public Peca(String nome, String descricao, String codigoFabricante, BigDecimal preco, Integer quantidade) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.codigoFabricante = codigoFabricante;
        this.preco = preco;
        this.quantidade = quantidade;
        this.ativo = true;
    }

    public void decrementarQuantidade(Integer quantidade) {
        if (this.quantidade < quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque");
        }
        this.quantidade -= quantidade;
    }

    public void incrementarQuantidade(Integer quantidade) {
        this.quantidade += quantidade;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoFabricante() {
        return codigoFabricante;
    }

    public void setCodigoFabricante(String codigoFabricante) {
        this.codigoFabricante = codigoFabricante;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public Map<String, Object> getEspecificacoes() {
        return especificacoes;
    }

    public void setEspecificacoes(Map<String, Object> especificacoes) {
        this.especificacoes = especificacoes;
    }

    public List<String> getCompatibilidade() {
        return compatibilidade;
    }

    public void setCompatibilidade(List<String> compatibilidade) {
        this.compatibilidade = compatibilidade;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Peca peca = (Peca) o;
        return id != null && id.equals(peca.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
