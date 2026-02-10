package br.com.grupo99.hrservice.adapter.repository;

import br.com.grupo99.hrservice.domain.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository para Funcionario.
 * Implementa a interface de domínio FuncionarioRepository.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Interface de domínio: domain.repository.FuncionarioRepository (pura, sem
 * Spring Data)
 * - Implementação em adapter: JpaRepository (detalhes técnicos)
 */
@Repository
public interface FuncionarioJpaRepository extends JpaRepository<Funcionario, UUID> {

    boolean existsByPessoaId(UUID pessoaId);

    Funcionario findByPessoaId(UUID pessoaId);
}
