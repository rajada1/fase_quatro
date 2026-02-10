package br.com.grupo99.customerservice.application.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para requisições de Veículo.
 */
public record VeiculoRequestDTO(
                @NotBlank(message = "Placa é obrigatória") String placa,

                @NotBlank(message = "Marca é obrigatória") String marca,

                @NotBlank(message = "Modelo é obrigatório") String modelo,

                @NotNull(message = "Ano é obrigatório") @Min(1900) @Max(2100) Integer ano,

                String renavam,

                String cor,

                String chassi) {
}
