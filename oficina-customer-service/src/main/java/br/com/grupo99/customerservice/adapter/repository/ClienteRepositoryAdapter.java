package br.com.grupo99.customerservice.adapter.repository;

import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.repository.ClienteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter que implementa ClienteRepository (domínio) usando Spring Data JPA.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Implementa interface de domínio: ClienteRepository
 * - Delega para JpaRepository: ClienteJpaRepository
 * - Isolamento de framework em adapter layer
 */
@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        return jpaRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> findByPessoaId(UUID pessoaId) {
        return jpaRepository.findByPessoaId(pessoaId);
    }

    @Override
    public boolean existsByPessoaId(UUID pessoaId) {
        return jpaRepository.existsByPessoaId(pessoaId);
    }

    @Override
    public void deleteByPessoaId(UUID pessoaId) {
        jpaRepository.deleteByPessoaId(pessoaId);
    }

    @Override
    public Optional<Cliente> findById(UUID pessoaId) {
        return jpaRepository.findById(pessoaId);
    }

    @Override
    public List<Cliente> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID pessoaId) {
        jpaRepository.deleteById(pessoaId);
    }
}
