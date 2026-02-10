package br.com.grupo99.catalogo.domain.repository;

import br.com.grupo99.catalogo.domain.model.Peca;

import java.util.List;
import java.util.Optional;

/**
 * ✅ CLEAN ARCHITECTURE: Interface pura de domínio
 * Sem Spring Data, sem framework annotations
 * Implementação fica na camada adapter/infrastructure
 * 
 * Migrado para MongoDB/DocumentDB - ID agora é String
 */
public interface PecaRepository {

    /**
     * Salva uma peça.
     *
     * @param peca peça a ser salva
     * @return peça salva
     */
    Peca save(Peca peca);

    /**
     * Busca peça por ID.
     *
     * @param id ID da peça (String para MongoDB)
     * @return Optional com peça se existir
     */
    Optional<Peca> findById(String id);

    /**
     * Busca todas as peças ativas.
     *
     * @return List de peças ativas
     */
    List<Peca> findByAtivoTrue();

    /**
     * Busca todas as peças ativas ordenadas por nome.
     *
     * @return List de peças ativas ordenadas por nome
     */
    List<Peca> findByAtivoTrueOrderByNomeAsc();

    /**
     * Busca peça por código do fabricante.
     *
     * @param codigoFabricante código do fabricante
     * @return Optional com peça se existir
     */
    Optional<Peca> findByCodigoFabricante(String codigoFabricante);

    /**
     * Lista todas as peças.
     *
     * @return List de peças
     */
    List<Peca> findAll();

    /**
     * Deleta peça por ID.
     *
     * @param id ID da peça (String para MongoDB)
     */
    void deleteById(String id);

    /**
     * Verifica se existe peça com ID.
     *
     * @param id ID da peça (String para MongoDB)
     * @return true se existe, false caso contrário
     */
    boolean existsById(String id);

    /**
     * Busca peças por categoria.
     *
     * @param categoria categoria da peça
     * @return List de peças na categoria
     */
    List<Peca> findByCategoria(String categoria);

    /**
     * Busca peças por marca.
     *
     * @param marca marca da peça
     * @return List de peças da marca
     */
    List<Peca> findByMarca(String marca);
}
