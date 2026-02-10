package br.com.grupo99.notificacao.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.grupo99.notificacao.domain.Notificacao;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoResponseDTO {
    private UUID id;
    private String destinatarioEmail;
    private String tipoNotificacao;
    private String assunto;
    private String mensagem;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataEnvio;

    private Integer tentativas;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static NotificacaoResponseDTO fromDomain(Notificacao notificacao) {
        return NotificacaoResponseDTO.builder()
                .id(notificacao.getId())
                .destinatarioEmail(notificacao.getDestinatarioEmail())
                .tipoNotificacao(notificacao.getTipoNotificacao().name())
                .assunto(notificacao.getAssunto())
                .mensagem(notificacao.getMensagem())
                .status(notificacao.getStatus().name())
                .dataEnvio(notificacao.getDataEnvio())
                .tentativas(notificacao.getTentativas())
                .createdAt(notificacao.getCreatedAt())
                .updatedAt(notificacao.getUpdatedAt())
                .build();
    }
}
