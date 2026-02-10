package br.com.grupo99.executionservice.infrastructure.messaging;

import br.com.grupo99.executionservice.domain.events.DiagnosticoConcluidoEvent;
import br.com.grupo99.executionservice.domain.events.ExecucaoConcluidaEvent;
import br.com.grupo99.executionservice.domain.events.ExecucaoFalhouEvent;
import br.com.grupo99.executionservice.domain.model.ExecucaoOS;

/**
 * Interface de abstração para publicação de eventos de execução
 * Permite trocar implementação SQS ↔ Kafka facilmente (Strategy Pattern)
 */
public interface ExecutionEventPublisherPort {

    /**
     * Publica evento de diagnóstico concluído
     */
    void publishDiagnosticoConcluido(DiagnosticoConcluidoEvent event);

    /**
     * Publica evento de execução concluída
     */
    void publishExecucaoConcluida(ExecucaoConcluidaEvent event);

    /**
     * Publica evento de compensação - Execução falhou
     */
    void publishExecucaoFalhou(ExecucaoFalhouEvent event);

    // ===================== MÉTODOS DE CONVENIÊNCIA =====================

    /**
     * Publicar diagnóstico concluído a partir de uma execução
     */
    void publicarDiagnosticoConcluido(ExecucaoOS execucao);

    /**
     * Publicar execução concluída a partir de uma execução
     */
    void publicarExecucaoConcluida(ExecucaoOS execucao);

    /**
     * Publicar falha de execução a partir de uma execução
     */
    void publicarExecucaoFalhou(ExecucaoOS execucao, String motivo, boolean requerRetrabalho);
}
