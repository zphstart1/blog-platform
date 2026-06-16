# 模拟阿里云 SLS 往 n8n 推一条告警，用来本机调试案例 10
# 用法：先在 n8n 里激活 Webhook(path=sls-alert)，再跑   .\test-alert.ps1

$body = @{
  service   = "order-svc"
  severity  = "P1"
  fire_time = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ssZ")
  query_url = "https://sls.console.aliyun.com/lognext/project/demo"
  samples   = @(
    @{ message = "java.lang.NullPointerException: Cannot invoke `"com.x.Order.getAmount()`" because `"order`" is null at com.x.OrderService.pay(OrderService.java:88)" }
  )
} | ConvertTo-Json -Depth 5

# 注意：测试环境用 Test URL（webhook-test/...）；激活后用 webhook/...
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:5678/webhook-test/sls-alert" `
  -ContentType "application/json" `
  -Headers @{ "X-Token" = "local-dev" } `
  -Body $body
