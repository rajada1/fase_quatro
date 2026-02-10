package br.com.grupo99.customerservice.adapter.repository;

import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.repository.ClienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository para Cliente.
 * Implementa a interface de domínio ClienteRepository.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Interface de domínio: domain.repository.ClienteRepository (pura, sem Spring
 * Data)
 * - Implementação em adapter: JpaRepository (detalhes técnicos)
 */
@Repository
public interface ClienteJpaRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByPessoaId(UUID pessoaId);

    boolean existsByPessoaId(UUID pessoaId);

    void deleteByPessoaId(UUID pessoaId);
}
