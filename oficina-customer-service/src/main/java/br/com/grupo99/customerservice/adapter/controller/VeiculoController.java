package br.com.grupo99.customerservice.adapter.controller;

import br.com.grupo99.customerservice.application.dto.VeiculoRequestDTO;
import br.com.grupo99.customerservice.application.dto.VeiculoResponseDTO;
import br.com.grupo99.customerservice.application.service.VeiculoApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelos endpoints de Veículo.
 */
@RestController
@RequestMapping("/api/v1/clientes/{pessoaId}/veiculos")
public class VeiculoController {

    private final VeiculoApplicationService veiculoApplicationService;

    public VeiculoController(VeiculoApplicationService veiculoApplicationService) {
        this.veiculoApplicationService = veiculoApplicationService;
    }

    /**
     * POST - Criar um novo veículo para um cliente.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<VeiculoResponseDTO> criarVeiculo(
            @PathVariable UUID pessoaId,
            @Valid @RequestBody VeiculoRequestDTO requestDTO) {
        VeiculoResponseDTO response = veiculoApplicationService.criarVeiculo(pessoaId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET - Listar todos os veículos de um cliente.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<VeiculoResponseDTO>> listarVeiculosDoCliente(
            @PathVariable UUID pessoaId) {
        List<VeiculoResponseDTO> response = veiculoApplicationService.listarVeiculosDoCliente(pessoaId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Buscar um veículo específico.
     */
    @GetMapping("/{veiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculo(
            @PathVariable UUID pessoaId,
            @PathVariable UUID veiculoId) {
        VeiculoResponseDTO response = veiculoApplicationService.buscarPorId(veiculoId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT - Atualizar um veículo.
     */
    @PutMapping("/{veiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<VeiculoResponseDTO> atualizarVeiculo(
            @PathVariable UUID pessoaId,
            @PathVariable UUID veiculoId,
            @Valid @RequestBody VeiculoRequestDTO requestDTO) {
        VeiculoResponseDTO response = veiculoApplicationService.atualizarVeiculo(pessoaId, veiculoId, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - Deletar um veículo.
     */
    @DeleteMapping("/{veiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Void> deletarVeiculo(
            @PathVariable UUID pessoaId,
            @PathVariable UUID veiculoId) {
        veiculoApplicationService.deletarVeiculo(pessoaId, veiculoId);
        return ResponseEntity.noContent().build();
    }
}
