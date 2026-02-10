package br.com.grupo99.hrservice.adapter.repository;

import br.com.grupo99.hrservice.domain.model.Funcionario;
import br.com.grupo99.hrservice.domain.repository.FuncionarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter que implementa FuncionarioRepository (domínio) usando Spring Data
 * JPA.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Implementa interface de domínio: FuncionarioRepository
 * - Delega para JpaRepository: FuncionarioJpaRepository
 * - Isolamento de framework em adapter layer
 */
@Repository
public class FuncionarioRepositoryAdapter implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;

    public FuncionarioRepositoryAdapter(FuncionarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Funcionario save(Funcionario funcionario) {
        return jpaRepository.save(funcionario);
    }

    @Override
    public Optional<Funcionario> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Funcionario> findByPessoaId(UUID pessoaId) {
        return Optional.ofNullable(jpaRepository.findByPessoaId(pessoaId));
    }

    @Override
    public boolean existsByPessoaId(UUID pessoaId) {
        return jpaRepository.existsByPessoaId(pessoaId);
    }

    @Override
    public List<Funcionario> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
