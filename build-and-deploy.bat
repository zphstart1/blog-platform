@echo off
cd /d "%~dp0"
echo === DDD Build ^& Deploy (no-cache) ===
echo [%date% %time%] Starting build...
docker compose build --no-cache backend
if %ERRORLEVEL% neq 0 (
    echo [%date% %time%] BUILD FAILED
    pause
    exit /b 1
)
echo [%date% %time%] BUILD OK - Deploying...
docker compose up -d backend nginx
echo [%date% %time%] Waiting 20s for Spring Boot startup...
timeout /t 20 /nobreak > nul
docker compose logs backend --tail=10
echo [%date% %time%] Done. Press any key to close.
pause > nul
