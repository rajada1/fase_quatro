package br.com.grupo99.peopleservice.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade Pessoa - Raiz agregada centralizada de pessoas no sistema.
 * Representa todos os usuários: clientes, funcionários, administradores.
 * Cada pessoa tem um perfil que define seu tipo de acesso.
 */
@Entity
@Table(name = "pessoas", indexes = {
    @Index(name = "idx_numero_documento", columnList = "numero_documento"),
    @Index(name = "idx_email", columnList = "email")
})
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Número de documento é obrigatório")
    @Column(name = "numero_documento", nullable = false, unique = true, length = 14)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 20)
    private TipoPessoa tipoPessoa;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

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
    public Pessoa() {
    }

    public Pessoa(String numeroDocumento, TipoPessoa tipoPessoa, String name, 
                  String email, String phone, Perfil perfil) {
        validateNumeroDocumento(numeroDocumento, tipoPessoa);
        validateName(name);
        validateEmail(email);
        validatePerfil(perfil);
        
        this.numeroDocumento = numeroDocumento;
        this.tipoPessoa = tipoPessoa;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.perfil = perfil;
    }

    // Validações
    private void validateNumeroDocumento(String documento, TipoPessoa tipo) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Número de documento não pode ser vazio");
        }
        
        if (tipo == TipoPessoa.PESSOA_FISICA) {
            // Validar CPF (11 dígitos)
            if (!documento.matches("\\d{11}")) {
                throw new IllegalArgumentException("CPF deve conter 11 dígitos");
            }
        } else {
            // Validar CNPJ (14 dígitos)
            if (!documento.matches("\\d{14}")) {
                throw new IllegalArgumentException("CNPJ deve conter 14 dígitos");
            }
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 3 caracteres");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    private void validatePerfil(Perfil perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil não pode ser nulo");
        }
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(id, pessoa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", tipoPessoa=" + tipoPessoa +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", perfil=" + perfil +
                ", createdAt=" + createdAt +
                '}';
    }
}
