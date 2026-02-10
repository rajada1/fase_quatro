package br.com.grupo99.catalogo.adapter.repository;

import br.com.grupo99.catalogo.domain.model.Peca;
import br.com.grupo99.catalogo.domain.repository.PecaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa PecaRepository (domínio) usando Spring Data MongoDB.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Implementa interface de domínio: PecaRepository
 * - Delega para MongoRepository: PecaMongoRepository
 * - Isolamento de framework em adapter layer
 * 
 * Migrado de JPA/PostgreSQL para MongoDB/DocumentDB
 */
@Repository
public class PecaRepositoryAdapter implements PecaRepository {

    private final PecaMongoRepository mongoRepository;

    public PecaRepositoryAdapter(PecaMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Peca save(Peca peca) {
        return mongoRepository.save(peca);
    }

    @Override
    public Optional<Peca> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public List<Peca> findByAtivoTrue() {
        return mongoRepository.findByAtivoTrue();
    }

    @Override
    public List<Peca> findByAtivoTrueOrderByNomeAsc() {
        return mongoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    @Override
    public Optional<Peca> findByCodigoFabricante(String codigoFabricante) {
        return mongoRepository.findByCodigoFabricante(codigoFabricante);
    }

    @Override
    public List<Peca> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoRepository.existsById(id);
    }

    @Override
    public List<Peca> findByCategoria(String categoria) {
        return mongoRepository.findByCategoriasContainingAndAtivoTrue(categoria);
    }

    @Override
    public List<Peca> findByMarca(String marca) {
        return mongoRepository.findByMarcaAndAtivoTrue(marca);
    }
}
