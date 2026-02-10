package br.com.grupo99.peopleservice.application.service;

import br.com.grupo99.peopleservice.application.dto.PessoaRequestDTO;
import br.com.grupo99.peopleservice.application.dto.PessoaResponseDTO;
import br.com.grupo99.peopleservice.application.exception.BusinessException;
import br.com.grupo99.peopleservice.application.exception.ResourceNotFoundException;
import br.com.grupo99.peopleservice.domain.model.Pessoa;
import br.com.grupo99.peopleservice.domain.repository.PessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para gerenciar Pessoas.
 * Implementa lógica de negócio e orquestração de transações.
 */
@Service
@Transactional
public class PessoaApplicationService {

    private final PessoaRepository pessoaRepository;
    private final EventPublishingService eventPublishingService;

    public PessoaApplicationService(PessoaRepository pessoaRepository,
            EventPublishingService eventPublishingService) {
        this.pessoaRepository = pessoaRepository;
        this.eventPublishingService = eventPublishingService;
    }

    /**
     * Cria uma nova Pessoa.
     */
    public PessoaResponseDTO criarPessoa(PessoaRequestDTO requestDTO) {
        validarCamposObrigatorios(requestDTO);
        verificarDuplicidadeDocumento(requestDTO.numeroDocumento());
        verificarDuplicidadeEmail(requestDTO.email());

        Pessoa pessoa = new Pessoa(
                requestDTO.numeroDocumento(),
                requestDTO.tipoPessoa(),
                requestDTO.name(),
                requestDTO.email(),
                requestDTO.phone(),
                requestDTO.perfil());

        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        eventPublishingService.publicarPessoaCriada(pessoaSalva);

        return PessoaResponseDTO.fromDomain(pessoaSalva);
    }

    /**
     * Busca Pessoa por ID.
     */
    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorId(UUID id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa com ID " + id + " não encontrada"));
        return PessoaResponseDTO.fromDomain(pessoa);
    }

    /**
     * Busca Pessoa por número de documento (CPF/CNPJ).
     * Usado pelo Lambda Auth Service para validação de autenticação.
     */
    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorDocumento(String documento) {
        Pessoa pessoa = pessoaRepository.findByNumeroDocumento(documento)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Pessoa com documento " + documento + " não encontrada"));
        return PessoaResponseDTO.fromDomain(pessoa);
    }

    /**
     * Lista todas as Pessoas.
     */
    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> listarTodas() {
        return pessoaRepository.findAll()
                .stream()
                .map(PessoaResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma Pessoa.
     */
    public PessoaResponseDTO atualizarPessoa(UUID id, PessoaRequestDTO requestDTO) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa com ID " + id + " não encontrada"));

        // Validar duplicidade apenas se o documento foi alterado
        if (!pessoa.getNumeroDocumento().equals(requestDTO.numeroDocumento())) {
            verificarDuplicidadeDocumento(requestDTO.numeroDocumento());
        }

        // Validar duplicidade apenas se o email foi alterado
        if (!pessoa.getEmail().equals(requestDTO.email())) {
            verificarDuplicidadeEmail(requestDTO.email());
        }

        pessoa.setNumeroDocumento(requestDTO.numeroDocumento());
        pessoa.setTipoPessoa(requestDTO.tipoPessoa());
        pessoa.setName(requestDTO.name());
        pessoa.setEmail(requestDTO.email());
        pessoa.setPhone(requestDTO.phone());
        pessoa.setPerfil(requestDTO.perfil());

        Pessoa pessoaAtualizada = pessoaRepository.save(pessoa);
        eventPublishingService.publicarPessoaAtualizada(pessoaAtualizada);

        return PessoaResponseDTO.fromDomain(pessoaAtualizada);
    }

    /**
     * Deleta uma Pessoa.
     */
    public void deletarPessoa(UUID id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa com ID " + id + " não encontrada"));

        pessoaRepository.delete(pessoa);
        eventPublishingService.publicarPessoaDeletada(pessoa);
    }

    // Métodos privados de validação
    private void validarCamposObrigatorios(PessoaRequestDTO requestDTO) {
        if (requestDTO.numeroDocumento() == null || requestDTO.numeroDocumento().isEmpty()) {
            throw new BusinessException("Número de documento é obrigatório");
        }
        if (requestDTO.name() == null || requestDTO.name().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }
        if (requestDTO.email() == null || requestDTO.email().isEmpty()) {
            throw new BusinessException("Email é obrigatório");
        }
        if (requestDTO.perfil() == null) {
            throw new BusinessException("Perfil é obrigatório");
        }
    }

    private void verificarDuplicidadeDocumento(String numeroDocumento) {
        if (pessoaRepository.existsByNumeroDocumento(numeroDocumento)) {
            throw new BusinessException("Já existe uma Pessoa com este número de documento");
        }
    }

    private void verificarDuplicidadeEmail(String email) {
        if (pessoaRepository.existsByEmail(email)) {
            throw new BusinessException("Já existe uma Pessoa com este email");
        }
    }
}
