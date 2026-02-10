# Script para iniciar todos os microserviços com profile local
# Cada serviço será iniciado em uma nova janela CMD

$basePath = "C:\Users\Meu Computador\OneDrive\Área de Trabalho\FIAP\projeto\fase_quatro"

$services = @(
    @{name = "people-service"; port = 8081; jar = "oficina-people-service" },
    @{name = "customer-service"; port = 8082; jar = "oficina-customer-service" },
    @{name = "catalog-service"; port = 8083; jar = "oficina-catalog-service" },
    @{name = "hr-service"; port = 8084; jar = "oficina-hr-service" },
    @{name = "os-service"; port = 8085; jar = "oficina-os-service" },
    @{name = "billing-service"; port = 8086; jar = "oficina-billing-service" },
    @{name = "execution-service"; port = 8087; jar = "oficina-execution-service" },
    @{name = "maintenance-service"; port = 8088; jar = "oficina-maintenance-service" },
    @{name = "notification-service"; port = 8089; jar = "oficina-notification-service" },
    @{name = "operations-service"; port = 8090; jar = "oficina-operations-service" }
)

foreach ($service in $services) {
    $servicePath = Join-Path $basePath $service.jar
    $jarPath = Join-Path $servicePath "target\$($service.jar)-0.0.1-SNAPSHOT.jar"
    
    if (Test-Path $jarPath) {
        Write-Host "Iniciando $($service.name) na porta $($service.port)..." -ForegroundColor Green
        Start-Process cmd -ArgumentList "/k", "title $($service.name) && cd /d `"$servicePath`" && java -jar target\$($service.jar)-0.0.1-SNAPSHOT.jar --spring.profiles.active=local"
        Start-Sleep -Seconds 2
    }
    else {
        Write-Host "JAR não encontrado para $($service.name): $jarPath" -ForegroundColor Red
    }
}

Write-Host "`nTodos os serviços foram iniciados!" -ForegroundColor Cyan
Write-Host "Aguarde alguns segundos para os serviços inicializarem...`n"
