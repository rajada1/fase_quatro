# language: pt
Funcionalidade: Saga Pattern - Execução e Diagnóstico
  Como Execution Service
  Eu quero gerenciar execuções e diagnósticos
  Para coordenar o trabalho operacional no fluxo Saga

  Contexto:
    Dado que o Execution Service está disponível
    E a fila "execution-events-queue" está configurada

  Cenário: Saga Step 1 - Receber OS Criada e Registrar Execução
    Dado que o evento "OS_CRIADA" foi publicado
    E contém os dados:
      | osId          | 550e8400-e29b-41d4-a716-446655440000 |
      | clienteNome   | Maria Santos                          |
      | veiculoPlaca  | XYZ-5678                              |
      | descricao     | Troca de óleo e filtros               |
    Quando o Execution Service recebe o evento
    Então um registro de execução deve ser criado
    E o status da execução deve ser "AGUARDANDO_INICIO"
    E a execução deve estar associada à OS

  Cenário: Saga Step 2 - Realizar Diagnóstico
    Dado que existe uma execução para a OS "550e8400-e29b-41d4-a716-446655440000"
    Quando o mecânico realiza o diagnóstico
    E identifica os seguintes problemas:
      | problema                | valor_estimado |
      | Troca de óleo           | 300.00         |
      | Troca de filtro de ar   | 150.00         |
      | Troca de filtro de óleo | 100.00         |
    Então o diagnóstico deve ser registrado
    E o valor total estimado deve ser "550.00"
    E o evento "DIAGNOSTICO_CONCLUIDO" deve ser publicado
    E o Billing Service deve receber o evento

  Cenário: Saga Step 3 - Iniciar Execução Após Aprovação
    Dado que existe uma execução com status "AGUARDANDO_INICIO"
    E o evento "ORCAMENTO_APROVADO" foi recebido
    Quando o Execution Service processa o evento
    Então o status deve mudar para "EM_ANDAMENTO"
    E a data de início deve ser registrada
    E o mecânico responsável deve ser atribuído

  Cenário: Saga Step 4 - Registrar Tarefas Durante Execução
    Dado que existe uma execução com status "EM_ANDAMENTO"
    Quando o mecânico registra as seguintes tarefas:
      | tarefa                  | tempo_minutos | completa |
      | Drenar óleo usado       | 15            | true     |
      | Trocar filtro de óleo   | 10            | true     |
      | Adicionar óleo novo     | 10            | true     |
    Então todas as tarefas devem estar registradas
    E o progresso da execução deve ser atualizado

  Cenário: Saga Step 5 - Registrar Uso de Peças
    Dado que existe uma execução em andamento
    Quando o mecânico utiliza as seguintes peças:
      | peca                | quantidade | valor_unitario |
      | Óleo 5W30          | 4          | 75.00          |
      | Filtro de óleo     | 1          | 100.00         |
      | Filtro de ar       | 1          | 150.00         |
    Então todas as peças devem ser registradas
    E o valor total de peças deve ser "550.00"

  Cenário: Saga Step 6 - Finalizar Execução
    Dado que existe uma execução em andamento
    E todas as tarefas foram concluídas
    Quando o mecânico finaliza a execução
    Então o status deve mudar para "CONCLUIDA"
    E a data de finalização deve ser registrada
    E o evento "EXECUCAO_FINALIZADA" deve ser publicado
    E o OS Service deve receber o evento
    E o Billing Service deve receber o evento

  Cenário: Compensação - Orçamento Rejeitado
    Dado que existe uma execução com status "AGUARDANDO_INICIO"
    E o agendamento foi feito
    Quando o evento "ORCAMENTO_REJEITADO" é recebido
    Então o status deve mudar para "CANCELADA"
    E o agendamento deve ser cancelado
    E nenhum trabalho deve ser iniciado

  Cenário: Compensação - OS Cancelada
    Dado que existe uma execução com status "EM_ANDAMENTO"
    E o mecânico já iniciou o trabalho
    Quando o evento "OS_CANCELADA" é recebido
    Então o status deve mudar para "CANCELADA"
    E o evento "EXECUCAO_FALHOU" deve ser publicado
    E o motivo deve ser "OS foi cancelada"
    E o trabalho em andamento deve ser interrompido

  Cenário: Compensação - Falha Durante Execução
    Dado que existe uma execução em andamento
    Quando ocorre uma falha crítica durante a execução:
      | tipo_falha        | descricao                          |
      | EQUIPAMENTO       | Equipamento de diagnóstico falhou  |
    Então o status deve mudar para "CANCELADA"
    E o evento "EXECUCAO_FALHOU" deve ser publicado
    E o motivo deve conter "Equipamento de diagnóstico falhou"
    E o OS Service deve receber a compensação
    E o Billing Service deve receber a compensação

  Cenário: Idempotência - Evento Duplicado OS_CRIADA
    Dado que já existe uma execução para a OS "550e8400-e29b-41d4-a716-446655440000"
    Quando o evento "OS_CRIADA" é recebido novamente
    Então o evento deve ser ignorado
    E nenhuma nova execução deve ser criada
    E um log de duplicata deve ser registrado

  Cenário: Idempotência - Evento Duplicado ORCAMENTO_APROVADO
    Dado que uma execução já está em status "EM_ANDAMENTO"
    Quando o evento "ORCAMENTO_APROVADO" é recebido novamente
    Então o evento deve ser ignorado
    E o status não deve mudar
    E um log de warning deve ser gerado

  Cenário: Validação - Tentativa de Iniciar Execução sem Aprovação
    Dado que existe uma execução com status "AGUARDANDO_INICIO"
    E o orçamento ainda não foi aprovado
    Quando o mecânico tenta iniciar a execução manualmente
    Então a execução não deve iniciar
    E um erro deve ser retornado
    E uma mensagem deve indicar "Aguardando aprovação do orçamento"

  Cenário: Histórico - Rastreamento Completo da Execução
    Dado que uma execução passou por todo o ciclo
    Quando eu consulto o histórico da execução
    Então deve haver registros de:
      | status_anterior     | status_novo      | evento_causador         |
      | null                | AGUARDANDO_INICIO | OS_CRIADA              |
      | AGUARDANDO_INICIO   | EM_ANDAMENTO     | ORCAMENTO_APROVADO     |
      | EM_ANDAMENTO        | CONCLUIDA        | Finalização manual     |
    E cada registro deve ter timestamp
    E deve haver registro de todas as tarefas executadas
    E deve haver registro de todas as peças utilizadas

  Cenário: Saga Completo - Happy Path do Execution
    Dado que o evento "OS_CRIADA" foi recebido
    Quando todos os passos do Saga são executados:
      | passo | ação                      | status_resultado  |
      | 1     | Receber OS_CRIADA         | AGUARDANDO_INICIO |
      | 2     | Realizar diagnóstico      | AGUARDANDO_INICIO |
      | 3     | Receber ORCAMENTO_APROVADO | EM_ANDAMENTO     |
      | 4     | Executar tarefas          | EM_ANDAMENTO      |
      | 5     | Finalizar execução        | CONCLUIDA         |
    Então a execução deve estar com status "CONCLUIDA"
    E todos os eventos devem ter sido publicados
    E nenhuma compensação deve ter sido acionada
    E todas as métricas devem estar registradas
