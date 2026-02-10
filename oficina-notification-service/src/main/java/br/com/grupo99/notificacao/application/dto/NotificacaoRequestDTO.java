package br.com.grupo99.notificacao.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoRequestDTO {
    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Email inválido")
    private String destinatarioEmail;

    @NotNull(message = "Tipo de notificação não pode ser nulo")
    private String tipoNotificacao;

    @NotBlank(message = "Assunto não pode ser vazio")
    @Size(min = 5, max = 200, message = "Assunto deve ter entre 5 e 200 caracteres")
    private String assunto;

    @NotBlank(message = "Mensagem não pode ser vazia")
    @Size(min = 10, max = 2000, message = "Mensagem deve ter entre 10 e 2000 caracteres")
    private String mensagem;
}
