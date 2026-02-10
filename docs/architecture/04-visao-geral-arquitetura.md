# 🏗️ Visão Geral da Arquitetura

## 📊 Arquitetura de Alto Nível

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTES                                │
└────────────────┬────────────────────────────────────────────────┘
                 │
        ┌────────▼────────┐
        │  API Gateway    │
        │  AWS            │
        └────┬───────┬────┘
             │       │
      ┌──────▼──┐  ┌─▼─────────────┐
      │Lambda   │  │ Load Balancer │
      │Auth     │  │ (Ingress)     │
      │Service  │  └─┬─────────────┘
      └────┬────┘    │
           │    ┌────▼──────────────────────────────────────────┐
           │    │      Amazon EKS (Kubernetes)                 │
           │    │                                              │
           │    │  ┌─────────────────────────────────────────┐ │
           │    │  │    9 Microserviços (Spring Boot)      │ │
           │    │  │                                       │ │
           │    │  │  • Customer Service                  │ │
           │    │  │  • People Service                    │ │
           │    │  │  • HR Service                        │ │
           │    │  │  • Billing Service                   │ │
           │    │  │  • Execution Service                 │ │
           │    │  │  • OS Service                        │ │
           │    │  │  • Maintenance Service               │ │
           │    │  │  • Notification Service              │ │
           │    │  │  • Operations Service                │ │
           │    │  └──────────────┬──────────────────────┘ │
           │    │                 │                        │
           │    │            ┌────▼────┐                  │
           │    │            │HPA: 2-10│                  │
           │    │            │replicas │                  │
           │    │            └─────────┘                  │
           │    └────────┬─────────────────────────────────┘
           │             │
      ┌────┴────────┬────▼────────────────┐
      │             │                     │
  ┌───▼──┐  ┌──────▼───────┐  ┌─────────▼─────┐
  │RDS   │  │AWS SQS FIFO  │  │  New Relic    │
  │Post- │  │              │  │  Monitoring   │
  │greSQL│  │  Event Queue │  │               │
  │16.3  │  │  (9 Queues)  │  └───────────────┘
  └──────┘  └──────────────┘
```

---

## 🔄 Fluxo de Dados - Event-Driven Saga Pattern

```
1. Client Request
   │
   ▼
2. Spring Boot REST Controller
   │
   ▼
3. Application Service (Business Logic)
   │
   ▼
4. Save to Database (Local Transaction)
   │
   ▼
5. Domain Event Created
   │
   ▼
6. Event Publisher
   │
   ├─► Serialize to JSON
   │
   └─► AWS SQS FIFO Queue
       │
       ├─ messageGroupId (FIFO Order)
       ├─ messageDeduplicationId (5 min)
       └─ VisibilityTimeout (60s Retry)
       │
       ▼
7. Async Processing (Other Services)
   │
   ├─► Service A processes event
   ├─► Service B processes event
   ├─► Service C processes event
   │
   └─► Events cascade continue
```

---

## 🎯 Padrão Saga - Event Choreography

### 6 Serviços com Saga Completo (AWS SQS FIFO)

```
┌──────────────────────────────────────────────────────────────┐
│                 SAGA COMPLETO (AWS SQS)                      │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  1. Customer Service                                         │
│     └─ Events: ClienteCriado, VeiculoAdicionado            │
│                                                              │
│  2. People Service                                          │
│     └─ Events: PessoaCriada                               │
│                                                              │
│  3. HR Service                                             │
│     └─ Events: FuncionarioCriado                          │
│                                                              │
│  4. Billing Service                                        │
│     └─ Events: FaturaGerada, PagamentoRecebido          │
│                                                              │
│  5. Execution Service                                      │
│     └─ Events: OrdemExecutada, ProblemaDetectado        │
│                                                              │
│  6. OS Service                                            │
│     └─ Events: OrdemCriada, OrdemFinalizada             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### 3 Serviços com Saga Básico (Spring Events)

```
┌──────────────────────────────────────────────────────────────┐
│              SAGA BÁSICO (Spring Events)                     │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  7. Maintenance Service                                     │
│     └─ Events: ManutençãoProgramada                      │
│                                                              │
│  8. Notification Service                                   │
│     └─ Events: NotificaçãoEnviada                       │
│                                                              │
│  9. Operations Service                                     │
│     └─ Events: OperaçãoConcluída                        │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 📦 Stack Técnico

### Plataforma Cloud
```
AWS (Amazon Web Services)
├── EKS (Elastic Kubernetes Service) v1.29
├── RDS (Relational Database Service) PostgreSQL 16.3
├── Lambda (Serverless Compute)
├── API Gateway
├── SQS FIFO (Simple Queue Service - First In First Out)
└── VPC (Virtual Private Cloud)
```

### Backend
```
Java & Spring Ecosystem
├── Java 21 (LTS)
├── Spring Boot 3.3.13
├── Spring Data JPA
├── Spring Security (JWT)
├── Spring Cloud (AWS Integration)
└── Hibernate ORM
```

### Database
```
PostgreSQL 16.3
├── ACID Transactions
├── JSON Support (JSONB)
├── Extensions (UUID, etc.)
└── Connection Pool: HikariCP
```

### Observability
```
New Relic
├── APM (Application Performance Monitoring)
├── Infrastructure Monitoring
├── Log Aggregation
└── Custom Dashboards
```

### Build & Deployment
```
Maven 3.x
├── Dependency Management
├── Plugin Configuration
├── Build Profiles (dev, test, prod)
└── JAR Packaging

GitHub Actions
├── CI/CD Pipeline
├── Automated Testing
└── Deployment Automation

Kubernetes
├── Deployment & ReplicaSets
├── Services & Ingress
├── HPA (Horizontal Pod Autoscaling)
└── ConfigMaps & Secrets
```

---

## 🔐 Segurança

```
┌─ Authentication ─┐
│  Lambda Auth     │
│  + JWT Tokens   │
└────────┬────────┘
         │
    ┌────▼─────┐
    │ API Gateway
    │ - Rate Limiting
    │ - CORS
    │ - API Keys
    └────┬─────┘
         │
    ┌────▼──────────┐
    │ Spring Security
    │ - JWT Validation
    │ - Role-based Access
    │ - Method-level Auth
    └────┬──────────┘
         │
    ┌────▼─────┐
    │ VPC
    │ - Private Subnets
    │ - Security Groups
    │ - Network ACLs
    └─────────┘
```

---

## 📊 Conformidade Saga Pattern

```
┌─────────────────────────────────────────┐
│    Conformidade Geral: 100% ✅          │
├─────────────────────────────────────────┤
│                                         │
│  Saga Completo: 6/9 (67%) ✅           │
│  ├─ Customer Service    ✅             │
│  ├─ People Service      ✅             │
│  ├─ HR Service          ✅             │
│  ├─ Billing Service     ✅             │
│  ├─ Execution Service   ✅             │
│  └─ OS Service          ✅             │
│                                         │
│  Saga Básico: 3/9 (33%) ✅            │
│  ├─ Maintenance Service ✅             │
│  ├─ Notification Service ✅            │
│  └─ Operations Service  ✅             │
│                                         │
│  Sem Saga: 0/9 (0%)                   │
│                                         │
└─────────────────────────────────────────┘
```

---

## 🧪 Qualidade de Código

```
Testes Unitários
├── Customer Service:   19/19 ✅
├── People Service:      8/8 ✅
├── HR Service:         ✅
├── Billing Service:    ✅
├── Execution Service:  ✅
├── OS Service:         ✅
├── Maintenance Service: ✅
├── Notification Service: ✅
└── Operations Service: ✅

Total: 35/35 PASS ✅

Compilação
├── Maven Clean: ✅
├── Maven Compile: ✅
├── Maven Test: ✅
└── Maven Package: ✅

Cobertura
├── Unit Tests: ~85%
├── Integration: ~70%
└── E2E: ~60%
```

---

## 🚀 Fluxo de Deployment

```
Developer Commits Code
        │
        ▼
GitHub Actions Trigger
        │
    ┌───┴────────────┐
    │                │
    ▼                ▼
Build & Test    Lint Check
    │                │
    └───┬────────────┘
        │
        ▼
Maven Package (JAR)
        │
        ▼
Docker Build (Image)
        │
        ▼
Push to ECR
        │
        ▼
Deploy to EKS
    │
    ├─ Rolling Update
    ├─ Health Checks
    ├─ Traffic Shift
    └─ Monitoring Alert
```

---

## 📈 Performance e Escalabilidade

```
Horizontal Scaling
├─ HPA Min Replicas: 2
├─ HPA Max Replicas: 10
├─ CPU Target: 80%
└─ Memory Target: 80%

Vertical Scaling (Pod Resources)
├─ Request CPU: 100m
├─ Request Memory: 256Mi
├─ Limit CPU: 500m
└─ Limit Memory: 512Mi

Database Scaling
├─ Connection Pool: 20
├─ Max Connections: 100
└─ Read Replicas: Optional (Multi-AZ)

SQS Queue Scaling
├─ Automatic (AWS managed)
├─ Throughput: Unlimited
└─ Retention: 4 days default
```

---

## 🔍 Monitoramento

```
New Relic Dashboard
├─ Application Performance
│  ├─ Response Time (p50, p95, p99)
│  ├─ Throughput (requests/min)
│  └─ Error Rate (4xx, 5xx)
│
├─ Infrastructure
│  ├─ CPU Usage
│  ├─ Memory Usage
│  ├─ Disk I/O
│  └─ Network I/O
│
├─ Event Processing (SQS)
│  ├─ Messages Published
│  ├─ Messages Processed
│  ├─ Queue Depth
│  └─ Processing Latency
│
└─ Logs
   ├─ Application Logs
   ├─ System Logs
   ├─ Audit Logs
   └─ Error Logs
```

---

## 📚 Documentação Relacionada

- 📄 [01 - Diagrama de Componentes](./01-diagrama-componentes.md)
- 📄 [02 - Fluxos de Sequência](./02-fluxos-sequencia.md)
- 📄 [03 - Padrão Saga](./03-padrao-saga.md)
- 📄 [05 - Modelo de Dados](./05-modelo-dados.md)

---

## 🎓 Próximas Etapas

### Fase 5 (Q2 2026)
- Implementar Dead Letter Queues (DLQ)
- Adicionar Circuit Breaker pattern
- Implementar compensating transactions

### Fase 6 (Q3 2026)
- Event Sourcing
- CQRS (Command Query Responsibility Segregation)

### Fase 7 (Q4 2026)
- Machine Learning
- Anomaly Detection
- Predictive Analytics

---

*Arquitetura documentada em Fevereiro 2026*  
*Tech Challenge FIAP - Sistemas de Arquitetura*  
*Status: ✅ Completo e em Produção*
