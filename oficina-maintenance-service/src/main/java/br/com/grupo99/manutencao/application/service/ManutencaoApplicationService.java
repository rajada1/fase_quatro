package br.com.grupo99.manutencao.application.service;

import br.com.grupo99.manutencao.application.dto.ManutencaoRequestDTO;
import br.com.grupo99.manutencao.application.dto.ManutencaoResponseDTO;
import br.com.grupo99.manutencao.application.exception.ResourceNotFoundException;
import br.com.grupo99.manutencao.adapter.repository.ManutencaoRepository;
import br.com.grupo99.manutencao.adapter.event.EventPublishingService;
import br.com.grupo99.manutencao.domain.Manutencao;
import br.com.grupo99.manutencao.domain.StatusManutencao;
import br.com.grupo99.manutencao.domain.TipoManutencao;
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
public class ManutencaoApplicationService {
    private final ManutencaoRepository manuntencaoRepository;
    private final EventPublishingService eventPublishingService;

    @Transactional
    public ManutencaoResponseDTO criar(ManutencaoRequestDTO requestDTO) {
        log.info("Criando manutenção para veículo: {}", requestDTO.getVeiculoId());

        Manutencao manutencao = Manutencao.builder()
                .veiculoId(requestDTO.getVeiculoId())
                .dataAgendada(requestDTO.getDataAgendada())
                .tipoManutencao(TipoManutencao.valueOf(requestDTO.getTipoManutencao()))
                .descricao(requestDTO.getDescricao())
                .observacoes(requestDTO.getObservacoes())
                .build();

        Manutencao saved = manuntencaoRepository.save(manutencao);
        eventPublishingService.publishManutencaoCriada(saved.getId().toString(), saved.getVeiculoId().toString());

        log.info("Manutenção criada com sucesso: {}", saved.getId());
        return ManutencaoResponseDTO.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public ManutencaoResponseDTO buscarPorId(UUID id) {
        log.info("Buscando manutenção: {}", id);
        Manutencao manutencao = manuntencaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção não encontrada: " + id));
        return ManutencaoResponseDTO.fromDomain(manutencao);
    }

    @Transactional(readOnly = true)
    public List<ManutencaoResponseDTO> listarTodas() {
        log.info("Listando todas as manutenções");
        return manuntencaoRepository.findAll().stream()
                .map(ManutencaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ManutencaoResponseDTO> listarPorVeiculo(UUID veiculoId) {
        log.info("Listando manutenções do veículo: {}", veiculoId);
        return manuntencaoRepository.findByVeiculoId(veiculoId).stream()
                .map(ManutencaoResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public ManutencaoResponseDTO atualizar(UUID id, ManutencaoRequestDTO requestDTO) {
        log.info("Atualizando manutenção: {}", id);
        Manutencao manutencao = manuntencaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção não encontrada: " + id));

        manutencao.setDataAgendada(requestDTO.getDataAgendada());
        manutencao.setTipoManutencao(TipoManutencao.valueOf(requestDTO.getTipoManutencao()));
        manutencao.setDescricao(requestDTO.getDescricao());
        manutencao.setObservacoes(requestDTO.getObservacoes());

        Manutencao updated = manuntencaoRepository.save(manutencao);
        eventPublishingService.publishManutencaoAtualizada(updated.getId().toString(), updated.getStatus().name());

        log.info("Manutenção atualizada com sucesso: {}", id);
        return ManutencaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public void deletar(UUID id) {
        log.info("Deletando manutenção: {}", id);
        Manutencao manutencao = manuntencaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção não encontrada: " + id));

        manuntencaoRepository.deleteById(id);
        log.info("Manutenção deletada com sucesso: {}", id);
    }

    @Transactional
    public ManutencaoResponseDTO iniciarManutencao(UUID id) {
        log.info("Iniciando manutenção: {}", id);
        Manutencao manutencao = manuntencaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção não encontrada: " + id));

        manutencao.setStatus(StatusManutencao.EM_EXECUCAO);
        Manutencao updated = manuntencaoRepository.save(manutencao);

        eventPublishingService.publishManutencaoAtualizada(updated.getId().toString(), updated.getStatus().name());
        log.info("Manutenção iniciada com sucesso: {}", id);
        return ManutencaoResponseDTO.fromDomain(updated);
    }

    @Transactional
    public ManutencaoResponseDTO concluirManutencao(UUID id) {
        log.info("Concluindo manutenção: {}", id);
        Manutencao manutencao = manuntencaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção não encontrada: " + id));

        manutencao.setStatus(StatusManutencao.CONCLUIDA);
        manutencao.setDataConclusao(LocalDateTime.now());
        Manutencao updated = manuntencaoRepository.save(manutencao);

        eventPublishingService.publishManutencaoConcluida(updated.getId().toString());
        log.info("Manutenção concluída com sucesso: {}", id);
        return ManutencaoResponseDTO.fromDomain(updated);
    }
}
