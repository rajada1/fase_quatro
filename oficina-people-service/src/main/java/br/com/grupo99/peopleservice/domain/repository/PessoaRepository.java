package br.com.grupo99.peopleservice.domain.repository;

import br.com.grupo99.peopleservice.domain.model.Pessoa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ✅ CLEAN ARCHITECTURE: Interface pura de domínio
 * Sem Spring Data, sem framework annotations
 * Implementação fica na camada adapter/infrastructure
 */
public interface PessoaRepository {

    /**
     * Salva uma pessoa.
     *
     * @param pessoa pessoa a ser salva
     * @return pessoa salva
     */
    Pessoa save(Pessoa pessoa);

    /**
     * Busca pessoa por ID.
     *
     * @param id ID da pessoa
     * @return Optional com pessoa se existir
     */
    Optional<Pessoa> findById(UUID id);

    /**
     * Busca pessoa por número de documento.
     *
     * @param numeroDocumento número do documento
     * @return Optional com pessoa se existir
     */
    Optional<Pessoa> findByNumeroDocumento(String numeroDocumento);

    /**
     * Busca pessoa por email.
     *
     * @param email email da pessoa
     * @return Optional com pessoa se existir
     */
    Optional<Pessoa> findByEmail(String email);

    /**
     * Verifica se existe pessoa com número de documento.
     *
     * @param numeroDocumento número do documento
     * @return true se existe, false caso contrário
     */
    boolean existsByNumeroDocumento(String numeroDocumento);

    /**
     * Verifica se existe pessoa com email.
     *
     * @param email email da pessoa
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Lista todas as pessoas.
     *
     * @return List de pessoas
     */
    List<Pessoa> findAll();

    /**
     * Deleta pessoa por ID.
     *
     * @param id ID da pessoa
     */
    void deleteById(UUID id);

    /**
     * Deleta uma pessoa.
     *
     * @param pessoa pessoa a ser deletada
     */
    void delete(Pessoa pessoa);
}
