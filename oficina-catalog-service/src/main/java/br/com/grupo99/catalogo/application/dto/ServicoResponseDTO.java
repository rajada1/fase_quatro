package br.com.grupo99.catalogo.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.grupo99.catalogo.domain.model.Servico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para Serviço.
 * Migrado para MongoDB/DocumentDB - ID agora é String.
 * Inclui novos campos flexíveis do MongoDB.
 */
public class ServicoResponseDTO {
    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer tempoEstimadoMinutos;
    private Boolean ativo;
    private List<String> categorias;
    private List<String> pecasNecessarias;
    private List<String> requisitos;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public ServicoResponseDTO() {
    }

    public ServicoResponseDTO(String id, String nome, String descricao, BigDecimal preco, Integer tempoEstimadoMinutos,
            Boolean ativo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.ativo = ativo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServicoResponseDTO fromDomain(Servico servico) {
        ServicoResponseDTO dto = new ServicoResponseDTO(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getTempoEstimadoMinutos(),
                servico.getAtivo(),
                servico.getCreatedAt(),
                servico.getUpdatedAt());
        dto.setCategorias(servico.getCategorias());
        dto.setPecasNecessarias(servico.getPecasNecessarias());
        dto.setRequisitos(servico.getRequisitos());
        return dto;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public Boolean getAtivo() {
        return ativo;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
