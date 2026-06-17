$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DDD Refactor Deploy" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Build Frontend
Write-Host ""
Write-Host "[1/4] Building frontend..." -ForegroundColor Yellow

Push-Location "$PSScriptRoot\blog-platform\blog-frontend"
if (-not (Test-Path "node_modules")) { 
    npm install 
}
Write-Host "  Building blog-frontend..." -ForegroundColor Gray
npm run build
Pop-Location

Push-Location "$PSScriptRoot\blog-platform\blog-admin"
if (-not (Test-Path "node_modules")) { 
    npm install 
}
Write-Host "  Building blog-admin..." -ForegroundColor Gray
npm run build
Pop-Location
Write-Host "  Frontend build done." -ForegroundColor Green

# 2. Build Backend
Write-Host ""
Write-Host "[2/4] Building backend Docker image..." -ForegroundColor Yellow
docker compose build --no-cache backend
if ($LASTEXITCODE -ne 0) {
    Write-Host "  [ERROR] Docker build failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  Backend image done." -ForegroundColor Green

# 3. Restart
Write-Host ""
Write-Host "[3/4] Restarting services..." -ForegroundColor Yellow

docker compose up -d mysql redis

Write-Host "  Waiting for MySQL..." -ForegroundColor Gray
$retries = 0
do {
    Start-Sleep -Seconds 3
    $healthy = docker compose ps mysql | Select-String "healthy"
    $retries++
} while (-not $healthy -and $retries -lt 30)

if (-not $healthy) {
    Write-Host "  [ERROR] MySQL timeout" -ForegroundColor Red
    docker compose logs mysql --tail=30
    exit 1
}
Write-Host "  MySQL ready." -ForegroundColor Green

docker compose up -d --force-recreate backend nginx
Write-Host "  Services restarted." -ForegroundColor Green

# 4. Health check
Write-Host ""
Write-Host "[4/4] Health check..." -ForegroundColor Yellow

Write-Host "  Waiting for backend..." -ForegroundColor Gray
$retries = 0
do {
    Start-Sleep -Seconds 5
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/doc.html" -TimeoutSec 3 -UseBasicParsing
        $backendReady = $response.StatusCode -eq 200
    } catch {
        $backendReady = $false
    }
    $retries++
} while (-not $backendReady -and $retries -lt 30)

if ($backendReady) {
    Write-Host "  Backend API ready." -ForegroundColor Green
} else {
    Write-Host "  [WARN] Backend still starting, check logs." -ForegroundColor Yellow
}

Write-Host ""
docker compose ps

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Deploy Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Blog:      http://localhost:3000" -ForegroundColor Cyan
Write-Host "  Admin:     http://localhost:3000/admin" -ForegroundColor Cyan
Write-Host "  API Docs:  http://localhost:8080/api/doc.html" -ForegroundColor Cyan
Write-Host "  Admin login: admin / admin123" -ForegroundColor Yellow
