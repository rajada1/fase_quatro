package br.com.grupo99.manutencao.adapter.web;

import br.com.grupo99.manutencao.application.dto.ManutencaoRequestDTO;
import br.com.grupo99.manutencao.application.dto.ManutencaoResponseDTO;
import br.com.grupo99.manutencao.application.service.ManutencaoApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manutencoes")
@RequiredArgsConstructor
public class ManutencaoController {
    private final ManutencaoApplicationService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<ManutencaoResponseDTO> criar(@Valid @RequestBody ManutencaoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(requestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'MECANICO', 'ADMIN')")
    public ResponseEntity<List<ManutencaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'MECANICO', 'ADMIN')")
    public ResponseEntity<ManutencaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/veiculo/{veiculoId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'MECANICO', 'ADMIN')")
    public ResponseEntity<List<ManutencaoResponseDTO>> listarPorVeiculo(@PathVariable UUID veiculoId) {
        return ResponseEntity.ok(service.listarPorVeiculo(veiculoId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<ManutencaoResponseDTO> atualizar(@PathVariable UUID id,
            @Valid @RequestBody ManutencaoRequestDTO requestDTO) {
        return ResponseEntity.ok(service.atualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<ManutencaoResponseDTO> iniciar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.iniciarManutencao(id));
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<ManutencaoResponseDTO> concluir(@PathVariable UUID id) {
        return ResponseEntity.ok(service.concluirManutencao(id));
    }
}
