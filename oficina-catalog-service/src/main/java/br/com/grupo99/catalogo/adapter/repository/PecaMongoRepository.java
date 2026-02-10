package br.com.grupo99.catalogo.adapter.repository;

import br.com.grupo99.catalogo.domain.model.Peca;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB Repository para Peca.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Interface de domínio: domain.repository.PecaRepository (pura, sem Spring Data)
 * - Implementação em adapter: MongoRepository (detalhes técnicos)
 * 
 * Migrado de JPA/PostgreSQL para MongoDB/DocumentDB
 */
@Repository
public interface PecaMongoRepository extends MongoRepository<Peca, String> {
    
    List<Peca> findByAtivoTrue();

    @Query(value = "{ 'ativo': true }", sort = "{ 'nome': 1 }")
    List<Peca> findByAtivoTrueOrderByNomeAsc();

    Optional<Peca> findByCodigoFabricante(String codigoFabricante);

    @Query("{ 'categorias': ?0, 'ativo': true }")
    List<Peca> findByCategoriasContainingAndAtivoTrue(String categoria);

    List<Peca> findByMarcaAndAtivoTrue(String marca);

    @Query("{ 'quantidade': { $lte: ?0 }, 'ativo': true }")
    List<Peca> findByQuantidadeLessThanEqualAndAtivoTrue(Integer quantidade);
}
