package br.com.grupo99.notificacao.adapter.repository;

import br.com.grupo99.notificacao.domain.Notificacao;
import br.com.grupo99.notificacao.domain.StatusNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    List<Notificacao> findByStatus(StatusNotificacao status);

    List<Notificacao> findByDestinatarioEmail(String email);
}
