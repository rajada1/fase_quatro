@echo off
title Oficina - Start All Local Services
echo ============================================
echo   INICIANDO TODOS OS SERVICOS LOCAIS
echo ============================================
echo.

cd /d "C:\Users\Meu Computador\OneDrive\Área de Trabalho\FIAP\projeto\fase_quatro"

echo [1/2] Iniciando LocalStack, PostgreSQL, MongoDB...
docker compose -f docker-compose.localstack.yml up -d

echo.
echo Aguardando containers ficarem saudaveis...
timeout /t 15 /nobreak

echo.
echo [2/2] Verificando status dos containers...
docker compose -f docker-compose.localstack.yml ps

echo.
echo ============================================
echo   SERVICOS LOCAIS INICIADOS!
echo ============================================
echo.
echo URLs disponiveis:
echo   - LocalStack:    http://localhost:4566
echo   - PostgreSQL:    localhost:5433
echo   - MongoDB:       localhost:27017
echo   - Adminer:       http://localhost:8090
echo   - Mongo Express: http://localhost:8091
echo.
echo Para iniciar os microservicos, execute:
echo   - oficina-customer-service\start-local.bat
echo   - oficina-vehicle-service\start-local.bat
echo   - etc...
echo.
pause
