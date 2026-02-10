package br.com.grupo99.hrservice.domain.repository;

import br.com.grupo99.hrservice.domain.model.Funcionario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ✅ CLEAN ARCHITECTURE: Interface pura de domínio
 * Sem Spring Data, sem framework annotations
 * Implementação fica na camada adapter/infrastructure
 */
public interface FuncionarioRepository {

    /**
     * Salva um funcionário.
     *
     * @param funcionario funcionário a ser salvo
     * @return funcionário salvo
     */
    Funcionario save(Funcionario funcionario);

    /**
     * Busca funcionário por ID.
     *
     * @param id ID do funcionário
     * @return Optional com funcionário se existir
     */
    Optional<Funcionario> findById(UUID id);

    /**
     * Busca funcionário por pessoaId.
     *
     * @param pessoaId ID da pessoa
     * @return Optional com funcionário se existir
     */
    Optional<Funcionario> findByPessoaId(UUID pessoaId);

    /**
     * Verifica se existe funcionário para um determinado pessoaId.
     *
     * @param pessoaId ID da pessoa
     * @return true se existe, false caso contrário
     */
    boolean existsByPessoaId(UUID pessoaId);

    /**
     * Lista todos os funcionários.
     *
     * @return List de funcionários
     */
    List<Funcionario> findAll();

    /**
     * Deleta funcionário por ID.
     *
     * @param id ID do funcionário
     */
    void deleteById(UUID id);
}
