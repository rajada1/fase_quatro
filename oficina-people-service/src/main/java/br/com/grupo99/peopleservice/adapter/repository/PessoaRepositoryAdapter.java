package br.com.grupo99.peopleservice.adapter.repository;

import br.com.grupo99.peopleservice.domain.model.Pessoa;
import br.com.grupo99.peopleservice.domain.repository.PessoaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter que implementa PessoaRepository (domínio) usando Spring Data JPA.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Implementa interface de domínio: PessoaRepository
 * - Delega para JpaRepository: PessoaJpaRepository
 * - Isolamento de framework em adapter layer
 */
@Repository
public class PessoaRepositoryAdapter implements PessoaRepository {

    private final PessoaJpaRepository jpaRepository;

    public PessoaRepositoryAdapter(PessoaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        return jpaRepository.save(pessoa);
    }

    @Override
    public Optional<Pessoa> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Pessoa> findByNumeroDocumento(String numeroDocumento) {
        return jpaRepository.findByNumeroDocumento(numeroDocumento);
    }

    @Override
    public Optional<Pessoa> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return jpaRepository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<Pessoa> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Pessoa pessoa) {
        jpaRepository.delete(pessoa);
    }
}
