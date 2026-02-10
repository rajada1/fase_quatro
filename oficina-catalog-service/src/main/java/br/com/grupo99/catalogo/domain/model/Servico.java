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
import java.util.UUID;

/**
 * Documento MongoDB para Serviço.
 * 
 * Migrado de JPA/PostgreSQL para MongoDB/DocumentDB para:
 * - Schema flexível
 * - Melhor performance em leituras de catálogo
 * - Suporte nativo a campos aninhados e arrays
 */
@Document(collection = "servicos")
public class Servico {

    @Id
    private String id;

    @Field("nome")
    @Indexed
    private String nome;

    @Field("descricao")
    private String descricao;

    @Field("preco")
    private BigDecimal preco;

    @Field("tempo_estimado_minutos")
    private Integer tempoEstimadoMinutos;

    @Field("ativo")
    @Indexed
    private Boolean ativo = true;

    // Campos flexíveis para MongoDB - aproveitando schema-less
    @Field("categorias")
    private List<String> categorias;

    @Field("pecas_necessarias")
    private List<String> pecasNecessarias;

    @Field("requisitos")
    private List<String> requisitos;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public Servico() {
        this.id = UUID.randomUUID().toString();
    }

    public Servico(String nome, String descricao, BigDecimal preco, Integer tempoEstimadoMinutos) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.ativo = true;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public void setTempoEstimadoMinutos(Integer tempoEstimadoMinutos) {
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
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

    public List<String> getPecasNecessarias() {
        return pecasNecessarias;
    }

    public void setPecasNecessarias(List<String> pecasNecessarias) {
        this.pecasNecessarias = pecasNecessarias;
    }

    public List<String> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<String> requisitos) {
        this.requisitos = requisitos;
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
        Servico servico = (Servico) o;
        return id != null && id.equals(servico.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
