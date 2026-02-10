package br.com.grupo99.customerservice.application.dto;

import br.com.grupo99.customerservice.domain.model.Cliente;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para resposta de Cliente.
 */
public record ClienteResponseDTO(
                UUID pessoaId,
                List<VeiculoResponseDTO> veiculos,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        public static ClienteResponseDTO fromDomain(Cliente cliente) {
                List<VeiculoResponseDTO> veiculos = cliente.getVeiculos().stream()
                                .map(VeiculoResponseDTO::fromDomain)
                                .toList();

                return new ClienteResponseDTO(
                                cliente.getPessoaId(),
                                veiculos,
                                cliente.getCreatedAt(),
                                cliente.getUpdatedAt());
        }
}
