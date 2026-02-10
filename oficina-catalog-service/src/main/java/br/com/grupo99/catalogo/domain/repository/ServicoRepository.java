package br.com.grupo99.catalogo.domain.repository;

import br.com.grupo99.catalogo.domain.model.Servico;

import java.util.List;
import java.util.Optional;

/**
 * ✅ CLEAN ARCHITECTURE: Interface pura de domínio
 * Sem Spring Data, sem framework annotations
 * Implementação fica na camada adapter/infrastructure
 * 
 * Migrado para MongoDB/DocumentDB - ID agora é String
 */
public interface ServicoRepository {

    /**
     * Salva um serviço.
     *
     * @param servico serviço a ser salvo
     * @return serviço salvo
     */
    Servico save(Servico servico);

    /**
     * Busca serviço por ID.
     *
     * @param id ID do serviço (String para MongoDB)
     * @return Optional com serviço se existir
     */
    Optional<Servico> findById(String id);

    /**
     * Busca todos os serviços ativos.
     *
     * @return List de serviços ativos
     */
    List<Servico> findByAtivoTrue();

    /**
     * Busca todos os serviços ativos ordenados por nome.
     *
     * @return List de serviços ativos ordenados por nome
     */
    List<Servico> findByAtivoTrueOrderByNomeAsc();

    /**
     * Lista todos os serviços.
     *
     * @return List de serviços
     */
    List<Servico> findAll();

    /**
     * Deleta serviço por ID.
     *
     * @param id ID do serviço (String para MongoDB)
     */
    void deleteById(String id);

    /**
     * Verifica se existe serviço com ID.
     *
     * @param id ID do serviço (String para MongoDB)
     * @return true se existe, false caso contrário
     */
    boolean existsById(String id);

    /**
     * Busca serviços por categoria.
     *
     * @param categoria categoria do serviço
     * @return List de serviços na categoria
     */
    List<Servico> findByCategoria(String categoria);
}
