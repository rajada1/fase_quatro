package br.com.grupo99.notificacao.adapter.web;

import br.com.grupo99.notificacao.application.dto.NotificacaoRequestDTO;
import br.com.grupo99.notificacao.application.dto.NotificacaoResponseDTO;
import br.com.grupo99.notificacao.application.service.NotificacaoApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {
    private final NotificacaoApplicationService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<NotificacaoResponseDTO> criarNotificacao(
            @Valid @RequestBody NotificacaoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarNotificacao(requestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'MECANICO', 'ADMIN')")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'MECANICO', 'ADMIN')")
    public ResponseEntity<NotificacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'MECANICO', 'ADMIN')")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.listarPorEmail(email));
    }

    @PatchMapping("/{id}/enviada")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<NotificacaoResponseDTO> marcarComoEnviada(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarComoEnviada(id));
    }

    @PatchMapping("/{id}/falha")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<NotificacaoResponseDTO> marcarComoFalha(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarComoFalha(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
