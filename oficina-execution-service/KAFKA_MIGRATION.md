# Migração SQS → Kafka - Execution Service

## Visão Geral

Este documento descreve a migração do `oficina-execution-service` de AWS SQS para Apache Kafka como parte do padrão Saga Coreografada.

## Mudanças Realizadas

### 1. Novas Classes Kafka

#### `KafkaConfig.java`
- Configuração centralizada de Consumer/Producer
- Definição de tópicos: `os-events`, `billing-events`, `execution-events`
- Configuração de JSON serialization/deserialization

#### `ExecutionEventPublisherPort.java`
- Interface de abstração para publicação de eventos
- Permite trocar implementação SQS ↔ Kafka facilmente

#### `KafkaExecutionEventPublisher.java` (@Primary)
- Implementação Kafka do publisher de eventos
- Publicação síncrona para eventos críticos
- Headers para roteamento de eventos

#### `KafkaExecutionEventListener.java`
- Consumidor de eventos dos tópicos `os-events` e `billing-events`
- Manual acknowledgment para garantia de processamento
- Handlers para eventos da Saga:
  - `OS_CRIADA` → Criar registro de execução
  - `ORCAMENTO_APROVADO` → Iniciar execução
  - `ORCAMENTO_REJEITADO` → Cancelar execução (compensação)
  - `OS_CANCELADA` → Cancelar execução (compensação)

### 2. Eventos de Domínio Atualizados

Adicionado `@Builder` aos eventos:
- `DiagnosticoConcluidoEvent` - diagnostico, valorEstimado
- `ExecucaoConcluidaEvent` - mecanico, observacoes
- `ExecucaoFalhouEvent` - requerRetrabalho

### 3. Configurações

#### `application.yml`
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: execution-service-group
      auto-offset-reset: earliest
      enable-auto-commit: false
    producer:
      acks: all
      retries: 3
      properties:
        enable.idempotence: true
    listener:
      ack-mode: manual
      concurrency: 3
```

#### `docker-compose.yml`
- Zookeeper (2181)
- Kafka (9092)
- Kafka UI (8280)
- kafka-init (criação de tópicos)

### 4. Dependências (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <version>4.2.0</version>
    <scope>test</scope>
</dependency>
```

## Fluxo da Saga Coreografada

```
┌─────────────┐     os-events        ┌───────────────────┐
│  os-service │ ────────────────────→│ execution-service │
│             │     OS_CRIADA        │                   │
└─────────────┘                      └───────────────────┘
                                              │
                                              │ execution-events
                                              │ DIAGNOSTICO_CONCLUIDO
                                              ▼
┌─────────────────┐  billing-events  ┌───────────────────┐
│ billing-service │ ────────────────→│ execution-service │
│                 │ ORCAMENTO_APROVADO│                   │
└─────────────────┘                  └───────────────────┘
                                              │
                                              │ execution-events
                                              │ EXECUCAO_CONCLUIDA
                                              ▼
                                     ┌───────────────────┐
                                     │    os-service     │
                                     │  (finaliza OS)    │
                                     └───────────────────┘
```

## Eventos Produzidos

| Evento | Descrição |
|--------|-----------|
| DIAGNOSTICO_CONCLUIDO | Diagnóstico realizado, envia para billing calcular orçamento |
| EXECUCAO_CONCLUIDA | Serviço finalizado com sucesso |
| EXECUCAO_FALHOU | Compensação - execução falhou |

## Eventos Consumidos

| Evento | Origem | Ação |
|--------|--------|------|
| OS_CRIADA | os-service | Criar registro de execução |
| OS_CANCELADA | os-service | Cancelar execução |
| ORCAMENTO_APROVADO | billing-service | Iniciar execução |
| ORCAMENTO_REJEITADO | billing-service | Cancelar execução |

## Execução

### Iniciar Infraestrutura
```bash
docker-compose up -d
```

### Verificar Kafka UI
Acesse: http://localhost:8280

### Executar Aplicação
```bash
mvn spring-boot:run
```

## Compatibilidade

- SQS mantido com profile `sqs-legacy` para rollback
- `ExecutionEventPublisher` (SQS) permanece disponível
- `KafkaExecutionEventPublisher` é `@Primary`

## Próximos Passos

1. ✅ Migrar os-service (concluído)
2. ✅ Migrar billing-service (concluído)
3. ✅ Migrar execution-service (concluído)
4. ⏳ Configurar Dead Letter Topics
5. ⏳ Implementar Circuit Breaker
6. ⏳ Adicionar métricas Kafka ao New Relic
