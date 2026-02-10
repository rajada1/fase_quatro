package br.com.grupo99.catalogo.adapter.repository;

import br.com.grupo99.catalogo.domain.model.Servico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB Repository para Servico.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Interface de domínio: domain.repository.ServicoRepository (pura, sem Spring Data)
 * - Implementação em adapter: MongoRepository (detalhes técnicos)
 * 
 * Migrado de JPA/PostgreSQL para MongoDB/DocumentDB
 */
@Repository
public interface ServicoMongoRepository extends MongoRepository<Servico, String> {
    
    List<Servico> findByAtivoTrue();

    @Query(value = "{ 'ativo': true }", sort = "{ 'nome': 1 }")
    List<Servico> findByAtivoTrueOrderByNomeAsc();

    @Query("{ 'categorias': ?0, 'ativo': true }")
    List<Servico> findByCategoriasContainingAndAtivoTrue(String categoria);

    @Query("{ 'tempo_estimado_minutos': { $lte: ?0 }, 'ativo': true }")
    List<Servico> findByTempoEstimadoMinutosLessThanEqualAndAtivoTrue(Integer tempoMaximo);
}
