$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Blog Platform Docker Deploy" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Check Docker
Write-Host "`n[1/5] Checking Docker..." -ForegroundColor Yellow
try {
    docker --version *> $null
    docker compose version *> $null
    Write-Host "  Docker OK" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] Docker not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# 2. Build Frontend
Write-Host "`n[2/5] Building frontend..." -ForegroundColor Yellow
try {
    node --version *> $null
} catch {
    Write-Host "  [ERROR] Node.js not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

$frontendRoot = "$PSScriptRoot\blog-platform"

Write-Host "  Building blog-frontend..." -ForegroundColor Gray
Push-Location "$frontendRoot\blog-frontend"
if (-not (Test-Path "node_modules")) { npm install }
npm run build
Pop-Location
Write-Host "  blog-frontend done" -ForegroundColor Green

Write-Host "  Building blog-admin..." -ForegroundColor Gray
Push-Location "$frontendRoot\blog-admin"
if (-not (Test-Path "node_modules")) { npm install }
npm run build
Pop-Location
Write-Host "  blog-admin done" -ForegroundColor Green

# 3. Build Backend
Write-Host "`n[3/5] Building backend Docker image..." -ForegroundColor Yellow
docker compose build backend
Write-Host "  Backend image done" -ForegroundColor Green

# 4. Start Services
Write-Host "`n[4/5] Starting services..." -ForegroundColor Yellow
docker compose up -d mysql redis
Write-Host "  Waiting for MySQL..." -ForegroundColor Gray

$retries = 0
do {
    Start-Sleep -Seconds 3
    $healthy = docker compose ps mysql | Select-String "healthy"
    $retries++
} while (-not $healthy -and $retries -lt 30)

if (-not $healthy) {
    Write-Host "  [ERROR] MySQL startup timeout" -ForegroundColor Red
    docker compose logs mysql
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "  MySQL ready" -ForegroundColor Green

docker compose up -d backend nginx

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

if (-not $backendReady) {
    Write-Host "  [WARN] Backend may still be starting, check: docker compose logs backend" -ForegroundColor Yellow
} else {
    Write-Host "  Backend ready" -ForegroundColor Green
}

# 5. Done
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Deploy Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  URLs:" -ForegroundColor White
Write-Host "    Blog:       http://localhost:3000" -ForegroundColor Cyan
Write-Host "    Admin:      http://localhost:3000/admin" -ForegroundColor Cyan
Write-Host "    API Docs:   http://localhost:8080/api/doc.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Admin: admin / admin123" -ForegroundColor Yellow
Write-Host ""
Write-Host "  MySQL: localhost:3307 (root/root)" -ForegroundColor Gray
Write-Host "  Redis: localhost:6380" -ForegroundColor Gray
Write-Host ""
Write-Host "  docker compose logs -f backend" -ForegroundColor Gray
Write-Host "  docker compose down" -ForegroundColor Gray
Write-Host ""

Read-Host "Press Enter to exit"
