package br.com.grupo99.customerservice.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade Cliente - representa clientes do sistema.
 * ID principal é pessoaId (vem do People Service).
 * Relaciona-se com múltiplos Veículos (1:N).
 */
@Entity
@Table(name = "clientes", indexes = {
        @Index(name = "idx_pessoa_id", columnList = "pessoa_id")
})
public class Cliente {

    @Id
    @Column(name = "pessoa_id")
    private UUID pessoaId;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Veiculo> veiculos = new ArrayList<>();

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
    public Cliente() {
    }

    public Cliente(UUID pessoaId) {
        if (pessoaId == null) {
            throw new IllegalArgumentException("Pessoa ID não pode ser nula");
        }
        this.pessoaId = pessoaId;
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não pode ser nulo");
        }
        veiculo.setCliente(this);
        this.veiculos.add(veiculo);
    }

    public void removerVeiculo(Veiculo veiculo) {
        if (veiculo != null) {
            this.veiculos.remove(veiculo);
            veiculo.setCliente(null);
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

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
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
        Cliente cliente = (Cliente) o;
        return Objects.equals(pessoaId, cliente.pessoaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pessoaId);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "pessoaId=" + pessoaId +
                ", veiculosCount=" + veiculos.size() +
                '}';
    }
}
