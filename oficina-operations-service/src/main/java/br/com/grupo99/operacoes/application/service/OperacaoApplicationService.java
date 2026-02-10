package br.com.grupo99.operacoes.application.service;

import br.com.grupo99.operacoes.application.dto.OperacaoRequestDTO;
import br.com.grupo99.operacoes.application.dto.OperacaoResponseDTO;
import br.com.grupo99.operacoes.application.exception.BusinessException;
import br.com.grupo99.operacoes.application.exception.ResourceNotFoundException;
import br.com.grupo99.operacoes.adapter.repository.OperacaoRepository;
import br.com.grupo99.operacoes.adapter.event.EventPublishingService;
import br.com.grupo99.operacoes.domain.Operacao;
import br.com.grupo99.operacoes.domain.PrioridadeOperacao;
import br.com.grupo99.operacoes.domain.StatusOperacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperacaoApplicationService {
    private final OperacaoRepository operacaoRepository;
    private final EventPublishingService eventPublishingService;

    @Transactional
    public OperacaoResponseDTO criarOperacao(OperacaoRequestDTO requestDTO) {
        log.info("Criando operação para orçamento: {}", requestDTO.getOrcamentoId());

        String numeroOperacao = "OP-" + System.currentTimeMillis();

        Operacao operacao = Operacao.builder()
                .numeroOperacao(numeroOperacao)
                .orcamentoId(requestDTO.getOrcamentoId())
                .manutencaoId(requestDTO.getManutencaoId())
                .descricao(requestDTO.getDescricao())
                .prioridade(PrioridadeOperacao.valueOf(requestDTO.getPrioridade()))
                .responsavelId(requestDTO.getResponsavelId())
                .tempoEstimadoHoras(requestDTO.getTempoEstimadoHoras())
                .observacoes(requestDTO.getObservacoes())
                .build();

        Operacao saved = operacaoRepository.save(operacao);
        eventPublishingService.publishOperacaoCriada(saved.getId().toString(), saved.getNumeroOperacao());

        log.info("Operação criada com sucesso: {}", saved.getId());
        return OperacaoResponseDTO.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public OperacaoResponseDTO buscarPorId(UUID id) {
        log.info("Buscando operação: {}", id);
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operação não encontrada: " + id));
        return OperacaoResponseDTO.fromDomain(operacao);
    }

    @Transactional(readOnly = true)
    public List<OperacaoResponseDTO> listarTodas() {
        log.info("Listando todas as operações");
        return operacaoRepository.findAll().stream()
                .map(OperacaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OperacaoResponseDTO> listarPorStatus(String status) {
        log.info("Listando operações com status: {}", status);
        return operacaoRepository.findByStatus(StatusOperacao.valueOf(status)).stream()
                .map(OperacaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OperacaoResponseDTO> listarPorOrcamento(UUID orcamentoId) {
        log.info("Listando operações do orçamento: {}", orcamentoId);
        return operacaoRepository.findByOrcamentoId(orcamentoId).stream()
                .map(OperacaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public OperacaoResponseDTO iniciarOperacao(UUID id, UUID responsavelId) {
        log.info("Iniciando operação: {}", id);
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operação não encontrada: " + id));

        if (operacao.getStatus() != StatusOperacao.PLANEJADA && operacao.getStatus() != StatusOperacao.EM_ESPERA) {
            throw new BusinessException("Operação não está em estado que permite iniciar");
        }

        operacao.setStatus(StatusOperacao.EXECUTANDO);
        operacao.setResponsavelId(responsavelId);
        operacao.setDataInicio(LocalDateTime.now());

        Operacao updated = operacaoRepository.save(operacao);
        eventPublishingService.publishOperacaoIniciada(updated.getId().toString(), updated.getStatus().name());

        log.info("Operação iniciada com sucesso: {}", id);
        return OperacaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public OperacaoResponseDTO concluirOperacao(UUID id, Integer tempoRealizadoHoras) {
        log.info("Concluindo operação: {}", id);
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operação não encontrada: " + id));

        if (operacao.getStatus() != StatusOperacao.EXECUTANDO && operacao.getStatus() != StatusOperacao.PAUSADA) {
            throw new BusinessException("Operação não está em execução");
        }

        operacao.setStatus(StatusOperacao.CONCLUIDA);
        operacao.setDataConclusao(LocalDateTime.now());
        operacao.setTempoRealizadoHoras(tempoRealizadoHoras);

        Operacao updated = operacaoRepository.save(operacao);
        eventPublishingService.publishOperacaoConcluida(updated.getId().toString());

        log.info("Operação concluída com sucesso: {}", id);
        return OperacaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public OperacaoResponseDTO pausarOperacao(UUID id) {
        log.info("Pausando operação: {}", id);
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operação não encontrada: " + id));

        if (operacao.getStatus() != StatusOperacao.EXECUTANDO) {
            throw new BusinessException("Apenas operações em execução podem ser pausadas");
        }

        operacao.setStatus(StatusOperacao.PAUSADA);
        Operacao updated = operacaoRepository.save(operacao);

        log.info("Operação pausada com sucesso: {}", id);
        return OperacaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public void deletar(UUID id) {
        log.info("Deletando operação: {}", id);
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operação não encontrada: " + id));

        operacaoRepository.deleteById(id);
        log.info("Operação deletada com sucesso: {}", id);
    }
}
