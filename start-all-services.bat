@echo off
echo ================================================
echo  Iniciando Microservicos - Oficina Local
echo ================================================
echo.

set BASE_PATH=C:\Users\Meu Computador\OneDrive\Área de Trabalho\FIAP\projeto\fase_quatro

echo [1/10] Iniciando oficina-people-service (porta 8081)...
start "people-service" cmd /k "cd /d "%BASE_PATH%\oficina-people-service" && java -jar target\oficina-people-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [2/10] Iniciando oficina-customer-service (porta 8082)...
start "customer-service" cmd /k "cd /d "%BASE_PATH%\oficina-customer-service" && java -jar target\oficina-customer-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [3/10] Iniciando oficina-catalog-service (porta 8083)...
start "catalog-service" cmd /k "cd /d "%BASE_PATH%\oficina-catalog-service" && java -jar target\oficina-catalog-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [4/10] Iniciando oficina-os-service (porta 8084)...
start "os-service" cmd /k "cd /d "%BASE_PATH%\oficina-os-service" && java -jar target\oficina-os-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [5/10] Iniciando oficina-execution-service (porta 8085)...
start "execution-service" cmd /k "cd /d "%BASE_PATH%\oficina-execution-service" && java -jar target\oficina-execution-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [6/10] Iniciando oficina-billing-service (porta 8086)...
start "billing-service" cmd /k "cd /d "%BASE_PATH%\oficina-billing-service" && java -jar target\oficina-billing-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [7/10] Iniciando oficina-hr-service (porta 8087)...
start "hr-service" cmd /k "cd /d "%BASE_PATH%\oficina-hr-service" && java -jar target\oficina-hr-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [8/10] Iniciando oficina-maintenance-service (porta 8088)...
start "maintenance-service" cmd /k "cd /d "%BASE_PATH%\oficina-maintenance-service" && java -jar target\oficina-maintenance-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [9/10] Iniciando oficina-notification-service (porta 8089)...
start "notification-service" cmd /k "cd /d "%BASE_PATH%\oficina-notification-service" && java -jar target\oficina-notification-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
timeout /t 3 /nobreak > nul

echo [10/10] Iniciando oficina-operations-service (porta 8090)...
start "operations-service" cmd /k "cd /d "%BASE_PATH%\oficina-operations-service" && java -jar target\oficina-operations-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"

echo.
echo ================================================
echo  Todos os microservicos foram iniciados!
echo ================================================
echo.
echo Portas:
echo   - people-service:       http://localhost:8081
echo   - customer-service:     http://localhost:8082
echo   - catalog-service:      http://localhost:8083
echo   - os-service:           http://localhost:8084
echo   - execution-service:    http://localhost:8085
echo   - billing-service:      http://localhost:8086
echo   - hr-service:           http://localhost:8087
echo   - maintenance-service:  http://localhost:8088
echo   - notification-service: http://localhost:8089
echo   - operations-service:   http://localhost:8090
echo.
pause
