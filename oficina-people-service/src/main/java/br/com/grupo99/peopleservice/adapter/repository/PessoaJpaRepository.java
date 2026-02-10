package br.com.grupo99.peopleservice.adapter.repository;

import br.com.grupo99.peopleservice.domain.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository para Pessoa.
 * Implementa a interface de domínio PessoaRepository.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Interface de domínio: domain.repository.PessoaRepository (pura, sem Spring Data)
 * - Implementação em adapter: JpaRepository (detalhes técnicos)
 */
@Repository
public interface PessoaJpaRepository extends JpaRepository<Pessoa, UUID> {

    Optional<Pessoa> findByNumeroDocumento(String numeroDocumento);

    Optional<Pessoa> findByEmail(String email);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByEmail(String email);
}
