# ============================================
# Agent 研发团队 - 前端质量门禁脚本
# ============================================
# 用途：本地运行质量门禁 Gate 1-2
# 用法：.\lint-check.ps1 -ProjectDir "blog-frontend"
#       .\lint-check.ps1 -ProjectDir "blog-admin"

param(
    [Parameter(Mandatory=$true)]
    [string]$ProjectDir
)

$ErrorActionPreference = "Stop"
$OriginalDir = Get-Location

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Agent Team 质量门禁 - 前端检查" -ForegroundColor Cyan
Write-Host "  项目: $ProjectDir" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

try {
    Set-Location "$ProjectDir"

    # ==================== Gate 1: TypeScript 类型检查 ====================
    Write-Host "[Gate 1] TypeScript 类型检查 (vue-tsc --noEmit)..." -ForegroundColor Yellow
    $tsResult = npx vue-tsc --noEmit 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Gate 1 失败: 类型错误" -ForegroundColor Red
        Write-Host $tsResult
        exit 1
    }
    Write-Host "✅ Gate 1 通过: 类型检查无错误" -ForegroundColor Green
    Write-Host ""

    # ==================== Gate 2: 构建验证 ====================
    Write-Host "[Gate 2] 生产构建 (npm run build)..." -ForegroundColor Yellow
    $buildResult = npm run build 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Gate 2 失败: 构建错误" -ForegroundColor Red
        Write-Host $buildResult
        exit 1
    }
    Write-Host "✅ Gate 2 通过: 构建成功" -ForegroundColor Green

    if (!(Test-Path "dist/index.html")) {
        Write-Host "❌ Gate 2 失败: 缺少 dist/index.html" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 构建产物验证: dist/index.html 存在" -ForegroundColor Green
    Write-Host ""

    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  🎉 所有质量门禁通过！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan

} finally {
    Set-Location $OriginalDir
}
