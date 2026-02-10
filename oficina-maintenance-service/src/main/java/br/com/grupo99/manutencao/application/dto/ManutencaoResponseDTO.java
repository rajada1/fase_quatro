package br.com.grupo99.manutencao.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.grupo99.manutencao.domain.Manutencao;
import br.com.grupo99.manutencao.domain.StatusManutencao;
import br.com.grupo99.manutencao.domain.TipoManutencao;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManutencaoResponseDTO {
    private UUID id;
    private UUID veiculoId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAgendada;

    private String tipoManutencao;
    private String descricao;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataConclusao;

    private String observacoes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static ManutencaoResponseDTO fromDomain(Manutencao manutencao) {
        return ManutencaoResponseDTO.builder()
                .id(manutencao.getId())
                .veiculoId(manutencao.getVeiculoId())
                .dataAgendada(manutencao.getDataAgendada())
                .tipoManutencao(manutencao.getTipoManutencao().name())
                .descricao(manutencao.getDescricao())
                .status(manutencao.getStatus().name())
                .dataConclusao(manutencao.getDataConclusao())
                .observacoes(manutencao.getObservacoes())
                .createdAt(manutencao.getCreatedAt())
                .updatedAt(manutencao.getUpdatedAt())
                .build();
    }
}
