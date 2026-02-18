package br.com.grupo99.executionservice.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain Events - Testes unitários")
class DomainEventsTest {

    @Nested
    @DisplayName("DiagnosticoConcluidoEvent")
    class DiagnosticoConcluidoEventTests {

        @Test
        @DisplayName("Deve criar evento via builder")
        void deveCriarEventoViaBuilder() {
            UUID osId = UUID.randomUUID();
            UUID execucaoId = UUID.randomUUID();

            DiagnosticoConcluidoEvent event = DiagnosticoConcluidoEvent.builder()
                    .osId(osId)
                    .execucaoId(execucaoId)
                    .diagnostico("Motor saudável")
                    .valorEstimado(new BigDecimal("500.00"))
                    .timestamp(LocalDateTime.now())
                    .build();

            assertThat(event.getOsId()).isEqualTo(osId);
            assertThat(event.getExecucaoId()).isEqualTo(execucaoId);
            assertThat(event.getDiagnostico()).isEqualTo("Motor saudável");
            assertThat(event.getValorEstimado()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(event.getEventType()).isEqualTo("DIAGNOSTICO_CONCLUIDO");
        }
    }

    @Nested
    @DisplayName("ExecucaoConcluidaEvent")
    class ExecucaoConcluidaEventTests {

        @Test
        @DisplayName("Deve criar evento via builder")
        void deveCriarEventoViaBuilder() {
            UUID execucaoId = UUID.randomUUID();
            UUID osId = UUID.randomUUID();

            ExecucaoConcluidaEvent event = ExecucaoConcluidaEvent.builder()
                    .execucaoId(execucaoId)
                    .osId(osId)
                    .mecanico("João")
                    .observacoes("Execução concluída sem problemas")
                    .timestamp(LocalDateTime.now())
                    .build();

            assertThat(event.getExecucaoId()).isEqualTo(execucaoId);
            assertThat(event.getOsId()).isEqualTo(osId);
            assertThat(event.getMecanico()).isEqualTo("João");
            assertThat(event.getObservacoes()).isEqualTo("Execução concluída sem problemas");
            assertThat(event.getEventType()).isEqualTo("EXECUCAO_CONCLUIDA");
        }
    }

    @Nested
    @DisplayName("ExecucaoFalhouEvent")
    class ExecucaoFalhouEventTests {

        @Test
        @DisplayName("Deve criar evento via builder")
        void deveCriarEventoViaBuilder() {
            UUID execucaoId = UUID.randomUUID();
            UUID osId = UUID.randomUUID();

            ExecucaoFalhouEvent event = ExecucaoFalhouEvent.builder()
                    .execucaoId(execucaoId)
                    .osId(osId)
                    .motivo("Peça danificada")
                    .etapaFalha("TROCA_PECA")
                    .requerRetrabalho(true)
                    .timestamp(LocalDateTime.now())
                    .build();

            assertThat(event.getExecucaoId()).isEqualTo(execucaoId);
            assertThat(event.getOsId()).isEqualTo(osId);
            assertThat(event.getMotivo()).isEqualTo("Peça danificada");
            assertThat(event.getEtapaFalha()).isEqualTo("TROCA_PECA");
            assertThat(event.getRequerRetrabalho()).isTrue();
            assertThat(event.getEventType()).isEqualTo("EXECUCAO_FALHOU");
        }

        @Test
        @DisplayName("Deve ter requerRetrabalho false como padrão")
        void deveTermRequerRetrabalhoPadrao() {
            ExecucaoFalhouEvent event = ExecucaoFalhouEvent.builder().build();
            assertThat(event.getRequerRetrabalho()).isFalse();
        }
    }

    @Nested
    @DisplayName("OrcamentoAprovadoEvent")
    class OrcamentoAprovadoEventTests {

        @Test
        @DisplayName("Deve criar evento com construtor")
        void deveCriarEventoComConstrutor() {
            UUID orcamentoId = UUID.randomUUID();
            UUID osId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();

            OrcamentoAprovadoEvent event = new OrcamentoAprovadoEvent(
                    orcamentoId, osId, timestamp, "ORCAMENTO_APROVADO");

            assertThat(event.getOrcamentoId()).isEqualTo(orcamentoId);
            assertThat(event.getOsId()).isEqualTo(osId);
            assertThat(event.getTimestamp()).isEqualTo(timestamp);
            assertThat(event.getEventType()).isEqualTo("ORCAMENTO_APROVADO");
        }
    }

    @Nested
    @DisplayName("OSCriadaEvent")
    class OSCriadaEventTests {

        @Test
        @DisplayName("Deve criar evento com construtor completo")
        void deveCriarEventoComConstrutorCompleto() {
            UUID osId = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            UUID veiculoId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();

            OSCriadaEvent event = new OSCriadaEvent(
                    osId, clienteId, veiculoId, "Revisão geral", timestamp, "OS_CRIADA");

            assertThat(event.getOsId()).isEqualTo(osId);
            assertThat(event.getClienteId()).isEqualTo(clienteId);
            assertThat(event.getVeiculoId()).isEqualTo(veiculoId);
            assertThat(event.getDescricao()).isEqualTo("Revisão geral");
            assertThat(event.getEventType()).isEqualTo("OS_CRIADA");
        }

        @Test
        @DisplayName("Deve criar via construtor vazio e setters")
        void deveCriarViaConstrutorVazio() {
            OSCriadaEvent event = new OSCriadaEvent();
            UUID osId = UUID.randomUUID();
            event.setOsId(osId);

            assertThat(event.getOsId()).isEqualTo(osId);
        }
    }
}
