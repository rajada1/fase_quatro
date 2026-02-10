package br.com.grupo99.hrservice.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade Funcionario - representa funcionários da organização.
 * ID principal é pessoaId (vem do People Service).
 */
@Entity
@Table(name = "funcionarios", indexes = {
        @Index(name = "idx_pessoa_id", columnList = "pessoa_id")
})
public class Funcionario {

    @Id
    @Column(name = "pessoa_id")
    private UUID pessoaId;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(name = "setor", nullable = false, length = 100)
    private String setor;

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    @Column(name = "salario", nullable = false)
    private BigDecimal salario;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Construtores
    public Funcionario() {
    }

    public Funcionario(UUID pessoaId, LocalDate dataAdmissao, String setor, String cargo, BigDecimal salario) {
        validarCamposObrigatorios(pessoaId, dataAdmissao, setor, cargo, salario);
        this.pessoaId = pessoaId;
        this.dataAdmissao = dataAdmissao;
        this.setor = setor;
        this.cargo = cargo;
        this.salario = salario;
    }

    private void validarCamposObrigatorios(UUID pessoaId, LocalDate dataAdmissao, String setor, String cargo,
            BigDecimal salario) {
        if (pessoaId == null) {
            throw new IllegalArgumentException("Pessoa ID não pode ser nula");
        }
        if (dataAdmissao == null) {
            throw new IllegalArgumentException("Data de admissão não pode ser nula");
        }
        if (setor == null || setor.trim().isEmpty()) {
            throw new IllegalArgumentException("Setor não pode ser nulo ou vazio");
        }
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser nulo ou vazio");
        }
        if (salario == null || salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salário deve ser maior que zero");
        }
    }

    // Getters e Setters
    public UUID getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(UUID pessoaId) {
        if (pessoaId == null) {
            throw new IllegalArgumentException("Pessoa ID não pode ser nula");
        }
        this.pessoaId = pessoaId;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(pessoaId, that.pessoaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pessoaId);
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "pessoaId=" + pessoaId +
                ", setor='" + setor + '\'' +
                ", cargo='" + cargo + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
