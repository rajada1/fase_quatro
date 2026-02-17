#!/bin/bash

# =========================================================
#  Script de Automação - Oficina Mecânica
#  Sobe TODAS as APIs (backend) via Docker Compose
#  O frontend deve ser rodado separadamente
#
#  Uso:
#    ./start-all-apis.sh          # Sobe tudo
#    ./start-all-apis.sh build    # Rebuild + sobe tudo
#    ./start-all-apis.sh stop     # Para tudo
#    ./start-all-apis.sh status   # Mostra status dos containers
#    ./start-all-apis.sh logs     # Mostra logs de todos os serviços
#    ./start-all-apis.sh clean    # Para tudo e remove volumes
# =========================================================

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Banner
echo -e "${CYAN}"
echo "╔═══════════════════════════════════════════════════════╗"
echo "║      🔧 Oficina Mecânica - Backend Automation        ║"
echo "║      Microservices + Infrastructure                   ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Diretório raiz do projeto
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# Funções auxiliares
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[OK]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar pré-requisitos
check_prerequisites() {
    print_info "Verificando pré-requisitos..."

    if ! command -v docker &> /dev/null; then
        print_error "Docker não encontrado. Instale em https://docs.docker.com/get-docker/"
        exit 1
    fi
    print_success "Docker encontrado: $(docker --version)"

    if ! docker compose version &> /dev/null; then
        if ! docker-compose version &> /dev/null; then
            print_error "Docker Compose não encontrado."
            exit 1
        fi
        COMPOSE_CMD="docker-compose"
    else
        COMPOSE_CMD="docker compose"
    fi
    print_success "Docker Compose encontrado"

    if ! docker info &> /dev/null 2>&1; then
        print_error "Docker daemon não está rodando. Inicie o Docker Desktop."
        exit 1
    fi
    print_success "Docker daemon está rodando"

    echo ""
}

# Subir todos os serviços
start_services() {
    local BUILD_FLAG=""
    if [ "$1" = "build" ]; then
        BUILD_FLAG="--build"
        print_info "Modo BUILD: Reconstruindo todas as imagens..."
    fi

    print_info "Subindo infraestrutura (PostgreSQL, DynamoDB, Kafka)..."
    $COMPOSE_CMD up -d $BUILD_FLAG \
        postgres-main \
        postgres-execution \
        dynamodb-local \
        zookeeper

    print_info "Aguardando infraestrutura ficar saudável..."
    sleep 10

    print_info "Subindo Kafka e inicializações..."
    $COMPOSE_CMD up -d $BUILD_FLAG \
        kafka \
        dynamodb-init

    print_info "Aguardando Kafka ficar saudável..."
    sleep 15

    print_info "Inicializando tópicos do Kafka..."
    $COMPOSE_CMD up -d $BUILD_FLAG kafka-init kafka-ui

    sleep 5

    print_info "Subindo microserviços (isso pode levar alguns minutos no primeiro build)..."
    $COMPOSE_CMD up -d $BUILD_FLAG \
        os-service \
        billing-service \
        execution-service \
        customer-service \
        catalog-service \
        people-service \
        hr-service \
        maintenance-service \
        notification-service \
        operations-service

    print_info "Aguardando microserviços iniciarem..."
    sleep 10

    print_info "Subindo API Gateway..."
    $COMPOSE_CMD up -d $BUILD_FLAG gateway

    echo ""
    print_success "Todos os serviços foram iniciados!"
    echo ""
    show_endpoints
}

# Parar todos os serviços
stop_services() {
    print_info "Parando todos os serviços..."
    $COMPOSE_CMD down
    print_success "Todos os serviços foram parados."
}

# Limpar tudo (incluindo volumes)
clean_services() {
    print_warn "Isso irá remover todos os containers, volumes e dados!"
    read -p "Tem certeza? (y/N): " confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        print_info "Removendo tudo..."
        $COMPOSE_CMD down -v --remove-orphans
        print_success "Tudo removido com sucesso."
    else
        print_info "Operação cancelada."
    fi
}

# Mostrar status dos containers
show_status() {
    print_info "Status dos containers:"
    echo ""
    $COMPOSE_CMD ps
    echo ""

    print_info "Verificando saúde dos serviços..."
    echo ""

    SERVICES=(
        "os-service:8081"
        "billing-service:8082"
        "execution-service:8083"
        "customer-service:8084"
        "catalog-service:8085"
        "people-service:8086"
        "hr-service:8087"
        "maintenance-service:8088"
        "notification-service:8089"
        "operations-service:8090"
        "gateway:8080"
    )

    for svc in "${SERVICES[@]}"; do
        NAME=$(echo "$svc" | cut -d: -f1)
        PORT=$(echo "$svc" | cut -d: -f2)

        if curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/actuator/health" 2>/dev/null | grep -q "200"; then
            print_success "$NAME (porta $PORT) - ✅ Saudável"
        else
            print_warn "$NAME (porta $PORT) - ⏳ Indisponível ou iniciando..."
        fi
    done
    echo ""
}

# Mostrar logs
show_logs() {
    SERVICE_NAME=${1:-""}
    if [ -n "$SERVICE_NAME" ]; then
        print_info "Mostrando logs de $SERVICE_NAME..."
        $COMPOSE_CMD logs -f "$SERVICE_NAME"
    else
        print_info "Mostrando logs de todos os serviços..."
        $COMPOSE_CMD logs -f --tail=50
    fi
}

# Mostrar endpoints
show_endpoints() {
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║              📋 Endpoints Disponíveis                ║${NC}"
    echo -e "${CYAN}╠═══════════════════════════════════════════════════════╣${NC}"
    echo -e "${CYAN}║${NC}                                                       ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ${GREEN}🌐 API Gateway:${NC}     http://localhost:8080             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}                                                       ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ${BLUE}📦 Microserviços (acesso direto):${NC}                    ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • OS Service:           http://localhost:8081         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Billing Service:      http://localhost:8082         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Execution Service:    http://localhost:8083         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Customer Service:     http://localhost:8084         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Catalog Service:      http://localhost:8085         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • People Service:       http://localhost:8086         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • HR Service:           http://localhost:8087         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Maintenance Service:  http://localhost:8088         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Notification Service: http://localhost:8089         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Operations Service:   http://localhost:8090         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}                                                       ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ${YELLOW}🛠️  Ferramentas:${NC}                                      ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • Kafka UI:             http://localhost:9090         ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • PostgreSQL Principal:  localhost:5432               ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • PostgreSQL Execution:  localhost:5433               ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  • DynamoDB Local:        localhost:8000               ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}                                                       ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ${RED}🖥️  Frontend (rodar separadamente):${NC}                  ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  cd oficina-frontend && npx serve .                    ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ou abra oficina-frontend/index.html no navegador      ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}                                                       ${CYAN}║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
}

# ======================== MAIN ========================

check_prerequisites

case "${1:-start}" in
    start)
        start_services
        ;;
    build)
        start_services "build"
        ;;
    stop)
        stop_services
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs "$2"
        ;;
    clean)
        clean_services
        ;;
    endpoints)
        show_endpoints
        ;;
    *)
        echo "Uso: $0 {start|build|stop|status|logs [service]|clean|endpoints}"
        echo ""
        echo "  start     - Sobe todos os serviços (padrão)"
        echo "  build     - Rebuild + sobe todos os serviços"
        echo "  stop      - Para todos os serviços"
        echo "  status    - Mostra status e saúde dos serviços"
        echo "  logs      - Mostra logs (opcional: nome do serviço)"
        echo "  clean     - Para tudo e remove volumes/dados"
        echo "  endpoints - Mostra todos os endpoints disponíveis"
        exit 1
        ;;
esac
