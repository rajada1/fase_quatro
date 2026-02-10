@echo off
setlocal enabledelayedexpansion

set BASE_PATH=C:\Users\Meu Computador\OneDrive\Área de Trabalho\FIAP\projeto\fase_quatro

echo ================================================
echo  Compilando todos os microservicos
echo ================================================
echo.

set services=oficina-people-service oficina-customer-service oficina-catalog-service oficina-os-service oficina-execution-service oficina-billing-service oficina-hr-service oficina-maintenance-service oficina-notification-service oficina-operations-service

for %%s in (%services%) do (
    echo Compilando %%s...
    cd /d "%BASE_PATH%\%%s"
    call mvn package -DskipTests -q
    if !errorlevel! equ 0 (
        echo   [OK] %%s compilado com sucesso
    ) else (
        echo   [ERRO] Falha ao compilar %%s
    )
    echo.
)

echo ================================================
echo  Compilacao concluida!
echo ================================================
pause
