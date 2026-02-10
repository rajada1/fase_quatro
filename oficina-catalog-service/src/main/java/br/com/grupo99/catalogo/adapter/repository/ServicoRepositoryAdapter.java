package br.com.grupo99.catalogo.adapter.repository;

import br.com.grupo99.catalogo.domain.model.Servico;
import br.com.grupo99.catalogo.domain.repository.ServicoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa ServicoRepository (domínio) usando Spring Data
 * MongoDB.
 * 
 * ✅ CLEAN ARCHITECTURE:
 * - Implementa interface de domínio: ServicoRepository
 * - Delega para MongoRepository: ServicoMongoRepository
 * - Isolamento de framework em adapter layer
 * 
 * Migrado de JPA/PostgreSQL para MongoDB/DocumentDB
 */
@Repository
public class ServicoRepositoryAdapter implements ServicoRepository {

    private final ServicoMongoRepository mongoRepository;

    public ServicoRepositoryAdapter(ServicoMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Servico save(Servico servico) {
        return mongoRepository.save(servico);
    }

    @Override
    public Optional<Servico> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public List<Servico> findByAtivoTrue() {
        return mongoRepository.findByAtivoTrue();
    }

    @Override
    public List<Servico> findByAtivoTrueOrderByNomeAsc() {
        return mongoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    @Override
    public List<Servico> findAll() {
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
    public List<Servico> findByCategoria(String categoria) {
        return mongoRepository.findByCategoriasContainingAndAtivoTrue(categoria);
    }
}
