# ADR-001: Comunicação entre Microserviços via Event-Driven Architecture

## Status
**Aceito** - Janeiro 2026

## Contexto
Na arquitetura de microserviços, precisamos definir como os serviços (OS, Billing e Execution) irão se comunicar. As opções consideradas foram:
1. Comunicação síncrona REST entre todos os serviços
2. Event-Driven Architecture com mensageria assíncrona
3. Híbrido: REST para consultas + Event-Driven para notificações

## Decisão
Adotamos **Event-Driven Architecture utilizando Amazon SQS** como barramento de eventos, combinado com REST APIs para consultas diretas quando necessário.

### Implementação
- **Amazon SQS** como message broker
- **3 filas principais**:
  - `os-events-queue` - Eventos do OS Service
  - `billing-events-queue` - Eventos do Billing Service
  - `execution-events-queue` - Eventos do Execution Service
- **Dead Letter Queues (DLQ)** para mensagens com falha
- **Long Polling** para reduzir latência
- **REST APIs** para consultas síncronas

## Consequências

### Positivas ✅
- **Desacoplamento**: Microserviços não precisam conhecer URLs uns dos outros
- **Resiliência**: Se um serviço estiver down, mensagens ficam na fila
- **Escalabilidade**: Cada serviço consome mensagens no seu próprio ritmo
- **Auditoria**: Todas as mensagens são logadas e podem ser reprocessadas
- **Retry Automático**: SQS reentrega mensagens em caso de falha

### Negativas ❌
- **Complexidade**: Gerenciar filas e consumidores
- **Eventual Consistency**: Dados não são sincronizados imediatamente
- **Debugging**: Mais difícil rastrear fluxos (mitigado com New Relic Distributed Tracing)
- **Custo**: Cobrança por número de requisições SQS

### Riscos e Mitigações

| Risco | Mitigação |
|-------|-----------|
| Mensagens duplicadas | Idempotência nos consumers |
| Mensagens fora de ordem | Timestamps e versionamento |
| DLQ acumulando mensagens | Alertas no New Relic quando DLQ > 10 msgs |
| Latência elevada | Timeout de 30s + Long Polling de 20s |

## Alternativas Consideradas

### 1. REST Síncrono Puro
- **Prós**: Simples, resposta imediata
- **Contras**: Alto acoplamento, falha em cascata, difícil escalabilidade
- **Motivo da rejeição**: Não atende requisitos de resiliência

### 2. Apache Kafka
- **Prós**: Alta throughput, retenção longa, replay de eventos
- **Contras**: Complexidade operacional, custo de infraestrutura, overkill para volume atual
- **Motivo da rejeição**: Complexidade desnecessária para o volume de eventos

### 3. AWS EventBridge
- **Prós**: Roteamento de eventos, integração com serviços AWS
- **Contras**: Latência maior que SQS, custo mais elevado
- **Motivo da rejeição**: SQS é suficiente e mais econômico

## Implementação

### Exemplo de Publicação (OS Service)

```java
@Service
public class EventPublisher {
    
    @Autowired
    private SqsTemplate sqsTemplate;
    
    public void publishOSCriada(OrdemServico os) {
        OSCriadaEvent event = new OSCriadaEvent(
            os.getId(),
            os.getClienteId(),
            os.getVeiculoId(),
            LocalDateTime.now()
        );
        
        sqsTemplate.send("os-events-queue", event);
    }
}
```

### Exemplo de Consumo (Billing Service)

```java
@Component
public class OSEventListener {
    
    @SqsListener("os-events-queue")
    public void handleOSCriada(OSCriadaEvent event) {
        logger.info("OS criada recebida: {}", event.getOsId());
        
        // Criar orçamento vazio para a OS
        Orcamento orcamento = new Orcamento();
        orcamento.setOsId(event.getOsId());
        orcamento.setStatus(StatusOrcamento.AGUARDANDO_DIAGNOSTICO);
        
        orcamentoRepository.save(orcamento);
    }
}
```

## Referências
- [AWS SQS Best Practices](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-best-practices.html)
- [Event-Driven Architecture Pattern](https://martinfowler.com/articles/201701-event-driven.html)
- [Spring Cloud AWS SQS](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html#sqs-integration)

## Revisão
Esta decisão será revisada em **Julho 2026** ou se o volume de eventos ultrapassar **10.000 mensagens/dia**.

---

**Autor**: Grupo 99  
**Data**: Janeiro 2026  
**Revisores**: Equipe de Arquitetura
