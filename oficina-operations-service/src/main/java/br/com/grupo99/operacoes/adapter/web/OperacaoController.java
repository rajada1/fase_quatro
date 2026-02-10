package br.com.grupo99.operacoes.adapter.web;

import br.com.grupo99.operacoes.application.dto.OperacaoRequestDTO;
import br.com.grupo99.operacoes.application.dto.OperacaoResponseDTO;
import br.com.grupo99.operacoes.application.service.OperacaoApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operacoes")
@RequiredArgsConstructor
public class OperacaoController {
    private final OperacaoApplicationService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OperacaoResponseDTO> criarOperacao(@Valid @RequestBody OperacaoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarOperacao(requestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<List<OperacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OperacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<List<OperacaoResponseDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @GetMapping("/orcamento/{orcamentoId}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<List<OperacaoResponseDTO>> listarPorOrcamento(@PathVariable UUID orcamentoId) {
        return ResponseEntity.ok(service.listarPorOrcamento(orcamentoId));
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OperacaoResponseDTO> iniciarOperacao(@PathVariable UUID id,
            @RequestParam UUID responsavelId) {
        return ResponseEntity.ok(service.iniciarOperacao(id, responsavelId));
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OperacaoResponseDTO> concluirOperacao(@PathVariable UUID id,
            @RequestParam Integer tempoRealizadoHoras) {
        return ResponseEntity.ok(service.concluirOperacao(id, tempoRealizadoHoras));
    }

    @PatchMapping("/{id}/pausar")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OperacaoResponseDTO> pausarOperacao(@PathVariable UUID id) {
        return ResponseEntity.ok(service.pausarOperacao(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
