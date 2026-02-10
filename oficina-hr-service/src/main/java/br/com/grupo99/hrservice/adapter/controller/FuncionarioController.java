package br.com.grupo99.hrservice.adapter.controller;

import br.com.grupo99.hrservice.application.dto.FuncionarioRequestDTO;
import br.com.grupo99.hrservice.application.dto.FuncionarioResponseDTO;
import br.com.grupo99.hrservice.application.service.FuncionarioApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelos endpoints de Funcionario.
 */
@RestController
@RequestMapping("/api/v1/funcionarios")
public class FuncionarioController {

    private final FuncionarioApplicationService funcionarioApplicationService;

    public FuncionarioController(FuncionarioApplicationService funcionarioApplicationService) {
        this.funcionarioApplicationService = funcionarioApplicationService;
    }

    /**
     * POST - Criar um novo funcionário.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> criarFuncionario(
            @Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO response = funcionarioApplicationService.criarFuncionario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET - Listar todos os funcionários.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
        List<FuncionarioResponseDTO> response = funcionarioApplicationService.listarTodos();
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Buscar um funcionário pelo pessoaId.
     */
    @GetMapping("/{pessoaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> buscarFuncionario(
            @PathVariable UUID pessoaId) {
        FuncionarioResponseDTO response = funcionarioApplicationService.buscarPorId(pessoaId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT - Atualizar um funcionário.
     */
    @PutMapping("/{pessoaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> atualizarFuncionario(
            @PathVariable UUID pessoaId,
            @Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO response = funcionarioApplicationService.atualizarFuncionario(pessoaId, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - Deletar um funcionário.
     */
    @DeleteMapping("/{pessoaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarFuncionario(
            @PathVariable UUID pessoaId) {
        funcionarioApplicationService.deletarFuncionario(pessoaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH - Desativar um funcionário.
     */
    @PatchMapping("/{pessoaId}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> desativarFuncionario(
            @PathVariable UUID pessoaId) {
        FuncionarioResponseDTO response = funcionarioApplicationService.desativarFuncionario(pessoaId);
        return ResponseEntity.ok(response);
    }
}
