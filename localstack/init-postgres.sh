#!/bin/bash
# =============================================================================
# Script de Inicialização do PostgreSQL
# Cria todos os bancos de dados necessários para os microserviços
# =============================================================================

set -e

echo "=============================================="
echo "Iniciando criação dos bancos de dados..."
echo "=============================================="

# Conexão com PostgreSQL
POSTGRES_USER=${POSTGRES_USER:-postgres}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-postgres}

# Função para criar banco de dados se não existir
create_database() {
    local db_name=$1
    echo "Criando banco de dados: $db_name"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        SELECT 'CREATE DATABASE $db_name' 
        WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$db_name')\gexec
        GRANT ALL PRIVILEGES ON DATABASE $db_name TO $POSTGRES_USER;
EOSQL
    echo "  ✓ $db_name"
}

echo ""

# =============================================================================
# Bancos de dados dos microserviços
# =============================================================================

# OS Service
create_database "os_db"

# Execution Service
create_database "execution_db"

# Customer Service
create_database "customer_db"

# People Service
create_database "people_db"

# HR Service
create_database "hr_db"

# Catalog Service
create_database "catalog_db"

# Maintenance Service
create_database "maintenance_db"

# Notification Service
create_database "notification_db"

# Operations Service
create_database "operations_db"

# Tech FIAP (Main)
create_database "tech_fiap_db"

echo ""
echo "=============================================="
echo "Bancos de dados criados com sucesso!"
echo "=============================================="
echo ""
echo "Bancos criados:"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -c "\l" | grep -E "os_db|execution_db|customer_db|people_db|hr_db|catalog_db|maintenance_db|notification_db|operations_db|tech_fiap_db"
echo ""
