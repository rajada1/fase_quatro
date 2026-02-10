# Customer Service

Microsserviço responsável pelo gerenciamento de clientes e veículos.

## Funcionalidades

- **Gerenciamento de Clientes**: Criar, atualizar, buscar e deletar clientes
- **Gerenciamento de Veículos**: Registrar veículos para cada cliente
- **Publicação de Eventos**: Publica eventos de negócio via AWS SQS
- **Observabilidade**: Integração com New Relic para monitoramento
- **Documentação**: OpenAPI/Swagger disponível em `/swagger-ui.html`

## Endpoints

### Clientes

- `POST /api/v1/clientes` - Criar novo cliente
- `GET /api/v1/clientes` - Listar todos os clientes
- `GET /api/v1/clientes/{id}` - Buscar cliente por ID
- `PUT /api/v1/clientes/{id}` - Atualizar cliente
- `DELETE /api/v1/clientes/{id}` - Deletar cliente

### Veículos

- `POST /api/v1/clientes/{clienteId}/veiculos` - Registrar veículo
- `GET /api/v1/clientes/{clienteId}/veiculos` - Listar veículos do cliente
- `GET /api/v1/veiculos/{id}` - Buscar veículo por ID
- `PUT /api/v1/veiculos/{id}` - Atualizar veículo
- `DELETE /api/v1/veiculos/{id}` - Deletar veículo

## Eventos Publicados

- `CLIENTE_CRIADO` - Publicado quando um novo cliente é criado
- `CLIENTE_ATUALIZADO` - Publicado quando dados do cliente são atualizados
- `CLIENTE_DELETADO` - Publicado quando um cliente é removido
- `VEICULO_REGISTRADO` - Publicado quando um veículo é registrado
- `VEICULO_DELETADO` - Publicado quando um veículo é removido

## Dependências

- Spring Boot 3.3.13
- Spring Data JPA
- PostgreSQL 42.7.7
- AWS Spring Cloud SQS
- New Relic APM
- Cucumber for BDD Testing

## Configuração

### Variáveis de Ambiente

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/customer_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
AWS_REGION=us-east-1
AWS_SQS_CUSTOMER_EVENTS_QUEUE=customer-events-queue
```

## Execução

### Build

```bash
mvn clean package
```

### Testes

```bash
# Todos os testes
mvn test

# Apenas testes BDD
mvn test -Dgroups=bdd

# Com cobertura
mvn test jacoco:report
```

### Executar Serviço

```bash
mvn spring-boot:run
```

## Estrutura do Projeto

```
src/
  main/
    java/br/com/grupo99/customerservice/
      adapter/
        config/
        controller/
      application/
        dto/
        exception/
        service/
      domain/
        model/
        repository/
    resources/
      application.properties
  test/
    java/br/com/grupo99/customerservice/
      bdd/
      adapter/
      application/
    resources/
      features/
      application-test.properties
```

## Integração com Outros Serviços

O Customer Service publica eventos que são consumidos por:
- **Order Service (OS Service)**: Consome `CLIENTE_CRIADO` e `VEICULO_REGISTRADO` para validações
- **Analytics Service**: Consome eventos para relatórios
- **Notification Service**: Consome eventos para notificações

## Monitoramento

- Métricas expostas em `/actuator/metrics`
- Health check em `/actuator/health`
- Prometheus metrics em `/actuator/prometheus`
- APM integrado com New Relic (detalhes em environment variables)
