# ADR-003: Distributed Tracing com New Relic APM

## Status
**Aceito** - Janeiro 2026

## Contexto
Em uma arquitetura de microserviços com comunicação assíncrona via SQS, precisamos rastrear requisições que atravessam múltiplos serviços. Desafios:
- Correlacionar logs de diferentes serviços
- Identificar gargalos de performance entre serviços
- Debugar falhas em fluxos distribuídos
- Medir latência end-to-end

## Decisão
Implementar **Distributed Tracing com New Relic APM** em todos os microserviços, utilizando o Java Agent com suporte a:
- Trace IDs propagados entre serviços
- Span IDs para cada operação
- Automatic instrumentation de Spring Boot, JDBC, HTTP
- Custom instrumentation para SQS messages

### Implementação

Cada microserviço possui:
```yaml
# newrelic.yml
common: &default_settings
  distributed_tracing:
    enabled: true
  transaction_tracer:
    enabled: true
    record_sql: obfuscated
  application_logging:
    enabled: true
    forwarding:
      enabled: true
```

## Consequências

### Positivas ✅
- **Visibilidade Completa**: Rastreamento de requisições entre OS → Billing → Execution
- **Root Cause Analysis**: Identificar rapidamente onde falhas ocorrem
- **Performance Insights**: Detectar operações lentas (database queries, external calls)
- **SLA Monitoring**: Medir latência P50, P95, P99 por endpoint
- **Context Propagation**: Trace IDs automaticamente propagados via HTTP headers e SQS attributes

### Negativas ❌
- **Overhead**: ~2-5% de latência adicional
- **Custo**: Licenças New Relic por host/container
- **Complexidade**: Configuração e troubleshooting do agent
- **Data Retention**: Traces são retidos por período limitado

### Riscos e Mitigações

| Risco | Mitigação |
|-------|-----------|
| Agent crash causando app downtime | Health checks independentes do agent |
| Alto volume de dados enviados | Sampling de 10% em produção se necessário |
| Latência aumentada | Assíncrono buffering, thread pool dedicado |
| Falha de conectividade com New Relic | Agent continua funcionando, buffer local |

## Arquitetura de Tracing

### Propagação de Trace ID

```
Request inicial
  ├─ Trace-ID: abc-123-def-456
  └─ Span-ID: 001
     │
     ├─ OS Service (Span-ID: 002)
     │  ├─ Database Query (Span-ID: 003)
     │  └─ SQS Publish (Span-ID: 004, Trace-ID: abc-123-def-456)
     │
     ├─ Billing Service (Span-ID: 005, Trace-ID: abc-123-def-456)
     │  ├─ MongoDB Query (Span-ID: 006)
     │  └─ SQS Publish (Span-ID: 007, Trace-ID: abc-123-def-456)
     │
     └─ Execution Service (Span-ID: 008, Trace-ID: abc-123-def-456)
        └─ Database Query (Span-ID: 009)
```

### Exemplo de Trace Visualizado

```
┌─────────────────────────────────────────────────────────────────┐
│ POST /api/ordens (Total: 1,250ms)                               │
├─────────────────────────────────────────────────────────────────┤
│ ├─ OS Service (850ms)                                           │
│ │  ├─ Controller.criarOrdem (50ms)                              │
│ │  ├─ PostgreSQL INSERT ordens_servico (150ms) ⚠️               │
│ │  ├─ SQS Publish os-events-queue (100ms)                       │
│ │  └─ Service.validarCliente (550ms) ⚠️ SLOW                    │
│ │                                                                │
│ ├─ Billing Service (250ms) [Async]                              │
│ │  ├─ SQS Receive message (50ms)                                │
│ │  └─ MongoDB INSERT orcamento (200ms)                          │
│ │                                                                │
│ └─ Execution Service (150ms) [Async]                            │
│    ├─ SQS Receive message (50ms)                                │
│    └─ PostgreSQL INSERT execucao (100ms)                        │
└─────────────────────────────────────────────────────────────────┘
```

## Instrumentação Automática

New Relic Agent instrumenta automaticamente:

### Framework Spring Boot
```java
@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {
    
    @PostMapping  // ✅ Automaticamente instrumentado
    public ResponseEntity<OrdemServicoDTO> criarOrdem(@RequestBody OrdemServicoRequestDTO request) {
        // New Relic captura:
        // - URL: POST /api/ordens
        // - Response time
        // - Status code
        // - Request/response size
    }
}
```

### JDBC / PostgreSQL
```java
@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, UUID> {
    // ✅ Automaticamente instrumentado
    // New Relic captura:
    // - SQL query (obfuscated)
    // - Execution time
    // - Rows affected
    // - Database name
}
```

### HTTP Client (RestTemplate / WebClient)
```java
@Service
public class ExternalApiService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public void callExternalService() {
        // ✅ Automaticamente instrumentado
        // New Relic captura:
        // - External URL
        // - Request duration
        // - Response status
        restTemplate.getForEntity("https://api.externa.com/data", String.class);
    }
}
```

## Instrumentação Customizada

### SQS Message Tracing

Para propagar Trace ID via SQS:

```java
@Service
public class SQSEventPublisher {
    
    @Autowired
    private SqsTemplate sqsTemplate;
    
    @Trace(dispatcher = true)  // Custom instrumentation
    public void publishEvent(OSCriadaEvent event) {
        // Adiciona Trace ID aos message attributes
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        
        String traceId = NewRelic.getAgent()
            .getTransaction()
            .getTraceId();
        
        attributes.put("newrelic-trace-id", MessageAttributeValue.builder()
            .dataType("String")
            .stringValue(traceId)
            .build());
        
        sqsTemplate.send(SendMessageRequest.builder()
            .queueUrl("os-events-queue")
            .messageBody(objectMapper.writeValueAsString(event))
            .messageAttributes(attributes)
            .build());
    }
}

@Component
public class SQSEventConsumer {
    
    @SqsListener("os-events-queue")
    public void handleEvent(Message message) {
        // Extrai Trace ID dos message attributes
        String traceId = message.messageAttributes()
            .get("newrelic-trace-id")
            .stringValue();
        
        // Continua o trace
        NewRelic.getAgent()
            .getTransaction()
            .acceptDistributedTraceHeaders(
                TransportType.Other,
                Map.of("newrelic-trace-id", traceId)
            );
        
        // Processa mensagem
        processEvent(message.body());
    }
}
```

### Custom Transactions

```java
@Service
public class OrdemServicoService {
    
    @Trace(metricName = "Custom/OrdemServico/validate")
    public void validarOrdem(OrdemServico os) {
        // Este método aparecerá como span separado no trace
        NewRelic.addCustomParameter("ordem_id", os.getId().toString());
        NewRelic.addCustomParameter("status", os.getStatus().name());
        
        // Lógica de validação
    }
}
```

## Dashboards e Alertas

### Dashboards Criados

1. **Microservices Overview**
   - Throughput por serviço
   - Latência P95 por serviço
   - Taxa de erro por serviço

2. **Distributed Traces**
   - Top 10 traces mais lentos
   - Traces com erros
   - Breakdown por serviço

3. **SQS Monitoring**
   - Mensagens publicadas/consumidas
   - Latência de processamento
   - Dead Letter Queue size

### Alertas Configurados

| Alerta | Condição | Severidade |
|--------|----------|------------|
| High Latency | P95 > 3 segundos por 5 minutos | Critical |
| Error Rate Spike | Taxa de erro > 5% | Critical |
| Service Down | Apdex score < 0.5 | Critical |
| SQS DLQ Growing | DLQ messages > 10 | Warning |
| Database Slow Queries | Query time > 2 segundos | Warning |

## Exemplos de Queries NRQL

### Latência por Microserviço
```sql
SELECT average(duration) 
FROM Transaction 
WHERE appName IN ('OS Service', 'Billing Service', 'Execution Service')
FACET appName 
SINCE 1 hour ago 
TIMESERIES
```

### Traces com Erros
```sql
SELECT count(*) 
FROM Span 
WHERE error.message IS NOT NULL 
FACET service.name, error.message 
SINCE 1 day ago
```

### Top Endpoints Lentos
```sql
SELECT percentile(duration, 95) 
FROM Transaction 
WHERE transactionType = 'Web' 
FACET name 
SINCE 1 hour ago 
LIMIT 10
```

## Alternativas Consideradas

### 1. Jaeger (Open Source)
- **Prós**: Open source, sem custo de licença, compatível com OpenTelemetry
- **Contras**: Requer infraestrutura própria, menos features de APM
- **Motivo da rejeição**: Já utilizamos New Relic, infraestrutura adicional

### 2. AWS X-Ray
- **Prós**: Integração nativa com AWS, sem agent
- **Contras**: Vendor lock-in, menos features de APM que New Relic
- **Motivo da rejeição**: New Relic oferece mais visibilidade

### 3. Zipkin
- **Prós**: Open source, leve
- **Contras**: Menos maduro que Jaeger, UI básica
- **Motivo da rejeição**: Features limitadas

## Troubleshooting

### Agent não está reportando dados

```bash
# Verificar logs do New Relic Agent
kubectl logs -n os-service <pod-name> | grep newrelic

# Verificar se license key está configurada
kubectl get secret os-service-secrets -n os-service -o jsonpath='{.data.NEW_RELIC_LICENSE_KEY}' | base64 -d

# Verificar variáveis de ambiente no pod
kubectl exec -n os-service <pod-name> -- env | grep NEW_RELIC
```

### Traces incompletos

- Verificar se Trace ID está sendo propagado via SQS attributes
- Confirmar que `distributed_tracing.enabled=true` em todos os serviços
- Verificar logs para erros de instrumentação

## Referências
- [New Relic Java Agent](https://docs.newrelic.com/docs/apm/agents/java-agent/)
- [Distributed Tracing](https://docs.newrelic.com/docs/distributed-tracing/concepts/introduction-distributed-tracing/)
- [Custom Instrumentation](https://docs.newrelic.com/docs/apm/agents/java-agent/custom-instrumentation/java-custom-instrumentation/)
- [OpenTelemetry](https://opentelemetry.io/)

## Revisão
Esta decisão será revisada em **Julho 2026** ou quando OpenTelemetry atingir maturidade suficiente para migração.

---

**Autor**: Grupo 99  
**Data**: Janeiro 2026  
**Revisores**: Equipe de Arquitetura e SRE
