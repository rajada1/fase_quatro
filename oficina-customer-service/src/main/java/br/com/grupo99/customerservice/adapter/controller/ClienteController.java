package br.com.grupo99.customerservice.adapter.controller;

import br.com.grupo99.customerservice.application.dto.ClienteRequestDTO;
import br.com.grupo99.customerservice.application.dto.ClienteResponseDTO;
import br.com.grupo99.customerservice.application.service.CustomerApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para operações de Cliente.
 * Expõe endpoints para CRUD de clientes.
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final CustomerApplicationService customerApplicationService;

    public ClienteController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    /**
     * POST /api/v1/clientes - Criar novo cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO responseDTO = customerApplicationService.criarCliente(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * GET /api/v1/clientes/{id} - Buscar cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable UUID id) {
        ClienteResponseDTO responseDTO = customerApplicationService.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * GET /api/v1/clientes - Listar todos os clientes
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<ClienteResponseDTO> clientes = customerApplicationService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * PUT /api/v1/clientes/{id} - Atualizar cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO responseDTO = customerApplicationService.atualizarCliente(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE /api/v1/clientes/{id} - Deletar cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        customerApplicationService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
