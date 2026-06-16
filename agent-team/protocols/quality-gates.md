# 自动化质量门禁 (Quality Gates)

## 触发时机

每个阶段完成后自动执行，不通过则阻断流水线。

---

## 门禁定义

### Gate 1：代码 Lint

| 项 | 前端 | 后端 |
|----|------|------|
| 命令 | `vue-tsc --noEmit` | `mvn compile` |
| 通过标准 | 0 errors | BUILD SUCCESS |
| 不通过 | 打回开发，附 lint 报告 |

### Gate 2：构建验证

| 项 | 前端 | 后端 |
|----|------|------|
| 命令 | `npm run build` | `mvn package -DskipTests` |
| 通过标准 | 成功生成 dist/ + index.html | 成功生成 .jar |
| 不通过 | 打回开发，附构建日志 |

### Gate 3：测试覆盖率

| 项 | 标准 |
|----|------|
| 单元测试通过率 | 100% |
| 分支覆盖率 | ≥ 60% |
| 不通过 | 打回 senior-dev |

### Gate 4：浏览器验证

| 项 | 标准 |
|----|------|
| console error | = 0 |
| 页面渲染完整性 | 无空白区域 |
| 核心功能可用 | curl HTTP 200 |
| 不通过 | 打回对应 dev |

### Gate 5：部署健康检查

| 项 | 标准 |
|----|------|
| HTTP 状态码 | 200 |
| 响应时间 | < 2s |
| Docker 容器状态 | Up (healthy) |
| 日志检查 | 无 ERROR/Exception |

---

## 门禁执行顺序

```
Gate 1 (Lint) → Gate 2 (Build) → Gate 3 (Test) → Gate 4 (Browser) → Gate 5 (Health)
   ↓ 失败          ↓ 失败           ↓ 失败           ↓ 失败              ↓ 失败
  打回开发        打回开发         打回开发         打回开发           回滚 + 告警
```

## 角色分工

| Gate | 执行者 | 审核者 |
|------|--------|--------|
| Gate 1 | Dev (自检) | Team Lead |
| Gate 2 | DevOps | Team Lead |
| Gate 3 | Senior Dev / QA | Team Lead |
| Gate 4 | QA | Team Lead |
| Gate 5 | DevOps | Team Lead |

---

## 自动化脚本

### 前端质量检查 (`agent-team/scripts/lint-check.ps1`)
```powershell
param([string]$ProjectDir)

Write-Host "=== Gate 1: TypeScript 类型检查 ==="
cd "$ProjectDir"
$result = vue-tsc --noEmit 2>&1
if ($LASTEXITCODE -ne 0) { Write-Host "❌ 类型检查失败"; exit 1 }

Write-Host "=== Gate 2: 构建验证 ==="
npm run build 2>&1
if ($LASTEXITCODE -ne 0) { Write-Host "❌ 构建失败"; exit 1 }
if (!(Test-Path "dist/index.html")) { Write-Host "❌ 缺少 index.html"; exit 1 }

Write-Host "✅ 所有门禁通过"
```

### 后端质量检查 (`agent-team/scripts/backend-check.ps1`)
```powershell
param([string]$ProjectDir)

Write-Host "=== Gate 1: 编译验证 ==="
cd "$ProjectDir"
mvn compile 2>&1
if ($LASTEXITCODE -ne 0) { Write-Host "❌ 编译失败"; exit 1 }

Write-Host "=== Gate 2: 单元测试 ==="
mvn test 2>&1
if ($LASTEXITCODE -ne 0) { Write-Host "❌ 单元测试失败"; exit 1 }

Write-Host "=== Gate 2b: 打包验证 ==="
mvn package -DskipTests 2>&1
if ($LASTEXITCODE -ne 0) { Write-Host "❌ 打包失败"; exit 1 }

Write-Host "✅ 所有门禁通过"
```
