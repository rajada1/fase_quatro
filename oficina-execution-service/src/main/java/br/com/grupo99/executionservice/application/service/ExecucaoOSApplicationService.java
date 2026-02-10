package br.com.grupo99.executionservice.application.service;

import br.com.grupo99.executionservice.application.dto.ExecucaoOSRequestDTO;
import br.com.grupo99.executionservice.application.dto.ExecucaoOSResponseDTO;
import br.com.grupo99.executionservice.application.mapper.ExecucaoOSMapper;
import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import br.com.grupo99.executionservice.domain.repository.ExecucaoOSRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application Service para orquestrar operações de ExecucaoOS.
 * 
 * Responsabilidades:
 * - Coordenar chamadas ao domínio
 * - Usar mapper para conversões DTO ↔ Domínio
 * - Chamar repositório para persistência
 */
@Service
public class ExecucaoOSApplicationService {

    private final ExecucaoOSRepository repository;
    private final ExecucaoOSMapper mapper;

    public ExecucaoOSApplicationService(ExecucaoOSRepository repository, ExecucaoOSMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Cria uma nova execução de OS.
     *
     * @param request DTO de requisição
     * @return DTO de resposta
     */
    @Transactional
    public ExecucaoOSResponseDTO criar(ExecucaoOSRequestDTO request) {
        ExecucaoOS execucaoOS = mapper.toDomain(request);
        ExecucaoOS salvo = repository.save(execucaoOS);
        return mapper.toResponseDTO(salvo);
    }

    /**
     * Busca uma execução por ID.
     *
     * @param id ID da execução
     * @return DTO de resposta
     */
    @Transactional(readOnly = true)
    public ExecucaoOSResponseDTO buscarPorId(UUID id) {
        ExecucaoOS execucaoOS = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));
        return mapper.toResponseDTO(execucaoOS);
    }

    /**
     * Busca uma execução por ID da OS.
     *
     * @param osId ID da OS
     * @return DTO de resposta
     */
    @Transactional(readOnly = true)
    public ExecucaoOSResponseDTO buscarPorOsId(UUID osId) {
        ExecucaoOS execucaoOS = repository.findByOsId(osId)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada para OS: " + osId));
        return mapper.toResponseDTO(execucaoOS);
    }

    /**
     * Busca todas as execuções.
     *
     * @return lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<ExecucaoOSResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca execuções por status.
     *
     * @param status status desejado
     * @return lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<ExecucaoOSResponseDTO> buscarPorStatus(StatusExecucao status) {
        return repository.findByStatus(status).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca execuções por mecânico.
     *
     * @param mecanico nome do mecânico
     * @return lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<ExecucaoOSResponseDTO> buscarPorMecanico(String mecanico) {
        return repository.findByMecanico(mecanico).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Inicia uma execução de OS.
     *
     * @param id ID da execução
     * @return DTO de resposta
     */
    @Transactional
    public ExecucaoOSResponseDTO iniciar(UUID id) {
        ExecucaoOS execucaoOS = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));

        execucaoOS.iniciar();
        ExecucaoOS atualizado = repository.save(execucaoOS);
        return mapper.toResponseDTO(atualizado);
    }

    /**
     * Finaliza uma execução de OS.
     *
     * @param id          ID da execução
     * @param observacoes observações finais
     * @return DTO de resposta
     */
    @Transactional
    public ExecucaoOSResponseDTO finalizar(UUID id, String observacoes) {
        ExecucaoOS execucaoOS = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));

        execucaoOS.finalizar(observacoes);
        ExecucaoOS atualizado = repository.save(execucaoOS);
        return mapper.toResponseDTO(atualizado);
    }

    /**
     * Cancela uma execução de OS.
     *
     * @param id     ID da execução
     * @param motivo motivo do cancelamento
     * @return DTO de resposta
     */
    @Transactional
    public ExecucaoOSResponseDTO cancelar(UUID id, String motivo) {
        ExecucaoOS execucaoOS = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));

        execucaoOS.cancelar(motivo);
        ExecucaoOS atualizado = repository.save(execucaoOS);
        return mapper.toResponseDTO(atualizado);
    }

    /**
     * Deleta uma execução.
     *
     * @param id ID da execução
     */
    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Execução não encontrada: " + id);
        }
        repository.deleteById(id);
    }
}
