package br.com.grupo99.executionservice.infrastructure.messaging;

import br.com.grupo99.executionservice.domain.events.ExecucaoFalhouEvent;
import br.com.grupo99.executionservice.domain.events.OrcamentoAprovadoEvent;
import br.com.grupo99.executionservice.domain.events.OSCriadaEvent;
import br.com.grupo99.executionservice.domain.model.ExecucaoOS;
import br.com.grupo99.executionservice.domain.model.StatusExecucao;
import br.com.grupo99.executionservice.domain.repository.ExecucaoOSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event Listener - Compensação Saga Pattern
 * Métodos de compensação para eventos de falha
 */
@Slf4j
@Component
public class ExecutionEventListener {

    private final ExecucaoOSRepository execucaoOSRepository;
    private final ExecutionEventPublisher eventPublisher;

    public ExecutionEventListener(ExecucaoOSRepository execucaoOSRepository,
            ExecutionEventPublisher eventPublisher) {
        this.execucaoOSRepository = execucaoOSRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * COMPENSAÇÃO: Quando orçamento é rejeitado, cancelar execução
     */
    public void handleOrcamentoRejeitado(UUID osId, String motivo) {
        try {
            log.warn("🔄 Iniciando compensação: Orçamento rejeitado para OS: {}", osId);

            ExecucaoOS execucaoOS = execucaoOSRepository.findByOsId(osId)
                    .orElse(null);

            if (execucaoOS == null) {
                log.info("Execução não existe para OS: {}. Compensação não necessária.", osId);
                return;
            }

            // Cancelar execução
            execucaoOS.setStatus(StatusExecucao.CANCELADA);
            execucaoOSRepository.save(execucaoOS);

            log.warn("✅ Compensação concluída: Execução {} cancelada. Motivo: {}",
                    execucaoOS.getId(), motivo);

        } catch (Exception e) {
            log.error("❌ ERRO CRÍTICO na compensação da execução para OS {}: {}", osId, e.getMessage(), e);
        }
    }

    /**
     * COMPENSAÇÃO: Quando OS é cancelada, cancelar execução
     */
    public void handleOSCancelada(UUID osId, String motivo) {
        try {
            log.warn("🔄 Iniciando compensação: OS cancelada: {}", osId);

            ExecucaoOS execucaoOS = execucaoOSRepository.findByOsId(osId)
                    .orElse(null);

            if (execucaoOS == null) {
                log.info("Execução não existe para OS: {}. Compensação não necessária.", osId);
                return;
            }

            // Se execução está em andamento, cancelar
            if (execucaoOS.getStatus() == StatusExecucao.EM_ANDAMENTO) {
                execucaoOS.setStatus(StatusExecucao.CANCELADA);

                // Publicar evento de falha
                ExecucaoFalhouEvent falhouEvent = ExecucaoFalhouEvent.builder()
                        .execucaoId(execucaoOS.getId())
                        .osId(osId)
                        .motivo("OS foi cancelada: " + motivo)
                        .etapaFalha("OS_CANCELADA")
                        .timestamp(LocalDateTime.now())
                        .build();
                eventPublisher.publishExecucaoFalhou(falhouEvent);
            } else {
                execucaoOS.setStatus(StatusExecucao.CANCELADA);
            }

            execucaoOSRepository.save(execucaoOS);

            log.warn("✅ Compensação concluída: Execução {} cancelada devido ao cancelamento da OS",
                    execucaoOS.getId());

        } catch (Exception e) {
            log.error("❌ ERRO CRÍTICO na compensação da execução para OS {}: {}", osId, e.getMessage(), e);
        }
    }
}
