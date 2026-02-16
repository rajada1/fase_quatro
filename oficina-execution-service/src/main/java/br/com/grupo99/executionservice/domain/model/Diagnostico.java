package br.com.grupo99.executionservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando um Diagnóstico de execução.
 * 
 * Princípios de Clean Architecture:
 * - Sem dependências de Spring ou framework de persistência
 * - Lógica de negócio pura e independente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diagnostico {

    private UUID id;
    private String descricao;
    private String mecanico;
    private Instant dataDiagnostico;
    private String observacoes;
    private Instant createdAt;

    /**
     * Construtor para testes (compatibilidade).
     *
     * @param descricao descrição do diagnóstico
     * @param mecanico  mecânico responsável
     */
    public Diagnostico(String descricao, String mecanico) {
        if (descricao == null) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico é obrigatório");
        }

        if (descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        if (mecanico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mecânico não pode ser vazio");
        }

        this.id = UUID.randomUUID();
        this.descricao = descricao;
        this.mecanico = mecanico;
        this.dataDiagnostico = Instant.now();
        this.createdAt = Instant.now();
    }

    /**
     * Factory method para criar novo diagnóstico.
     *
     * @param descricao   descrição do diagnóstico
     * @param mecanico    mecânico responsável
     * @param observacoes observações opcionais
     * @return novo Diagnostico
     */
    public static Diagnostico criar(String descricao, String mecanico, String observacoes) {
        if (descricao == null) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico é obrigatório");
        }

        if (descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        if (mecanico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mecânico não pode ser vazio");
        }

        return Diagnostico.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .mecanico(mecanico)
                .dataDiagnostico(Instant.now())
                .observacoes(observacoes)
                .createdAt(Instant.now())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Diagnostico that = (Diagnostico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Diagnostico{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", mecanico='" + mecanico + '\'' +
                '}';
    }
}
