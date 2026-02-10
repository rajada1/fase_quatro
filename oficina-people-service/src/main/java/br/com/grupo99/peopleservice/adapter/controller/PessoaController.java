package br.com.grupo99.peopleservice.adapter.controller;

import br.com.grupo99.peopleservice.application.dto.PessoaRequestDTO;
import br.com.grupo99.peopleservice.application.dto.PessoaResponseDTO;
import br.com.grupo99.peopleservice.application.service.PessoaApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciar Pessoas.
 * Expõe endpoints CRUD para operações de Pessoa.
 */
@RestController
@RequestMapping("/api/v1/pessoas")
public class PessoaController {

    private final PessoaApplicationService pessoaApplicationService;

    public PessoaController(PessoaApplicationService pessoaApplicationService) {
        this.pessoaApplicationService = pessoaApplicationService;
    }

    /**
     * POST /api/v1/pessoas - Cria uma nova Pessoa
     */
    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criarPessoa(@Valid @RequestBody PessoaRequestDTO requestDTO) {
        PessoaResponseDTO response = pessoaApplicationService.criarPessoa(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/pessoas - Lista todas as Pessoas
     */
    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> listarPessoas() {
        List<PessoaResponseDTO> pessoas = pessoaApplicationService.listarTodas();
        return ResponseEntity.ok(pessoas);
    }

    /**
     * GET /api/v1/pessoas/{id} - Busca Pessoa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> buscarPorId(@PathVariable UUID id) {
        PessoaResponseDTO response = pessoaApplicationService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/pessoas/documento/{documento} - Busca Pessoa por número de
     * documento (CPF/CNPJ)
     * Usado pelo Lambda Auth Service para validação de autenticação.
     */
    @GetMapping("/documento/{documento}")
    public ResponseEntity<PessoaResponseDTO> buscarPorDocumento(@PathVariable String documento) {
        PessoaResponseDTO response = pessoaApplicationService.buscarPorDocumento(documento);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/pessoas/{id} - Atualiza uma Pessoa
     */
    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> atualizarPessoa(
            @PathVariable UUID id,
            @Valid @RequestBody PessoaRequestDTO requestDTO) {
        PessoaResponseDTO response = pessoaApplicationService.atualizarPessoa(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/pessoas/{id} - Deleta uma Pessoa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable UUID id) {
        pessoaApplicationService.deletarPessoa(id);
        return ResponseEntity.noContent().build();
    }
}
