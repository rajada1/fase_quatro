package br.com.grupo99.catalogo.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.grupo99.catalogo.domain.model.Peca;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO de resposta para Peça.
 * Migrado para MongoDB/DocumentDB - ID agora é String.
 * Inclui novos campos flexíveis do MongoDB.
 */
public class PecaResponseDTO {
    private String id;
    private String nome;
    private String descricao;
    private String codigoFabricante;
    private BigDecimal preco;
    private Integer quantidade;
    private Integer quantidadeMinima;
    private Boolean ativo;
    private List<String> categorias;
    private Map<String, Object> especificacoes;
    private List<String> compatibilidade;
    private String marca;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public PecaResponseDTO() {
    }

    public PecaResponseDTO(String id, String nome, String descricao, String codigoFabricante, BigDecimal preco,
            Integer quantidade, Integer quantidadeMinima, Boolean ativo, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.codigoFabricante = codigoFabricante;
        this.preco = preco;
        this.quantidade = quantidade;
        this.quantidadeMinima = quantidadeMinima;
        this.ativo = ativo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PecaResponseDTO fromDomain(Peca peca) {
        PecaResponseDTO dto = new PecaResponseDTO(
                peca.getId(),
                peca.getNome(),
                peca.getDescricao(),
                peca.getCodigoFabricante(),
                peca.getPreco(),
                peca.getQuantidade(),
                peca.getQuantidadeMinima(),
                peca.getAtivo(),
                peca.getCreatedAt(),
                peca.getUpdatedAt());
        dto.setCategorias(peca.getCategorias());
        dto.setEspecificacoes(peca.getEspecificacoes());
        dto.setCompatibilidade(peca.getCompatibilidade());
        dto.setMarca(peca.getMarca());
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

    public String getCodigoFabricante() {
        return codigoFabricante;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
