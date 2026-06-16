# 案例 10 本机部署（最小可跑）

本机阶段做减法：**SLS 用 curl 模拟**（入站 webhook 本机没公网 IP 接不了），
**钉钉照常能发**（n8n 主动出网调用钉钉公网 API，不受本机影响）。

## 一、启动

```powershell
cd C:\Users\26793\Desktop\study\workflow\本机部署
Copy-Item .env.example .env      # 然后编辑 .env 填钉钉 token/secret
docker compose up -d
```

打开 http://localhost:5678 ，首次进入设置管理员账号。

## 二、在 n8n 里准备

1. **建 Anthropic 凭据**：Credentials → New → Anthropic，填 API Key。
2. **建 Postgres 凭据**：Host=`postgres` Port=`5432` DB=`n8n` User=`n8n` Pass=`n8n`
   （容器内互相用服务名访问；表已由 init.sql 建好）。
3. 照「案例 10」逐个节点搭工作流。Webhook 节点 path 填 `sls-alert`。

## 三、分三步跑通（别想一口气全搭完）

- **第 1 步｜主链路**：只搭 `Webhook → Code(解析) → AI Agent → Code(拼报表+加签) → HTTP(钉钉)`。
  先不接代码仓库工具，让 AI 仅凭堆栈给个初步判断 —— 验证「告警进来 → 钉钉收到报表」。
- **第 2 步｜接仓库工具**：给 AI Agent 挂上 search_code / read_file / list_commits 三个
  HTTP Request Tool。本机可先指向一个**公开 GitHub 仓库**练手（先不接公司私有库）。
- **第 3 步｜去重 + 审计 + Jira**：补上 Postgres 去重、写审计、IF→Jira。

## 四、触发测试

n8n 画布里点 Webhook 节点 →「Listen for test event」，再跑：

```powershell
.\test-alert.ps1
```

预期：钉钉测试群收到一条「原因 + 修复方案」markdown 报表。
（激活工作流后，把 test-alert.ps1 里的 `webhook-test` 改成 `webhook`。）

## 五、停止 / 清理

```powershell
docker compose down          # 停止，保留数据
docker compose down -v       # 连数据卷一起删（重置）
```
