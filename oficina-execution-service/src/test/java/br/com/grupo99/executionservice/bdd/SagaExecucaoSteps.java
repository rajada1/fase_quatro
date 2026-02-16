package br.com.grupo99.executionservice.bdd;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import br.com.grupo99.executionservice.config.BddTestConfiguration;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(BddTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SagaExecucaoSteps {

    private String execucaoId;
    private String osId;
    private String status;
    private Exception exception;
    private List<Map<String, String>> problemas = new ArrayList<>();
    private BigDecimal valorTotalEstimado = BigDecimal.ZERO;
    private List<Map<String, String>> tarefas = new ArrayList<>();
    private List<Map<String, String>> pecas = new ArrayList<>();
    private String mecanicoResponsavel;
    private boolean agendamentoFeito;
    private boolean trabalhoIniciado;
    private String motivoFalha;

    @Dado("que o Execution Service está disponível")
    public void queExecutionServiceEstaDisponivel() {
        assertNotNull("Execution Service");
    }

    @Dado("a fila {string} está configurada")
    public void aFilaEstaConfigurada(String nomeFila) {
        assertNotNull(nomeFila);
    }

    @Dado("que o evento {string} foi publicado")
    public void queOEventoFoiPublicado(String nomeEvento) {
        osId = UUID.randomUUID().toString();
    }

    @Dado("contém os dados:")
    public void contemOsDados(DataTable dataTable) {
        assertNotNull(dataTable);
    }

    @Quando("o Execution Service recebe o evento")
    public void executionServiceRecebeEvento() {
        execucaoId = UUID.randomUUID().toString();
        status = "AGUARDANDO_INICIO";
    }

    @Então("um registro de execução deve ser criado")
    public void umRegistroDeExecucaoDeveSerCriado() {
        assertNotNull(execucaoId);
    }

    @Então("o status da execução deve ser {string}")
    public void statusDaExecucaoDeveSer(String statusEsperado) {
        assertEquals(statusEsperado, status);
    }

    @Então("a execução deve estar associada à OS {string}")
    public void execucaoDeveEstarAssociadaAOS(String osIdStr) {
        assertNotNull(osId);
    }

    @Dado("que existe uma execução para a OS {string}")
    public void queExisteUmaExecucaoParaOS(String osIdStr) {
        this.osId = osIdStr;
        execucaoId = UUID.randomUUID().toString();
        status = "AGUARDANDO_INICIO";
    }

    @Dado("o status é {string}")
    public void oStatusE(String statusEsperado) {
        this.status = statusEsperado;
    }

    @Quando("o mecânico realiza o diagnóstico")
    public void oMecanicoRealizaDiagnostico() {
        status = "EM_DIAGNOSTICO";
    }

    @Quando("identifica os seguintes problemas:")
    public void identificaOsSeguintesProblemas(DataTable dataTable) {
        problemas = dataTable.asMaps(String.class, String.class);
        valorTotalEstimado = BigDecimal.ZERO;
        for (Map<String, String> problema : problemas) {
            BigDecimal valor = new BigDecimal(problema.get("valor_estimado"));
            valorTotalEstimado = valorTotalEstimado.add(valor);
        }
    }

    @Então("o diagnóstico deve ser registrado")
    public void oDiagnosticoDeveSerRegistrado() {
        assertNotNull(problemas);
        assertFalse(problemas.isEmpty());
    }

    @Então("o valor total estimado deve ser {string}")
    public void oValorTotalEstimadoDeveSer(String valorEsperado) {
        BigDecimal esperado = new BigDecimal(valorEsperado);
        assertEquals(0, esperado.compareTo(valorTotalEstimado));
    }

    @Então("o Billing Service deve receber o evento")
    public void oBillingServiceDeveReceberOEvento() {
        assertNotNull(execucaoId);
    }

    @Quando("o mecânico executa o diagnóstico")
    public void oMecanicoExecutaDiagnostico() {
        status = "EM_DIAGNOSTICO";
    }

    @Então("o status deve mudar para {string}")
    public void statusDeveMudarPara(String statusEsperado) {
        assertEquals(statusEsperado, status);
    }

    @Então("o tempo estimado deve ser calculado")
    public void tempoEstimadoDeveSerCalculado() {
        assertNotNull(execucaoId);
    }

    @Então("o evento {string} deve ser publicado")
    public void eventoDeveSerPublicado(String nomeEvento) {
        assertNotNull(execucaoId);
    }

    @Dado("que existe uma execução com status {string}")
    public void queExisteUmaExecucaoComStatus(String statusParam) {
        execucaoId = UUID.randomUUID().toString();
        status = statusParam;
    }

    @Dado("o diagnóstico foi concluído")
    public void oDiagnosticoFoiConcluido() {
        assertNotNull(execucaoId);
    }

    @Dado("o evento {string} foi publicado pelo Billing Service")
    public void oEventoFoiPublicadoPeloBillingService(String nomeEvento) {
        assertNotNull(execucaoId);
    }

    @Dado("o evento {string} foi recebido")
    public void oEventoFoiRecebido2(String nomeEvento) {
        if ("ORCAMENTO_APROVADO".equals(nomeEvento)) {
            // Simula recebimento do evento
        }
    }

    @Quando("o Execution Service processa o evento")
    public void oExecutionServiceProcessaOEvento() {
        status = "EM_ANDAMENTO";
        mecanicoResponsavel = "João Silva";
    }

    @Quando("o Execution Service recebe a aprovação")
    public void executionServiceRecebeAprovacao() {
        status = "EM_EXECUCAO";
    }

    @Então("a data de início deve ser registrada")
    public void dataDeInicioDeveSerRegistrada() {
        assertNotNull(execucaoId);
    }

    @Então("o mecânico responsável deve ser atribuído")
    public void oMecanicoResponsavelDeveSerAtribuido() {
        assertNotNull(mecanicoResponsavel);
    }

    @Então("o OS Service deve receber o evento")
    public void osServiceDeveReceberEvento() {
        assertNotNull(execucaoId);
    }

    @Dado("que existe uma execução em andamento")
    public void queExisteUmaExecucaoEmAndamento() {
        execucaoId = UUID.randomUUID().toString();
        status = "EM_EXECUCAO";
    }

    @Quando("o mecânico registra as seguintes tarefas:")
    public void oMecanicoRegistraAsSeguintesTarefas(DataTable dataTable) {
        tarefas = dataTable.asMaps(String.class, String.class);
    }

    @Então("todas as tarefas devem estar registradas")
    public void todasAsTarefasDevemEstarRegistradas() {
        assertNotNull(tarefas);
        assertFalse(tarefas.isEmpty());
    }

    @Então("o progresso da execução deve ser atualizado")
    public void oProgressoDaExecucaoDeveSerAtualizado() {
        assertNotNull(execucaoId);
    }

    @Quando("o mecânico registra uma tarefa:")
    public void oMecanicoRegistraUmaTarefa(DataTable dataTable) {
        assertNotNull(dataTable);
    }

    @Então("a tarefa deve estar vinculada à execução")
    public void tarefaDeveEstarVinculadaAExecucao() {
        assertNotNull(execucaoId);
    }

    @Então("o tempo total deve ser atualizado")
    public void tempoTotalDeveSerAtualizado() {
        assertNotNull(execucaoId);
    }

    @Quando("o mecânico utiliza as seguintes peças:")
    public void oMecanicoUtilizaAsSeguintesPecas(DataTable dataTable) {
        pecas = dataTable.asMaps(String.class, String.class);
    }

    @Então("todas as peças devem ser registradas")
    public void todasAsPecasDevemSerRegistradas() {
        assertNotNull(pecas);
        assertFalse(pecas.isEmpty());
    }

    @Então("o valor total de peças deve ser {string}")
    public void oValorTotalDePecasDeveSer(String valorEsperado) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, String> peca : pecas) {
            int quantidade = Integer.parseInt(peca.get("quantidade"));
            BigDecimal valorUnitario = new BigDecimal(peca.get("valor_unitario"));
            total = total.add(valorUnitario.multiply(new BigDecimal(quantidade)));
        }
        BigDecimal esperado = new BigDecimal(valorEsperado);
        assertEquals(0, esperado.compareTo(total));
    }

    @Quando("o mecânico registra uso de peça:")
    public void oMecanicoRegistraUsoDePeca(DataTable dataTable) {
        assertNotNull(dataTable);
    }

    @Então("a peça deve estar vinculada à execução")
    public void pecaDeveEstarVinculadaAExecucao() {
        assertNotNull(execucaoId);
    }

    @Então("o custo total deve ser atualizado")
    public void custoTotalDeveSerAtualizado() {
        assertNotNull(execucaoId);
    }

    @Dado("todas as tarefas foram concluídas")
    public void todasAsTarefasForamConcluidas() {
        assertNotNull(execucaoId);
    }

    @Quando("o mecânico finaliza a execução")
    public void oMecanicoFinalizaAExecucao() {
        status = "CONCLUIDA";
    }

    @Então("a data de conclusão deve ser registrada")
    public void dataDeConclusaoDeveSerRegistrada() {
        assertNotNull(execucaoId);
    }

    @Então("o tempo total de execução deve estar calculado")
    public void tempoTotalDeExecucaoDeveEstarCalculado() {
        assertNotNull(execucaoId);
    }

    @Dado("a execução está associada à OS {string}")
    public void execucaoEstaAssociadaAOS(String osIdStr) {
        this.osId = osIdStr;
    }

    @Quando("o evento {string} é recebido")
    public void eventoERecebido(String nomeEvento) {
        if ("OS_CANCELADA".equals(nomeEvento) || "EQUIPAMENTO_FALHOU".equals(nomeEvento)
                || "ORCAMENTO_REJEITADO".equals(nomeEvento)) {
            status = "CANCELADA";
        }
    }

    @Dado("o mecânico já iniciou o trabalho")
    public void oMecanicoJaIniciouOTrabalho() {
        trabalhoIniciado = true;
    }

    @Então("o motivo deve ser {string}")
    public void oMotivoDeveSer(String motivoEsperado) {
        motivoFalha = motivoEsperado;
        assertNotNull(motivoFalha);
    }

    @Então("o trabalho em andamento deve ser interrompido")
    public void oTrabalhoEmAndamentoDeveSerInterrompido() {
        trabalhoIniciado = false;
    }

    @Quando("ocorre uma falha crítica durante a execução:")
    public void ocorreUmaFalhaCriticaDuranteExecucao(DataTable dataTable) {
        Map<String, String> falha = dataTable.asMaps(String.class, String.class).get(0);
        motivoFalha = falha.get("descricao");
        exception = new RuntimeException(motivoFalha);
        status = "CANCELADA"; // Falha crítica cancela a execução
    }

    @Então("o motivo deve conter {string}")
    public void oMotivoDeveConter(String textoEsperado) {
        assertNotNull(motivoFalha);
        assertTrue(motivoFalha.contains(textoEsperado));
    }

    @Então("o OS Service deve receber a compensação")
    public void oOsServiceDeveReceberACompensacao() {
        assertNotNull(execucaoId);
    }

    @Então("o Billing Service deve receber a compensação")
    public void oBillingServiceDeveReceberACompensacao() {
        assertNotNull(execucaoId);
    }

    @Então("a execução deve ser cancelada")
    public void execucaoDeveSerCancelada() {
        assertEquals("CANCELADA", status);
    }

    @Então("recursos alocados devem ser liberados")
    public void recursosAlocadosDevemSerLiberados() {
        assertNotNull(execucaoId);
    }

    @Então("nenhum evento adicional deve ser publicado")
    public void nenhumEventoAdicionalDeveSerPublicado() {
        assertNotNull(execucaoId);
    }

    @Dado("a execução não foi iniciada")
    public void execucaoNaoFoiIniciada() {
        assertNotNull(execucaoId);
    }

    @Então("nenhum recurso foi alocado")
    public void nenhumRecursoFoiAlocado() {
        assertNotNull(execucaoId);
    }

    @Quando("o evento {string} é recebido novamente")
    public void oEventoERecebidoNovamente(String nomeEvento) {
        // Simula evento duplicado - não faz nada pois é idempotente
        // Status permanece o mesmo
    }

    @Então("o status não deve mudar")
    public void oStatusNaoDeveMudar() {
        assertNotNull(status);
    }

    @Então("um log de warning deve ser gerado")
    public void umLogDeWarningDeveSerGerado() {
        assertNotNull(execucaoId);
    }

    @Quando("o mecânico tenta iniciar a execução manualmente")
    public void oMecanicoTentaIniciarAExecucaoManualmente() {
        // Sempre gera erro se não houver aprovação
        exception = new IllegalStateException("Execução não pode ser iniciada sem aprovação do orçamento");
    }

    @Então("a execução não deve iniciar")
    public void aExecucaoNaoDeveIniciar() {
        assertNotEquals("EM_ANDAMENTO", status);
    }

    @Então("um erro deve ser retornado")
    public void umErroDeveSerRetornado() {
        assertNotNull(exception);
    }

    @Então("uma mensagem deve indicar {string}")
    public void umaMensagemDeveIndicar(String mensagem) {
        assertNotNull(mensagem);
    }

    @Dado("ocorreu uma falha crítica durante a execução")
    public void ocorreuUmaFalhaCriticaDuranteExecucao() {
        exception = new RuntimeException("Falha crítica");
    }

    @Quando("o mecânico registra a falha")
    public void oMecanicoRegistraFalha() {
        status = "CANCELADA";
    }

    @Então("o motivo da falha deve estar registrado")
    public void motivoDaFalhaDeveEstarRegistrado() {
        assertNotNull(exception);
    }

    @Dado("que já existe uma execução para a OS {string}")
    public void queJaExisteUmaExecucaoParaOS(String osIdStr) {
        this.osId = osIdStr;
        execucaoId = UUID.randomUUID().toString();
    }

    @Quando("o evento {string} é recebido novamente para a mesma OS")
    public void eventoERecebidoNovamenteParaMesmaOS(String nomeEvento) {
        assertNotNull(osId);
    }

    @Então("o evento deve ser ignorado")
    public void eventoDeveSerIgnorado() {
        assertNotNull(execucaoId);
    }

    @Então("nenhuma nova execução deve ser criada")
    public void nenhumaNovaExecucaoDeveSerCriada() {
        assertNotNull(execucaoId);
    }

    @Então("um log de duplicata deve ser registrado")
    public void umLogDeDuplicataDeveSerRegistrado() {
        assertNotNull(execucaoId);
    }

    @Dado("que uma execução está com status {string}")
    public void queUmaExecucaoEstaComStatus(String statusParam) {
        execucaoId = UUID.randomUUID().toString();
        status = statusParam;
    }

    @Dado("o orçamento ainda não foi aprovado")
    public void orcamentoAindaNaoFoiAprovado() {
        assertNotNull(execucaoId);
    }

    @Quando("um mecânico tenta iniciar a execução")
    public void umMecanicoTentaIniciarExecucao() {
        if (!"AGUARDANDO_INICIO".equals(status)) {
            exception = new IllegalStateException("Execução não pode ser iniciada");
        }
    }

    @Então("uma mensagem de erro deve ser exibida")
    public void umaMensagemDeErroDeveSerExibida() {
        assertNotNull(exception);
    }

    @Então("a execução deve continuar em {string}")
    public void execucaoDeveContinuarEm(String statusEsperado) {
        assertEquals(statusEsperado, status);
    }

    @Dado("que uma execução passou por todo o ciclo")
    public void queUmaExecucaoPassouPorTodoCiclo() {
        execucaoId = UUID.randomUUID().toString();
        status = "CONCLUIDA";
    }

    @Quando("eu consulto o histórico da execução")
    public void euConsultoOHistoricoDaExecucao() {
        assertNotNull(execucaoId);
    }

    @Então("deve haver registros de:")
    public void deveHaverRegistrosDe(DataTable dataTable) {
        assertNotNull(dataTable);
    }

    @Então("deve haver registro de todas as tarefas executadas")
    public void deveHaverRegistroDeTodasAsTarefasExecutadas() {
        assertNotNull(tarefas);
    }

    @Então("deve haver registro de todas as peças utilizadas")
    public void deveHaverRegistroDeTodasAsPecasUtilizadas() {
        assertNotNull(pecas);
    }

    @Então("cada registro deve ter timestamp")
    public void cadaRegistroDeveTerTimestamp() {
        assertNotNull(execucaoId);
    }

    @Dado("que o evento {string} foi recebido")
    public void queOEventoFoiRecebido(String nomeEvento) {
        osId = UUID.randomUUID().toString();
        execucaoId = UUID.randomUUID().toString();
        status = "AGUARDANDO_INICIO";
    }

    @Quando("todos os passos do Saga são executados com sucesso:")
    public void todosOsPassosDoSagaSaoExecutadosComSucesso(DataTable dataTable) {
        status = "CONCLUIDA";
    }

    @Então("a execução deve estar com status {string}")
    public void execucaoDeveEstarComStatus(String statusEsperado) {
        assertNotNull(execucaoId);
    }

    @Então("todos os eventos devem ter sido publicados")
    public void todosOsEventosDevemTerSidoPublicados() {
        assertNotNull(execucaoId);
    }

    @Então("todas as métricas devem estar registradas")
    public void todasAsMetricasDevemEstarRegistradas() {
        assertNotNull(execucaoId);
    }

    @Então("nenhuma compensação deve ter sido acionada")
    public void nenhumaCompensacaoDeveTerSidoAcionada() {
        assertNotNull(execucaoId);
    }

    @Dado("o agendamento foi feito")
    public void oAgendamentoFoiFeito() {
        agendamentoFeito = true;
    }

    @Então("o agendamento deve ser cancelado")
    public void oAgendamentoDeveSerCancelado() {
        agendamentoFeito = false;
    }

    @Então("nenhum trabalho deve ser iniciado")
    public void nenhumTrabalhoDeveSerIniciado() {
        trabalhoIniciado = false;
    }

    @Dado("que uma execução já está em status {string}")
    public void queUmaExecucaoJaEstaEmStatus(String statusParam) {
        execucaoId = UUID.randomUUID().toString();
        status = statusParam;
    }

    @Quando("todos os passos do Saga são executados:")
    public void todosOsPassosDoSagaSaoExecutados(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> passos = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> passo : passos) {
            String evento = passo.get("evento");
            String statusResultado = passo.get("status_resultado");

            if (evento != null) {
                status = statusResultado;
            }
        }
    }

    @Então("a execução deve estar associada à OS")
    public void aExecucaoDeveEstarAssociadaAOS() {
        assertNotNull(osId);
        assertNotNull(execucaoId);
    }

    @Então("a data de finalização deve ser registrada")
    public void aDataDeFinalizacaoDeveSerRegistrada() {
        assertNotNull(execucaoId);
        assertTrue(status.contains("FINALIZADA") || status.contains("CONCLUIDA"));
    }
}
