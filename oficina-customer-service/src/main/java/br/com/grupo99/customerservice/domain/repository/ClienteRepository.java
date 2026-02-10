package br.com.grupo99.customerservice.domain.repository;

import br.com.grupo99.customerservice.domain.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ✅ CLEAN ARCHITECTURE: Interface pura de domínio
 * Sem Spring Data, sem framework annotations
 * Implementação fica na camada adapter/infrastructure
 */
public interface ClienteRepository {

    /**
     * Salva um cliente.
     *
     * @param cliente cliente a ser salvo
     * @return cliente salvo
     */
    Cliente save(Cliente cliente);

    /**
     * Busca cliente por pessoaId.
     *
     * @param pessoaId ID da pessoa
     * @return Optional com cliente se existir
     */
    Optional<Cliente> findByPessoaId(UUID pessoaId);

    /**
     * Verifica se existe cliente com pessoaId.
     *
     * @param pessoaId ID da pessoa
     * @return true se existe, false caso contrário
     */
    boolean existsByPessoaId(UUID pessoaId);

    /**
     * Deleta cliente por pessoaId.
     *
     * @param pessoaId ID da pessoa
     */
    void deleteByPessoaId(UUID pessoaId);

    /**
     * Busca cliente por ID.
     *
     * @param pessoaId ID da pessoa
     * @return Optional com cliente se existir
     */
    Optional<Cliente> findById(UUID pessoaId);

    /**
     * Lista todos os clientes.
     *
     * @return List de clientes
     */
    List<Cliente> findAll();

    /**
     * Deleta cliente por ID.
     *
     * @param pessoaId ID da pessoa
     */
    void deleteById(UUID pessoaId);
}
