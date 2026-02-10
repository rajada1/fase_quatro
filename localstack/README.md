# 🐳 LocalStack - Ambiente de Desenvolvimento Local

Este diretório contém a configuração completa para executar os microserviços localmente usando **LocalStack** para emular serviços AWS.

## 📋 Pré-requisitos

- Docker Desktop instalado e rodando
- Docker Compose v2+
- Java 21+
- Maven 3.9+

## 🚀 Início Rápido

### 1. Subir toda a infraestrutura

```bash
# Na raiz do projeto (fase_quatro)
docker-compose -f docker-compose.localstack.yml up -d
```

### 2. Verificar se os serviços estão rodando

```bash
docker-compose -f docker-compose.localstack.yml ps
```

### 3. Verificar as filas SQS criadas

```bash
# Usando AWS CLI com LocalStack
aws --endpoint-url=http://localhost:4566 sqs list-queues --region us-east-1

# Ou acesse o dashboard do LocalStack
# http://localhost:4566/_localstack/extensions/ui
```

### 4. Verificar os bancos de dados

```bash
# Acessar Adminer (PostgreSQL)
# http://localhost:8090
# Sistema: PostgreSQL
# Servidor: postgres
# Usuário: postgres
# Senha: postgres

# Acessar Mongo Express (MongoDB)
# http://localhost:8091
```

## 📦 Serviços Disponíveis

| Serviço | Porta | URL |
|---------|-------|-----|
| LocalStack | 4566 | http://localhost:4566 |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/db_name |
| MongoDB | 27017 | mongodb://localhost:27017 |
| Adminer | 8090 | http://localhost:8090 |
| Mongo Express | 8091 | http://localhost:8091 |

## 📨 Filas SQS Criadas

### Por Microserviço

| Serviço | Filas |
|---------|-------|
| OS Service | `os-events-queue`, `os-events-dlq` |
| Billing Service | `billing-events-queue`, `billing-events-dlq`, `payment-events-queue`, `payment-events-dlq` |
| Execution Service | `execution-events-queue`, `execution-events-dlq`, `diagnostico-events-queue`, `diagnostico-events-dlq` |
| Customer Service | `customer-events-queue`, `customer-events-dlq`, `veiculo-events-queue`, `veiculo-events-dlq` |
| People Service | `pessoas-events-queue`, `pessoas-events-dlq` |
| HR Service | `hr-events-queue`, `hr-events-dlq`, `funcionario-events-queue`, `funcionario-events-dlq` |
| Catalog Service | `catalog-events-queue`, `catalog-events-dlq`, `peca-events-queue`, `servico-events-queue` |
| Maintenance Service | `maintenance-events-queue`, `maintenance-events-dlq` |
| Notification Service | `notification-events-queue`, `notification-events-dlq`, `email-queue`, `sms-queue` |
| Operations Service | `operations-queue`, `operations-dlq` |
| Saga (Orquestração) | `saga-orchestrator-queue`, `saga-compensation-queue`, `saga-reply-queue` |

## 🗄️ Bancos de Dados

### PostgreSQL

| Banco | Microserviço |
|-------|--------------|
| `os_db` | oficina-os-service |
| `execution_db` | oficina-execution-service |
| `customer_db` | oficina-customer-service |
| `people_db` | oficina-people-service |
| `hr_db` | oficina-hr-service |
| `catalog_db` | oficina-catalog-service |
| `maintenance_db` | oficina-maintenance-service |
| `notification_db` | oficina-notification-service |
| `operations_db` | oficina-operations-service |
| `tech_fiap_db` | tech_fiap3 |

### MongoDB

| Banco | Microserviço |
|-------|--------------|
| `billing_db` | oficina-billing-service |

## ⚙️ Configuração dos Microserviços

### Opção 1: Usando perfil local

Crie um arquivo `application-local.properties` em cada microserviço:

```properties
# AWS LocalStack
spring.cloud.aws.endpoint=http://localhost:4566
spring.cloud.aws.region.static=us-east-1
spring.cloud.aws.credentials.access-key=test
spring.cloud.aws.credentials.secret-key=test

# Database (ajuste o nome do banco)
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Execute com:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Opção 2: Usando variáveis de ambiente

```bash
export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_REGION=us-east-1

mvn spring-boot:run
```

## 🔧 Comandos Úteis

### Listar filas SQS

```bash
aws --endpoint-url=http://localhost:4566 sqs list-queues --region us-east-1
```

### Enviar mensagem para uma fila

```bash
aws --endpoint-url=http://localhost:4566 sqs send-message \
    --queue-url http://localhost:4566/000000000000/os-events-queue \
    --message-body '{"tipo":"NOVA_OS","osId":"123"}' \
    --region us-east-1
```

### Receber mensagens de uma fila

```bash
aws --endpoint-url=http://localhost:4566 sqs receive-message \
    --queue-url http://localhost:4566/000000000000/os-events-queue \
    --region us-east-1
```

### Ver logs do LocalStack

```bash
docker-compose -f docker-compose.localstack.yml logs -f localstack
```

### Reiniciar todos os serviços

```bash
docker-compose -f docker-compose.localstack.yml down
docker-compose -f docker-compose.localstack.yml up -d
```

### Limpar todos os dados e começar do zero

```bash
docker-compose -f docker-compose.localstack.yml down -v
docker-compose -f docker-compose.localstack.yml up -d
```

## 🐛 Troubleshooting

### Erro: "Queue does not exist"

As filas são criadas automaticamente quando o LocalStack inicia. Se ainda não existirem:

```bash
# Execute o script manualmente
docker exec -it localstack bash /etc/localstack/init/ready.d/init-aws.sh
```

### Erro: "Connection refused" ao conectar ao PostgreSQL

Verifique se o container está rodando:
```bash
docker-compose -f docker-compose.localstack.yml ps postgres
```

### Erro: "Could not connect to MongoDB"

```bash
docker-compose -f docker-compose.localstack.yml logs mongodb
```

### LocalStack não está criando as filas

Verifique os logs:
```bash
docker-compose -f docker-compose.localstack.yml logs localstack
```

## 📁 Estrutura de Arquivos

```
localstack/
├── init-aws.sh              # Script para criar filas SQS
├── init-postgres.sh         # Script para criar bancos de dados
├── application-local.properties.template  # Template de configuração
└── README.md                # Esta documentação

docker-compose.localstack.yml  # Arquivo principal do Docker Compose
```

## 🔗 Links Úteis

- [LocalStack Documentação](https://docs.localstack.cloud/)
- [AWS CLI com LocalStack](https://docs.localstack.cloud/user-guide/integrations/aws-cli/)
- [Spring Cloud AWS](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html)
