# ============================================
# Agent 研发团队 - 后端质量门禁脚本
# ============================================
# 用途：本地运行质量门禁 Gate 1-2（后端）
# 用法：.\backend-check.ps1 -ProjectDir "blog-server"

param(
    [Parameter(Mandatory=$true)]
    [string]$ProjectDir
)

$ErrorActionPreference = "Stop"
$OriginalDir = Get-Location

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Agent Team 质量门禁 - 后端检查" -ForegroundColor Cyan
Write-Host "  项目: $ProjectDir" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

try {
    Set-Location "$ProjectDir"

    # ==================== Gate 1: 编译验证 ====================
    Write-Host "[Gate 1] 编译验证 (mvn compile)..." -ForegroundColor Yellow
    $compileResult = mvn compile -B 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Gate 1 失败: 编译错误" -ForegroundColor Red
        Write-Host $compileResult
        exit 1
    }
    Write-Host "✅ Gate 1 通过: 编译成功" -ForegroundColor Green
    Write-Host ""

    # ==================== Gate 2: 单元测试 ====================
    Write-Host "[Gate 2] 单元测试 (mvn test)..." -ForegroundColor Yellow
    $testResult = mvn test -B 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Gate 2 失败: 单元测试未通过" -ForegroundColor Red
        Write-Host $testResult
        exit 1
    }
    Write-Host "✅ Gate 2 通过: 所有单元测试通过" -ForegroundColor Green
    Write-Host ""

    # ==================== Gate 2b: 打包验证 ====================
    Write-Host "[Gate 2b] 打包验证 (mvn package)..." -ForegroundColor Yellow
    $packageResult = mvn package -DskipTests -B 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Gate 2b 失败: 打包错误" -ForegroundColor Red
        Write-Host $packageResult
        exit 1
    }

    $jarFiles = Get-ChildItem -Path "target" -Filter "*.jar" | Where-Object { $_.Name -notlike "*-sources.jar" }
    if ($jarFiles.Count -eq 0) {
        Write-Host "❌ Gate 2b 失败: 未找到 JAR 包" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ Gate 2b 通过: JAR 包已生成 ($($jarFiles.Count) 个)" -ForegroundColor Green
    Write-Host ""

    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  🎉 所有后端质量门禁通过！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan

} finally {
    Set-Location $OriginalDir
}
