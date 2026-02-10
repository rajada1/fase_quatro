package br.com.grupo99.customerservice.application.dto;

import br.com.grupo99.customerservice.domain.model.Veiculo;

import java.util.UUID;

public record VeiculoResponseDTO(
        UUID id,
        String placa,
        String renavam,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        String chassi
) {
    public static VeiculoResponseDTO fromDomain(Veiculo veiculo) {
        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getRenavam(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getCor(),
                veiculo.getChassi()
        );
    }
}
