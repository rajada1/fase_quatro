# 🚪 Oficina Gateway - API Gateway

API Gateway central do Sistema de Gestão de Oficina Mecânica, responsável por rotear todas as requisições para os microserviços apropriados.

## 📋 Visão Geral

O **oficina-gateway** é o ponto de entrada único da aplicação, implementado com **Spring Cloud Gateway**. Ele fornece:

- ✅ **Roteamento** para todos os 10 microserviços
- ✅ **Autenticação JWT** centralizada
- ✅ **Circuit Breaker** (Resilience4j) com fallback por serviço
- ✅ **CORS** configurado
- ✅ **Rate Limiting** e retry automático
- ✅ **Health Checks** e métricas via Actuator

## 🏗️ Arquitetura

```
                    ┌──────────────────┐
                    │  oficina-gateway │
                    │    (porta 8080)  │
                    └────────┬─────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                     │
   ┌────▼────┐        ┌─────▼─────┐        ┌──────▼──────┐
   │   OS    │        │  Billing  │        │  Execution  │
   │ :8081   │        │  :8082    │        │   :8083     │
   └─────────┘        └───────────┘        └─────────────┘
        │                    │                     │
   ┌────▼────┐        ┌─────▼─────┐        ┌──────▼──────┐
   │Customer │        │  Catalog  │        │   People    │
   │ :8084   │        │  :8085    │        │   :8086     │
   └─────────┘        └───────────┘        └─────────────┘
        │                    │                     │
   ┌────▼────┐        ┌─────▼─────┐        ┌──────▼──────┐
   │   HR    │        │Maintenance│        │Notification │
   │ :8087   │        │  :8088    │        │   :8089     │
   └─────────┘        └───────────┘        └─────────────┘
                             │
                    ┌────────▼─────────┐
                    │   Operations     │
                    │     :8090        │
                    └──────────────────┘
```

## 🚀 Rotas

| Rota | Serviço | Porta |
|------|---------|-------|
| `/api/v1/ordens-servico/**` | OS Service | 8081 |
| `/api/v1/orcamentos/**`, `/api/v1/pagamentos/**` | Billing Service | 8082 |
| `/api/v1/execucoes/**`, `/api/v1/diagnosticos/**` | Execution Service | 8083 |
| `/api/v1/clientes/**`, `/api/v1/veiculos/**` | Customer Service | 8084 |
| `/api/v1/pecas/**`, `/api/v1/servicos/**` | Catalog Service | 8085 |
| `/api/v1/pessoas/**` | People Service | 8086 |
| `/api/v1/funcionarios/**` | HR Service | 8087 |
| `/api/v1/manutencoes/**` | Maintenance Service | 8088 |
| `/api/v1/notificacoes/**` | Notification Service | 8089 |
| `/api/v1/operacoes/**` | Operations Service | 8090 |

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Descrição | Default |
|----------|-----------|---------|
| `JWT_SECRET` | Chave secreta para validação JWT | (obrigatório) |
| `OS_SERVICE_URL` | URL do OS Service | `http://localhost:8081` |
| `BILLING_SERVICE_URL` | URL do Billing Service | `http://localhost:8082` |
| `EXECUTION_SERVICE_URL` | URL do Execution Service | `http://localhost:8083` |
| `CUSTOMER_SERVICE_URL` | URL do Customer Service | `http://localhost:8084` |
| `CATALOG_SERVICE_URL` | URL do Catalog Service | `http://localhost:8085` |
| `PEOPLE_SERVICE_URL` | URL do People Service | `http://localhost:8086` |
| `HR_SERVICE_URL` | URL do HR Service | `http://localhost:8087` |
| `MAINTENANCE_SERVICE_URL` | URL do Maintenance Service | `http://localhost:8088` |
| `NOTIFICATION_SERVICE_URL` | URL do Notification Service | `http://localhost:8089` |
| `OPERATIONS_SERVICE_URL` | URL do Operations Service | `http://localhost:8090` |

### Execução Local

```bash
mvn spring-boot:run
```

### Docker

```bash
mvn package -DskipTests
docker build -t oficina-gateway .
docker run -p 8080:8080 \
  -e JWT_SECRET=sua-chave-secreta \
  -e OS_SERVICE_URL=http://host.docker.internal:8081 \
  oficina-gateway
```

### Kubernetes

```bash
kubectl apply -f k8s/deployment.yml
```

## 🛡️ Segurança

- **JWT**: Todas as requisições (exceto `/actuator`, `/fallback`, `/swagger-ui`) exigem token Bearer válido
- **Headers propagados**: `X-User-Id` e `X-User-Role` são extraídos do JWT e enviados aos serviços downstream
- **CORS**: Configurado para aceitar requisições de qualquer origem (ajustar em produção)

## 📊 Circuit Breaker

Cada serviço tem seu próprio circuit breaker com:
- **Sliding Window**: 10 chamadas
- **Failure Rate Threshold**: 50%
- **Wait in Open State**: 30s
- **Timeout**: 10s por chamada

Quando um serviço está indisponível, o gateway retorna:
```json
{
  "status": 503,
  "error": "Service Unavailable",
  "message": "O serviço 'os-service' está temporariamente indisponível.",
  "service": "os-service",
  "timestamp": "2026-02-14T17:20:00"
}
```

## 📈 Monitoramento

- **Health Check**: `GET /actuator/health`
- **Métricas**: `GET /actuator/metrics`
- **Rotas ativas**: `GET /actuator/gateway/routes`
- **Circuit Breakers**: `GET /actuator/circuitbreakers`

## 🔗 Repositórios Relacionados

| Serviço | Repositório |
|---------|-------------|
| OS Service | [oficina-os-service](https://github.com/rajada1/oficina-os-service) |
| Billing Service | [oficina-billing-service](https://github.com/rajada1/oficina-billing-service) |
| Execution Service | [oficina-execution-service](https://github.com/rajada1/oficina-execution-service) |
| Customer Service | [oficina-customer-service](https://github.com/rajada1/oficina-customer-service) |
| Catalog Service | [oficina-catalog-service](https://github.com/rajada1/oficina-catalog-service) |
| People Service | [oficina-people-service](https://github.com/rajada1/oficina-people-service) |
| HR Service | [oficina-hr-service](https://github.com/rajada1/oficina-hr-service) |
| Maintenance Service | [oficina-maintenance-service](https://github.com/rajada1/oficina-maintenance-service) |
| Notification Service | [oficina-notification-service](https://github.com/rajada1/oficina-notification-service) |
| Operations Service | [oficina-operations-service](https://github.com/rajada1/oficina-operations-service) |
