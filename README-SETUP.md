# 🚗 Sistema de Oficina Mecânica - Setup Completo

## 📋 Visão Geral

Este projeto implementa uma arquitetura de microserviços completa para um sistema de oficina mecânica, incluindo:

- **10 Microserviços** Spring Boot
- **API Gateway** com Spring Cloud Gateway
- **Infraestrutura completa**: PostgreSQL, DynamoDB, Kafka, Zookeeper
- **Frontend** separado (HTML/CSS/JS)
- **Monitoramento** com Kafka UI

## 🏗️ Arquitetura dos Serviços

| Serviço | Porta | Banco | Descrição |
|---------|-------|-------|-----------|
| **API Gateway** | 8080 | - | Ponto de entrada único |
| **OS Service** | 8081 | PostgreSQL | Ordens de Serviço |
| **Billing Service** | 8082 | DynamoDB | Orçamentos e Pagamentos |
| **Execution Service** | 8083 | PostgreSQL | Execução e Diagnósticos |
| **Customer Service** | 8084 | PostgreSQL | Clientes e Veículos |
| **Catalog Service** | 8085 | DynamoDB | Peças e Serviços |
| **People Service** | 8086 | PostgreSQL | Pessoas (Funcionários/Clientes) |
| **HR Service** | 8087 | PostgreSQL | Recursos Humanos |
| **Maintenance Service** | 8088 | PostgreSQL | Manutenção |
| **Notification Service** | 8089 | PostgreSQL | Notificações |
| **Operations Service** | 8090 | PostgreSQL | Operações |

## 🗄️ Infraestrutura

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| **PostgreSQL Main** | 5432 | Banco principal |
| **PostgreSQL Execution** | 5433 | Banco para execução |
| **DynamoDB** | 8000 | NoSQL para billing/catalog |
| **Kafka** | 9092 | Message broker |
| **Zookeeper** | 2181 | Coordenação Kafka |
| **Kafka UI** | 9090 | Interface web Kafka |

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados
- Pelo menos 4GB RAM disponível
- Portas 8080-8090, 5432-5433, 8000, 9090-9092, 2181 livres

### Passo 1: Clonar e navegar para o diretório
```bash
cd /caminho/para/fase_quatro
```

### Passo 2: Executar o setup completo (Primeira vez)
```bash
./start-all-apis.sh build
```
Este comando irá:
- Construir todas as imagens Docker
- Iniciar infraestrutura (bancos, Kafka)
- Subir todos os 10 microserviços + gateway
- Aguardar health checks

### Passo 3: Usos subsequentes (apenas iniciar)
```bash
./start-all-apis.sh
```

### Passo 4: Verificar status dos serviços
```bash
./start-all-apis.sh status
```

### Passo 5: Acessar aplicações

#### API Gateway (Ponto de entrada principal)
- **URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health

#### Serviços individuais (para desenvolvimento/debugging)
- OS Service: http://localhost:8081
- Billing Service: http://localhost:8082
- Execution Service: http://localhost:8083
- Customer Service: http://localhost:8084
- Catalog Service: http://localhost:8085
- People Service: http://localhost:8086
- HR Service: http://localhost:8087
- Maintenance Service: http://localhost:8088
- Notification Service: http://localhost:8089
- Operations Service: http://localhost:8090

#### Infraestrutura
- **Kafka UI**: http://localhost:9090
- **PostgreSQL Main**: localhost:5432 (user: postgres, password: postgres)
- **PostgreSQL Execution**: localhost:5433 (user: postgres, password: postgres)
- **DynamoDB**: localhost:8000

#### Frontend (Separado)
```bash
cd oficina-frontend
# Abrir index.html no navegador
```

## 🛠️ Scripts de Automação

### `start-all-apis.sh`

Comandos disponíveis:
- `./start-all-apis.sh build` - Primeira execução (build + up)
- `./start-all-apis.sh` - Iniciar serviços existentes
- `./start-all-apis.sh stop` - Parar todos os serviços
- `./start-all-apis.sh restart` - Reiniciar todos os serviços
- `./start-all-apis.sh status` - Verificar status dos containers
- `./start-all-apis.sh logs [serviço]` - Ver logs de um serviço específico

### Health Checks Automáticos

O script aguarda automaticamente:
- ✅ PostgreSQL databases healthy
- ✅ DynamoDB healthy
- ✅ Kafka/Zookeeper healthy
- ✅ Todos os 10 microserviços healthy
- ✅ API Gateway healthy

## 🔧 Desenvolvimento

### Adicionar novo serviço
1. Criar Dockerfile no diretório do serviço
2. Adicionar serviço no `docker-compose.yml`
3. Configurar rotas no `RouteConfig.java` do gateway
4. Atualizar `start-all-apis.sh` se necessário

### Debug individual
```bash
# Logs de um serviço específico
docker logs oficina-customer-service -f

# Acessar container
docker exec -it oficina-customer-service bash

# Health check individual
curl http://localhost:8084/actuator/health
```

## 📊 Monitoramento

### Health Checks
Todos os serviços expõem `/actuator/health` para monitoramento.

### Logs
- Application logs: `docker logs [service-name]`
- Infrastructure logs: `docker logs oficina-kafka`

### Métricas
- Spring Boot Actuator em cada serviço
- Kafka UI para mensagens
- PostgreSQL/DynamoDB para dados

## 🐛 Troubleshooting

### Serviço não inicia
```bash
# Verificar logs
docker logs oficina-[serviço]

# Verificar dependências
docker ps | grep oficina
```

### Porta ocupada
```bash
# Verificar quem está usando a porta
lsof -i :8080

# Mudar porta no docker-compose.yml
```

### Memória insuficiente
- Aumentar limite do Docker Desktop
- Fechar outras aplicações
- Usar `docker system prune` para limpar

### Kafka não conecta
```bash
# Verificar Kafka
docker logs oficina-kafka

# Reiniciar Kafka
docker restart oficina-kafka oficina-zookeeper
```

## 📝 Arquivos de Configuração

### Criados/Modificados
- `docker-compose.yml` - Infraestrutura completa
- `start-all-apis.sh` - Script de automação
- `init-databases.sql` - Criação automática dos bancos
- Dockerfiles para todos os serviços
- Correções em `pom.xml` dos serviços
- `RouteConfig.java` corrigido no gateway

### Configurações por Ambiente
- `application.yml` - Configurações principais
- `application-local.properties` - Configurações locais
- `application-test.yml` - Configurações de teste

## 🎯 Status Final

✅ **TODOS os 10 microserviços compilando e executando**
✅ **API Gateway funcionando com roteamento**
✅ **Infraestrutura completa (PostgreSQL, DynamoDB, Kafka)**
✅ **Health checks automáticos**
✅ **Script de automação completo**
✅ **Frontend separado funcionando**

## 📞 Suporte

Para problemas:
1. Verificar logs com `./start-all-apis.sh logs [serviço]`
2. Verificar status com `./start-all-apis.sh status`
3. Verificar conectividade de infraestrutura
4. Reiniciar serviços específicos se necessário

---

**🎉 Sistema pronto para desenvolvimento e testes!**