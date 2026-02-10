package br.com.grupo99.customerservice.adapter.repository;

import br.com.grupo99.customerservice.domain.model.Veiculo;
import br.com.grupo99.customerservice.domain.repository.VeiculoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter que implementa VeiculoRepository (domínio) usando Spring Data JPA.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Implementa interface de domínio: VeiculoRepository
 * - Delega para JpaRepository: VeiculoJpaRepository
 * - Isolamento de framework em adapter layer
 */
@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepository {

    private final VeiculoJpaRepository jpaRepository;

    public VeiculoRepositoryAdapter(VeiculoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Veiculo save(Veiculo veiculo) {
        return jpaRepository.save(veiculo);
    }

    @Override
    public Optional<Veiculo> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Veiculo> findByPlaca(String placa) {
        return jpaRepository.findByPlaca(placa);
    }

    @Override
    public List<Veiculo> findByClientePessoaId(UUID clientePessoaId) {
        return jpaRepository.findByClientePessoaId(clientePessoaId);
    }

    @Override
    public boolean existsByPlaca(String placa) {
        return jpaRepository.existsByPlaca(placa);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Veiculo> findAll() {
        return jpaRepository.findAll();
    }
}
