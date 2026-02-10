package br.com.grupo99.catalogo.application.service;

import br.com.grupo99.catalogo.application.dto.ServicoRequestDTO;
import br.com.grupo99.catalogo.application.dto.ServicoResponseDTO;
import br.com.grupo99.catalogo.domain.model.Servico;
import br.com.grupo99.catalogo.domain.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service para Serviço.
 * Migrado para MongoDB/DocumentDB - ID agora é String.
 */
@Service
public class ServicoApplicationService {
    private final ServicoRepository servicoRepository;

    public ServicoApplicationService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public ServicoResponseDTO criarServico(ServicoRequestDTO request) {
        Servico servico = new Servico(
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getTempoEstimadoMinutos());
        servico = servicoRepository.save(servico);
        return ServicoResponseDTO.fromDomain(servico);
    }

    @Transactional(readOnly = true)
    public ServicoResponseDTO buscarPorId(String id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));
        return ServicoResponseDTO.fromDomain(servico);
    }

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> listarTodos() {
        return servicoRepository.findAll()
                .stream()
                .map(ServicoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> listarAtivos() {
        return servicoRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(ServicoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServicoResponseDTO atualizar(String id, ServicoRequestDTO request) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));

        servico.setNome(request.getNome());
        servico.setDescricao(request.getDescricao());
        servico.setPreco(request.getPreco());
        servico.setTempoEstimadoMinutos(request.getTempoEstimadoMinutos());

        servico = servicoRepository.save(servico);
        return ServicoResponseDTO.fromDomain(servico);
    }

    @Transactional
    public void desativar(String id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }

    @Transactional
    public void deletar(String id) {
        if (!servicoRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado com ID: " + id);
        }
        servicoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> buscarPorCategoria(String categoria) {
        return servicoRepository.findByCategoria(categoria)
                .stream()
                .map(ServicoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
