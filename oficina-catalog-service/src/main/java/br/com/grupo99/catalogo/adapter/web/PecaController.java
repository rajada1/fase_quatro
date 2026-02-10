package br.com.grupo99.catalogo.adapter.web;

import br.com.grupo99.catalogo.application.dto.PecaRequestDTO;
import br.com.grupo99.catalogo.application.dto.PecaResponseDTO;
import br.com.grupo99.catalogo.application.service.PecaApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para Peça.
 * Migrado para MongoDB/DocumentDB - ID agora é String.
 */
@RestController
@RequestMapping("/api/v1/pecas")
public class PecaController {
    private final PecaApplicationService pecaService;

    public PecaController(PecaApplicationService pecaService) {
        this.pecaService = pecaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    public ResponseEntity<PecaResponseDTO> criar(@Valid @RequestBody PecaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pecaService.criarPeca(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<PecaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(pecaService.listarTodas());
    }

    @GetMapping("/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<PecaResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(pecaService.listarAtivas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<PecaResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(pecaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    public ResponseEntity<PecaResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody PecaRequestDTO request) {
        return ResponseEntity.ok(pecaService.atualizar(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativar(@PathVariable String id) {
        pecaService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        pecaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/decrementar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    public ResponseEntity<Void> decrementarEstoque(
            @PathVariable String id,
            @RequestParam Integer quantidade) {
        pecaService.decrementarEstoque(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/incrementar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    public ResponseEntity<Void> incrementarEstoque(
            @PathVariable String id,
            @RequestParam Integer quantidade) {
        pecaService.incrementarEstoque(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<PecaResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(pecaService.buscarPorCategoria(categoria));
    }

    @GetMapping("/marca/{marca}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'CLIENTE')")
    public ResponseEntity<List<PecaResponseDTO>> buscarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(pecaService.buscarPorMarca(marca));
    }
}
