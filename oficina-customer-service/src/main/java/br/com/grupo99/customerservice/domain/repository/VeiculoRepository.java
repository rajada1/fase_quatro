package br.com.grupo99.customerservice.domain.repository;

import br.com.grupo99.customerservice.domain.model.Veiculo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ✅ CLEAN ARCHITECTURE: Interface pura de domínio
 * Sem Spring Data, sem framework annotations
 * Implementação fica na camada adapter/infrastructure
 */
public interface VeiculoRepository {

    /**
     * Salva um veículo.
     *
     * @param veiculo veículo a ser salvo
     * @return veículo salvo
     */
    Veiculo save(Veiculo veiculo);

    /**
     * Busca veículo por ID.
     *
     * @param id ID do veículo
     * @return Optional com veículo se existir
     */
    Optional<Veiculo> findById(UUID id);

    /**
     * Busca veículo pela placa.
     *
     * @param placa placa do veículo
     * @return Optional com veículo se existir
     */
    Optional<Veiculo> findByPlaca(String placa);

    /**
     * Busca todos os veículos de um cliente.
     *
     * @param clientePessoaId ID da pessoa (cliente)
     * @return Lista de veículos do cliente
     */
    List<Veiculo> findByClientePessoaId(UUID clientePessoaId);

    /**
     * Verifica se existe veículo com essa placa.
     *
     * @param placa placa do veículo
     * @return true se existe, false caso contrário
     */
    boolean existsByPlaca(String placa);

    /**
     * Deleta um veículo.
     *
     * @param id ID do veículo
     */
    void deleteById(UUID id);

    /**
     * Busca todos os veículos.
     *
     * @return Lista de veículos
     */
    List<Veiculo> findAll();
}
