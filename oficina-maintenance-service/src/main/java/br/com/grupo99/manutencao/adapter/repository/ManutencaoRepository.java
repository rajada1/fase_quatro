package br.com.grupo99.manutencao.adapter.repository;

import br.com.grupo99.manutencao.domain.Manutencao;
import br.com.grupo99.manutencao.domain.StatusManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, UUID> {
    List<Manutencao> findByVeiculoId(UUID veiculoId);

    List<Manutencao> findByStatus(StatusManutencao status);
}
