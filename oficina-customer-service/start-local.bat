@echo off
title Oficina Customer Service - Local
echo ============================================
echo   INICIANDO CUSTOMER SERVICE (LOCAL)
echo ============================================
echo.

cd /d "C:\Users\Meu Computador\OneDrive\Área de Trabalho\FIAP\projeto\fase_quatro\oficina-customer-service"

echo Verificando se LocalStack esta rodando...
curl -s http://localhost:4566/health >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] LocalStack nao esta rodando!
    echo Execute primeiro: docker compose -f docker-compose.localstack.yml up -d
    pause
    exit /b 1
)

echo LocalStack OK!
echo.
echo Iniciando Customer Service na porta 8082...
echo Profile: local
echo.

java -jar "target\oficina-customer-service-0.0.1-SNAPSHOT.jar" --spring.profiles.active=local

pause
