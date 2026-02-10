package br.com.grupo99.executionservice.domain.repository;

import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Pure Java interface para persistência do agregado ExecucaoOS.
 * 
 * Esta interface define o contrato de persistência sem dependências
 * de Spring Data ou qualquer framework específico.
 * A implementação deve ser fornecida pela camada de infraestrutura.
 */
public interface ExecucaoOSRepository {

    /**
     * Salva uma execução de OS.
     *
     * @param execucaoOS execução a ser salva
     * @return execução salva
     */
    ExecucaoOS save(ExecucaoOS execucaoOS);

    /**
     * Encontra uma execução por ID.
     *
     * @param id ID da execução
     * @return Optional contendo a execução, se encontrada
     */
    Optional<ExecucaoOS> findById(UUID id);

    /**
     * Busca execução por ID da OS.
     *
     * @param osId ID da ordem de serviço
     * @return Optional contendo a execução, se encontrada
     */
    Optional<ExecucaoOS> findByOsId(UUID osId);

    /**
     * Busca execuções por status.
     *
     * @param status status da execução
     * @return lista de execuções
     */
    List<ExecucaoOS> findByStatus(StatusExecucao status);

    /**
     * Busca execuções por mecânico.
     *
     * @param mecanico nome do mecânico
     * @return lista de execuções
     */
    List<ExecucaoOS> findByMecanico(String mecanico);

    /**
     * Busca todas as execuções.
     *
     * @return lista de todas as execuções
     */
    List<ExecucaoOS> findAll();

    /**
     * Deleta uma execução por ID.
     *
     * @param id ID da execução
     */
    void deleteById(UUID id);

    /**
     * Verifica se existe execução para uma OS.
     *
     * @param osId ID da ordem de serviço
     * @return true se existe
     */
    boolean existsByOsId(UUID osId);

    /**
     * Verifica se existe execução com um ID específico.
     *
     * @param id ID da execução
     * @return true se existe
     */
    boolean existsById(UUID id);
}
