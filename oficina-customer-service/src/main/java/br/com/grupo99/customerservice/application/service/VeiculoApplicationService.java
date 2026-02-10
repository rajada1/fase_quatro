package br.com.grupo99.customerservice.application.service;

import br.com.grupo99.customerservice.application.dto.VeiculoRequestDTO;
import br.com.grupo99.customerservice.application.dto.VeiculoResponseDTO;
import br.com.grupo99.customerservice.application.exception.BusinessException;
import br.com.grupo99.customerservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.model.Veiculo;
import br.com.grupo99.customerservice.domain.repository.ClienteRepository;
import br.com.grupo99.customerservice.domain.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de aplicação responsável pela orquestração de casos de uso
 * relacionados a Veículo.
 * Implementa a lógica de negócio e valida as regras de domínio.
 */
@Service
@Transactional
public class VeiculoApplicationService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final EventPublishingService eventPublishingService;

    public VeiculoApplicationService(VeiculoRepository veiculoRepository,
            ClienteRepository clienteRepository,
            EventPublishingService eventPublishingService) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
        this.eventPublishingService = eventPublishingService;
    }

    /**
     * Cria um novo veículo para um cliente.
     */
    public VeiculoResponseDTO criarVeiculo(UUID pessoaId, VeiculoRequestDTO requestDTO) {
        validarCamposObrigatorios(requestDTO);

        // Verificar se cliente existe
        Cliente cliente = clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));

        // Verificar duplicidade de placa
        verificarDuplicidadePlaca(requestDTO.placa());

        // Criar Veículo
        Veiculo veiculo = new Veiculo(
                requestDTO.placa(),
                requestDTO.marca(),
                requestDTO.modelo(),
                requestDTO.ano());

        // Definir campos opcionais
        if (requestDTO.renavam() != null && !requestDTO.renavam().isEmpty()) {
            veiculo.setRenavam(requestDTO.renavam());
        }
        if (requestDTO.cor() != null && !requestDTO.cor().isEmpty()) {
            veiculo.setCor(requestDTO.cor());
        }
        if (requestDTO.chassi() != null && !requestDTO.chassi().isEmpty()) {
            veiculo.setChassi(requestDTO.chassi());
        }

        // Relacionar com cliente
        cliente.adicionarVeiculo(veiculo);

        // Salvar
        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);

        // Publicar evento
        eventPublishingService.publicarVeiculoCriado(veiculoSalvo);

        return VeiculoResponseDTO.fromDomain(veiculoSalvo);
    }

    /**
     * Busca um veículo pelo ID.
     */
    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscarPorId(UUID veiculoId) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com ID: " + veiculoId));
        return VeiculoResponseDTO.fromDomain(veiculo);
    }

    /**
     * Lista todos os veículos de um cliente.
     */
    @Transactional(readOnly = true)
    public List<VeiculoResponseDTO> listarVeiculosDoCliente(UUID pessoaId) {
        // Verificar se cliente existe
        clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));

        return veiculoRepository.findByClientePessoaId(pessoaId).stream()
                .map(VeiculoResponseDTO::fromDomain)
                .toList();
    }

    /**
     * Atualiza um veículo.
     */
    public VeiculoResponseDTO atualizarVeiculo(UUID pessoaId, UUID veiculoId, VeiculoRequestDTO requestDTO) {
        validarCamposObrigatorios(requestDTO);

        // Verificar se cliente existe
        clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));

        // Buscar veículo
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com ID: " + veiculoId));

        // Verificar se placa já existe em outro veículo
        if (!veiculo.getPlaca().equals(requestDTO.placa()) && veiculoRepository.existsByPlaca(requestDTO.placa())) {
            throw new BusinessException("Já existe um veículo com essa placa");
        }

        // Atualizar campos
        veiculo.setPlaca(requestDTO.placa());
        veiculo.setMarca(requestDTO.marca());
        veiculo.setModelo(requestDTO.modelo());
        veiculo.setAno(requestDTO.ano());

        if (requestDTO.renavam() != null && !requestDTO.renavam().isEmpty()) {
            veiculo.setRenavam(requestDTO.renavam());
        }
        if (requestDTO.cor() != null && !requestDTO.cor().isEmpty()) {
            veiculo.setCor(requestDTO.cor());
        }
        if (requestDTO.chassi() != null && !requestDTO.chassi().isEmpty()) {
            veiculo.setChassi(requestDTO.chassi());
        }

        Veiculo veiculoAtualizado = veiculoRepository.save(veiculo);

        // Publicar evento
        eventPublishingService.publicarVeiculoAtualizado(veiculoAtualizado);

        return VeiculoResponseDTO.fromDomain(veiculoAtualizado);
    }

    /**
     * Deleta um veículo.
     */
    public void deletarVeiculo(UUID pessoaId, UUID veiculoId) {
        // Verificar se cliente existe
        Cliente cliente = clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));

        // Buscar veículo
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com ID: " + veiculoId));

        // Remover do cliente
        cliente.removerVeiculo(veiculo);

        // Deletar
        veiculoRepository.deleteById(veiculoId);

        // Publicar evento
        eventPublishingService.publicarVeiculoDeletado(veiculoId);
    }

    // ===== Métodos de Validação =====

    private void validarCamposObrigatorios(VeiculoRequestDTO requestDTO) {
        if (requestDTO.placa() == null || requestDTO.placa().trim().isEmpty()) {
            throw new BusinessException("Placa é obrigatória");
        }
        if (requestDTO.marca() == null || requestDTO.marca().trim().isEmpty()) {
            throw new BusinessException("Marca é obrigatória");
        }
        if (requestDTO.modelo() == null || requestDTO.modelo().trim().isEmpty()) {
            throw new BusinessException("Modelo é obrigatório");
        }
        if (requestDTO.ano() == null || requestDTO.ano() < 1900 || requestDTO.ano() > 2100) {
            throw new BusinessException("Ano deve estar entre 1900 e 2100");
        }
    }

    private void verificarDuplicidadePlaca(String placa) {
        if (veiculoRepository.existsByPlaca(placa)) {
            throw new BusinessException("Já existe um veículo com essa placa");
        }
    }
}
