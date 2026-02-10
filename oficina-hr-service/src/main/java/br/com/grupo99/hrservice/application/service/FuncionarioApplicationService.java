package br.com.grupo99.hrservice.application.service;

import br.com.grupo99.hrservice.application.dto.FuncionarioRequestDTO;
import br.com.grupo99.hrservice.application.dto.FuncionarioResponseDTO;
import br.com.grupo99.hrservice.application.exception.BusinessException;
import br.com.grupo99.hrservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.hrservice.domain.model.Funcionario;
import br.com.grupo99.hrservice.domain.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de aplicação responsável pela orquestração de casos de uso
 * relacionados a Funcionario.
 * Funcionario usa pessoaId como ID principal (vem do People Service).
 * Implementa a lógica de negócio e valida as regras de domínio.
 */
@Service
@Transactional
public class FuncionarioApplicationService {

    private final FuncionarioRepository funcionarioRepository;
    private final EventPublishingService eventPublishingService;

    public FuncionarioApplicationService(FuncionarioRepository funcionarioRepository,
            EventPublishingService eventPublishingService) {
        this.funcionarioRepository = funcionarioRepository;
        this.eventPublishingService = eventPublishingService;
    }

    /**
     * Cria um novo funcionário a partir de uma Pessoa criada no People Service.
     */
    public FuncionarioResponseDTO criarFuncionario(FuncionarioRequestDTO requestDTO) {
        validarCamposObrigatorios(requestDTO);
        verificarDuplicidadeFuncionario(requestDTO.pessoaId());

        // Criar Funcionario com pessoaId vindo do People Service
        Funcionario funcionario = new Funcionario(
                requestDTO.pessoaId(),
                requestDTO.dataAdmissao(),
                requestDTO.setor(),
                requestDTO.cargo(),
                requestDTO.salario());

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        // Publicar evento
        eventPublishingService.publicarFuncionarioCriado(funcionarioSalvo);

        return FuncionarioResponseDTO.fromDomain(funcionarioSalvo);
    }

    /**
     * Busca um funcionário pelo pessoaId.
     */
    @Transactional(readOnly = true)
    public FuncionarioResponseDTO buscarPorId(UUID pessoaId) {
        Funcionario funcionario = funcionarioRepository.findById(pessoaId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Funcionário não encontrado com pessoaId: " + pessoaId));
        return FuncionarioResponseDTO.fromDomain(funcionario);
    }

    /**
     * Lista todos os funcionários.
     */
    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository.findAll().stream()
                .map(FuncionarioResponseDTO::fromDomain)
                .toList();
    }

    /**
     * Atualiza um funcionário.
     */
    public FuncionarioResponseDTO atualizarFuncionario(UUID pessoaId, FuncionarioRequestDTO requestDTO) {
        validarCamposObrigatorios(requestDTO);

        Funcionario funcionario = funcionarioRepository.findById(pessoaId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Funcionário não encontrado com pessoaId: " + pessoaId));

        // Atualizar campos
        funcionario.setDataAdmissao(requestDTO.dataAdmissao());
        funcionario.setSetor(requestDTO.setor());
        funcionario.setCargo(requestDTO.cargo());
        funcionario.setSalario(requestDTO.salario());

        Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);

        // Publicar evento
        eventPublishingService.publicarFuncionarioAtualizado(funcionarioAtualizado);

        return FuncionarioResponseDTO.fromDomain(funcionarioAtualizado);
    }

    /**
     * Deleta um funcionário.
     */
    public void deletarFuncionario(UUID pessoaId) {
        Funcionario funcionario = funcionarioRepository.findById(pessoaId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Funcionário não encontrado com pessoaId: " + pessoaId));

        funcionarioRepository.deleteById(pessoaId);

        // Publicar evento
        eventPublishingService.publicarFuncionarioDeletado(pessoaId);
    }

    /**
     * Desativa um funcionário.
     */
    public FuncionarioResponseDTO desativarFuncionario(UUID pessoaId) {
        Funcionario funcionario = funcionarioRepository.findById(pessoaId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Funcionário não encontrado com pessoaId: " + pessoaId));

        funcionario.setAtivo(false);

        Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);

        // Publicar evento
        eventPublishingService.publicarFuncionarioAtualizado(funcionarioAtualizado);

        return FuncionarioResponseDTO.fromDomain(funcionarioAtualizado);
    }

    // ===== Métodos de Validação =====

    private void validarCamposObrigatorios(FuncionarioRequestDTO requestDTO) {
        if (requestDTO.pessoaId() == null) {
            throw new BusinessException("Pessoa ID é obrigatório");
        }
        if (requestDTO.dataAdmissao() == null) {
            throw new BusinessException("Data de admissão é obrigatória");
        }
        if (requestDTO.setor() == null || requestDTO.setor().trim().isEmpty()) {
            throw new BusinessException("Setor é obrigatório");
        }
        if (requestDTO.cargo() == null || requestDTO.cargo().trim().isEmpty()) {
            throw new BusinessException("Cargo é obrigatório");
        }
        if (requestDTO.salario() == null) {
            throw new BusinessException("Salário é obrigatório");
        }
    }

    private void verificarDuplicidadeFuncionario(UUID pessoaId) {
        if (funcionarioRepository.existsByPessoaId(pessoaId)) {
            throw new BusinessException("Já existe um funcionário para esta pessoa");
        }
    }
}
