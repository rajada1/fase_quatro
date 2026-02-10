package br.com.grupo99.customerservice.application.service;

import br.com.grupo99.customerservice.application.dto.ClienteRequestDTO;
import br.com.grupo99.customerservice.application.dto.ClienteResponseDTO;
import br.com.grupo99.customerservice.application.exception.BusinessException;
import br.com.grupo99.customerservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.customerservice.domain.model.Cliente;
import br.com.grupo99.customerservice.domain.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de aplicação responsável pela orquestração de casos de uso
 * relacionados a Cliente.
 * Cliente usa pessoaId como ID principal (vem do People Service).
 * Implementa a lógica de negócio e valida as regras de domínio.
 */
@Service
@Transactional
public class CustomerApplicationService {

    private final ClienteRepository clienteRepository;
    private final EventPublishingService eventPublishingService;

    public CustomerApplicationService(ClienteRepository clienteRepository,
            EventPublishingService eventPublishingService) {
        this.clienteRepository = clienteRepository;
        this.eventPublishingService = eventPublishingService;
    }

    /**
     * Cria um novo cliente a partir de uma Pessoa criada no People Service.
     */
    public ClienteResponseDTO criarCliente(ClienteRequestDTO requestDTO) {
        validarCamposObrigatorios(requestDTO);
        verificarDuplicidadeCliente(requestDTO.pessoaId());

        // Criar Cliente com pessoaId vindo do People Service
        Cliente cliente = new Cliente(requestDTO.pessoaId());
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Publicar evento
        eventPublishingService.publicarClienteCriado(clienteSalvo);

        return ClienteResponseDTO.fromDomain(clienteSalvo);
    }

    /**
     * Busca um cliente pelo pessoaId.
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(UUID pessoaId) {
        Cliente cliente = clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));
        return ClienteResponseDTO.fromDomain(cliente);
    }

    /**
     * Lista todos os clientes.
     */
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDTO::fromDomain)
                .toList();
    }

    /**
     * Atualiza um cliente (no momento apenas marca como atualizado).
     * Dados pessoais são atualizados via People Service.
     */
    public ClienteResponseDTO atualizarCliente(UUID pessoaId, ClienteRequestDTO requestDTO) {
        Cliente cliente = clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));

        // Cliente apenas recebe pessoaId, dados pessoais vêm do People Service
        eventPublishingService.publicarClienteAtualizado(cliente);

        return ClienteResponseDTO.fromDomain(cliente);
    }

    /**
     * Deleta um cliente e seus veículos relacionados.
     */
    public void deletarCliente(UUID pessoaId) {
        Cliente cliente = clienteRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com pessoaId: " + pessoaId));

        clienteRepository.deleteById(pessoaId);
        eventPublishingService.publicarClienteDeletado(pessoaId);
    }

    // ===== Métodos de Validação =====

    private void validarCamposObrigatorios(ClienteRequestDTO requestDTO) {
        if (requestDTO.pessoaId() == null) {
            throw new BusinessException("Pessoa ID é obrigatório");
        }
    }

    private void verificarDuplicidadeCliente(UUID pessoaId) {
        if (clienteRepository.existsByPessoaId(pessoaId)) {
            throw new BusinessException("Já existe um cliente para esta pessoa");
        }
    }
}
