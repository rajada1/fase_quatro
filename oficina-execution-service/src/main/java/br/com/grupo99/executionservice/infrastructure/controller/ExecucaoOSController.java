package br.com.grupo99.executionservice.infrastructure.controller;

import br.com.grupo99.executionservice.application.dto.ExecucaoOSRequestDTO;
import br.com.grupo99.executionservice.application.dto.ExecucaoOSResponseDTO;
import br.com.grupo99.executionservice.application.service.ExecucaoOSApplicationService;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller para gerenciar Execuções de Ordem de Serviço.
 * 
 * Esta camada é responsável apenas por:
 * - Receber requisições HTTP
 * - Chamar o Application Service
 * - Retornar respostas HTTP
 * 
 * Toda lógica de negócio permanece na camada de domínio.
 */
@RestController
@RequestMapping("/api/v1/execucoes-os")
@Tag(name = "Execução de OS", description = "Endpoints para gerenciar execução de ordens de serviço")
public class ExecucaoOSController {

    private final ExecucaoOSApplicationService applicationService;

    public ExecucaoOSController(ExecucaoOSApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @Operation(summary = "Criar nova execução de OS")
    public ResponseEntity<ExecucaoOSResponseDTO> criar(@RequestBody ExecucaoOSRequestDTO request) {
        ExecucaoOSResponseDTO response = applicationService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar execução por ID")
    public ResponseEntity<ExecucaoOSResponseDTO> buscarPorId(@PathVariable UUID id) {
        ExecucaoOSResponseDTO response = applicationService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/os/{osId}")
    @Operation(summary = "Buscar execução por ID da OS")
    public ResponseEntity<ExecucaoOSResponseDTO> buscarPorOsId(@PathVariable UUID osId) {
        ExecucaoOSResponseDTO response = applicationService.buscarPorOsId(osId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as execuções")
    public ResponseEntity<List<ExecucaoOSResponseDTO>> listarTodas() {
        List<ExecucaoOSResponseDTO> response = applicationService.listarTodas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar execuções por status")
    public ResponseEntity<List<ExecucaoOSResponseDTO>> buscarPorStatus(@PathVariable StatusExecucao status) {
        List<ExecucaoOSResponseDTO> response = applicationService.buscarPorStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mecanico/{mecanico}")
    @Operation(summary = "Buscar execuções por mecânico")
    public ResponseEntity<List<ExecucaoOSResponseDTO>> buscarPorMecanico(@PathVariable String mecanico) {
        List<ExecucaoOSResponseDTO> response = applicationService.buscarPorMecanico(mecanico);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar execução de OS")
    public ResponseEntity<ExecucaoOSResponseDTO> iniciar(@PathVariable UUID id) {
        ExecucaoOSResponseDTO response = applicationService.iniciar(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar execução de OS")
    public ResponseEntity<ExecucaoOSResponseDTO> finalizar(
            @PathVariable UUID id,
            @RequestParam(required = false) String observacoes) {
        ExecucaoOSResponseDTO response = applicationService.finalizar(id, observacoes);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar execução de OS")
    public ResponseEntity<ExecucaoOSResponseDTO> cancelar(
            @PathVariable UUID id,
            @RequestParam(required = false) String motivo) {
        ExecucaoOSResponseDTO response = applicationService.cancelar(id, motivo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar execução de OS")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        applicationService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
