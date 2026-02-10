package br.com.grupo99.catalogo.adapter.web;

import br.com.grupo99.catalogo.application.dto.ServicoRequestDTO;
import br.com.grupo99.catalogo.application.dto.ServicoResponseDTO;
import br.com.grupo99.catalogo.application.service.ServicoApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para Serviço.
 * Migrado para MongoDB/DocumentDB - ID agora é String.
 */
@RestController
@RequestMapping("/api/v1/servicos")
public class ServicoController {
    private final ServicoApplicationService servicoService;

    public ServicoController(ServicoApplicationService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicoService.criarServico(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<ServicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/ativos")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<ServicoResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(servicoService.listarAtivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<ServicoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody ServicoRequestDTO request) {
        return ResponseEntity.ok(servicoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativar(@PathVariable String id) {
        servicoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<ServicoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(servicoService.buscarPorCategoria(categoria));
    }
}
