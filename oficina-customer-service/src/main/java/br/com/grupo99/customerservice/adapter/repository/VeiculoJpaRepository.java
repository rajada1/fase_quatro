package br.com.grupo99.customerservice.adapter.repository;

import br.com.grupo99.customerservice.domain.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository para Veiculo.
 * Implementa a interface de domínio VeiculoRepository.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Interface de domínio: domain.repository.VeiculoRepository (pura, sem Spring
 * Data)
 * - Implementação em adapter: JpaRepository (detalhes técnicos)
 */
@Repository
public interface VeiculoJpaRepository extends JpaRepository<Veiculo, UUID> {

    Optional<Veiculo> findByPlaca(String placa);

    List<Veiculo> findByClientePessoaId(UUID clientePessoaId);

    boolean existsByPlaca(String placa);
}
