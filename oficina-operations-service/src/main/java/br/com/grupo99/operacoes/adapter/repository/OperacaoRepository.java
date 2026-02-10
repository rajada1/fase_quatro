package br.com.grupo99.operacoes.adapter.repository;

import br.com.grupo99.operacoes.domain.Operacao;
import br.com.grupo99.operacoes.domain.StatusOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperacaoRepository extends JpaRepository<Operacao, UUID> {
    Optional<Operacao> findByNumeroOperacao(String numeroOperacao);
    List<Operacao> findByStatus(StatusOperacao status);
    List<Operacao> findByOrcamentoId(UUID orcamentoId);
    List<Operacao> findByResponsavelId(UUID responsavelId);
}
