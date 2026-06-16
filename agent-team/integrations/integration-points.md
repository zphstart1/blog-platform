# 外部集成点抽象定义

> 团队产出的代码和文档是**可执行**的，但运行时依赖的外部中间件和基础设施由用户按需接入。
> 本章节将每个外部依赖抽象为「集成点」，定义清晰的接口契约，用户只需按模板填写自己的环境配置即可。

---

## 集成点总览

```
┌─────────────────────────────────────────────────────────────────┐
│                      用户个性化配置                               │
│                                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │   Git    │  │ Database │  │  Redis   │  │   MQ     │       │
│  │ Remote   │  │ (MySQL)  │  │          │  │ (Kafka)  │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
│       │              │              │              │             │
│  ┌────┴──────┬───────┴──────┬───────┴──────┬───────┴─────┐     │
│  │  Build    │  Registry   │   Deploy    │   Monitor   │     │
│  │ (Maven)   │  (Harbor)   │   (K8s/SSH) │   (Prom)    │     │
│  └────┬──────┴──────┬──────┴──────┬──────┴──────┬──────┘     │
│       │              │              │              │             │
│  ┌────┴──────────────┴──────────────┴──────────────┴─────┐     │
│  │                   Agent 研发团队                         │     │
│  │  产出：代码 · DDL · 部署脚本 · 监控配置 · 上线方案       │     │
│  │  前端：React/Vue/小程序（用户选择）                      │     │
│  └────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 集成点清单

| # | 集成点 | 依赖阶段 | 抽象接口 | 用户需提供 |
|---|--------|---------|---------|-----------|
| IP-01 | 版本控制 | 全部 | Git Remote | 仓库地址、认证方式 |
| IP-02 | 数据库 | 架构/开发/部署 | JDBC URL | 连接串、账号密码 |
| IP-03 | 缓存 | 架构/开发/部署 | Redis URL | 连接串、集群信息 |
| IP-04 | 消息队列 | 架构/开发 | MQ Endpoint | Broker 地址、Topic 前缀 |
| IP-05 | 构建服务 | 部署前 | Maven/Docker CLI | 构建环境路径、命令 |
| IP-06 | 镜像仓库 | 部署前 | Registry URL | 仓库地址、认证 |
| IP-07 | 部署目标 | 部署 | K8s/SSH | 集群/服务器信息 |
| IP-08 | 监控系统 | 部署/运维 | Prometheus Endpoint | 告警通道、面板地址 |
| IP-09 | 前端技术栈 | 开发 | 框架选择 | React / Vue / 小程序 |

---

## IP-01 版本控制 (VCS)

### 抽象描述
团队产出代码后需要推送到远程仓库、创建分支、发起 MR/PR。

### 契约接口

```
推送代码    →  git push <remote> <branch>
创建分支    →  git checkout -b <branch> && git push -u <remote> <branch>
查看状态    →  git status && git diff
```

### 用户配置模板

```yaml
vcs:
  provider: "gitlab"          # 可选: github | gitlab | gitee | 自建
  remote: "origin"
  defaultBranch: "main"
  repository: ""              # 如: git@github.com:user/repo.git
  auth:
    type: "ssh"               # 可选: ssh | https-token
    sshKey: "~/.ssh/id_rsa"   # type=ssh 时必填
    token: ""                 # type=https-token 时必填
  branchStrategy:
    featurePrefix: "feature/"
    bugfixPrefix: "bugfix/"
    releasePrefix: "release/"
```

---

## IP-02 数据库 (Database)

### 抽象描述
架构师设计表结构后产出 DDL；开发完成后需要连接数据库执行 DDL、验证表结构。

### 契约接口

```
执行 DDL      →  mysql -h <host> -P <port> -u <user> -p < db.sql
验证表结构    →  SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='xxx'
连接测试      →  mysqladmin ping -h <host> -P <port> -u <user> -p
```

### 用户配置模板

```yaml
database:
  provider: "mysql"           # 可选: mysql | postgresql | supabase | cloudbase
  host: ""                    # 如: 127.0.0.1 或 rds.xxx.com
  port: 3306
  database: ""                # 数据库名
  username: ""
  password: ""
  charset: "utf8mb4"
  pool:
    minIdle: 10
    maxActive: 50
  # --- Supabase 专用 ---
  supabase:
    projectRef: ""
    anonKey: ""
  # --- CloudBase 专用 ---
  cloudbase:
    envId: ""
```

---

## IP-03 缓存 (Cache)

### 抽象描述
架构师设计缓存策略（Key 命名、过期时间），开发人员按设计实现；部署时需要连接 Redis。

### 契约接口

```
连接验证      →  redis-cli -h <host> -p <port> PING
Key 前缀检查  →  redis-cli KEYS "<prefix>*"
缓存清理      →  redis-cli DEL <key-pattern>
```

### 用户配置模板

```yaml
cache:
  provider: "redis"           # 可选: redis | redis-cluster | codis
  mode: "standalone"          # standalone | cluster | sentinel
  standalone:
    host: ""
    port: 6379
    password: ""
    database: 0
  cluster:
    nodes:
      - host: ""
        port: 6379
    password: ""
  pool:
    maxTotal: 30
    maxIdle: 10
    minIdle: 5
```

---

## IP-04 消息队列 (MQ)

### 抽象描述
如有异步/解耦需求，架构师设计消息模型，开发人员实现生产者和消费者。

### 契约接口

```
发送消息      →  生产者 API
消费消息      →  消费者 API
Topic 列表    →  管理端 CLI / API
```

### 用户配置模板

```yaml
mq:
  provider: "kafka"           # 可选: kafka | rocketmq | rabbitmq | none
  bootstrap:
    - "localhost:9092"
  producer:
    topicPrefix: ""           # 自动拼接: {prefix}_topicName
    acks: "all"
  consumer:
    groupId: ""
    autoOffsetReset: "earliest"
  # --- RocketMQ 专用 ---
  rocketmq:
    nameServer: ""
    producerGroup: ""
  # --- RabbitMQ 专用 ---
  rabbitmq:
    host: ""
    port: 5672
    username: ""
    password: ""
    virtualHost: "/"
```

---

## IP-05 构建服务 (Build)

### 抽象描述
代码完成后需要编译、打包、生成制品（JAR/WAR/Docker Image）。

### 契约接口

```
编译打包      →  mvn clean package -DskipTests
单元测试      →  mvn test
构建镜像      →  docker build -t <image> .
运行检查      →  mvn versions:display-dependency-updates
```

### 用户配置模板

```yaml
build:
  provider: "maven"           # 可选: maven | gradle | docker-compose
  maven:
    home: ""                  # Maven 安装路径，留空使用系统默认
    settingsFile: ""          # settings.xml 路径（私有仓库认证等）
    jdk: "11"                 # JDK 版本
    options: ""               # 额外参数，如: -T 4C
  docker:
    dockerfile: "Dockerfile"
    buildArgs:
      - "JAR_FILE=target/*.jar"
  artifact:
    type: "jar"               # jar | war | docker
    outputDir: "target/"
```

---

## IP-06 镜像仓库 (Registry)

### 抽象描述
Docker 镜像构建后需要推送到镜像仓库供部署拉取。

### 契约接口

```
推送镜像      →  docker push <registry>/<image>:<tag>
拉取镜像      →  docker pull <registry>/<image>:<tag>
列出标签      →  查询 Registry API
```

### 用户配置模板

```yaml
registry:
  provider: "dockerhub"       # 可选: dockerhub | harbor | acr | tcr
  url: ""                     # registry 地址，如: harbor.company.com
  namespace: ""               # 命名空间/项目名
  auth:
    username: ""
    password: ""
  image:
    name: ""                  # 镜像名
    tag: "latest"
```

---

## IP-07 部署目标 (Deploy)

### 抽象描述
构建完成的制品需要部署到目标环境。

### 契约接口

```
部署服务      →  kubectl apply -f deployment.yaml
滚动更新      →  kubectl rollout restart deployment/<name>
查看状态      →  kubectl get pods -l app=<name>
回滚         →  kubectl rollout undo deployment/<name>
健康检查      →  curl -f http://<host>:<port>/actuator/health
```

### 用户配置模板

```yaml
deploy:
  provider: "k8s"             # 可选: k8s | docker | ssh | cloudstudio | lighthouse
  strategy: "rolling"         # rolling | recreate | blue-green
  # --- K8s 模式 ---
  k8s:
    kubeconfig: "~/.kube/config"
    namespace: "default"
    context: ""
  # --- SSH 模式 ---
  ssh:
    host: ""
    port: 22
    username: ""
    keyFile: "~/.ssh/id_rsa"
    deployDir: "/opt/app"
  # --- Cloud Studio 模式 ---
  cloudStudio:
    environment: ""
  # --- Lighthouse 模式 ---
  lighthouse:
    instanceId: ""
    region: ""
  replicas: 2
  healthCheck:
    path: "/actuator/health"
    initialDelay: 30
    period: 10
```

---

## IP-08 监控系统 (Monitor)

### 抽象描述
服务上线后需要接入监控告警，确保运行状态可观测。

### 契约接口

```
指标暴露      →  /actuator/prometheus（Spring Boot Actuator）
日志收集      →  输出到标准输出，由日志采集 Agent 收集
告警通知      →  Webhook / 邮件 / 短信 / 企微/钉钉
```

### 用户配置模板

```yaml
monitor:
  provider: "prometheus"      # 可选: prometheus | grafana | none
  prometheus:
    endpoint: "http://localhost:9090"
    scrapeInterval: "15s"
  grafana:
    url: ""
    dashboardUid: ""
  alert:
    channels:
      - type: "webhook"
        url: ""               # 企微/钉钉机器人 webhook
      - type: "email"
        to: ""
  log:
    collector: "elasticsearch"  # elasticsearch | loki | file
    endpoint: ""
```

---

## IP-09 前端技术栈 (Frontend Stack)

### 抽象描述
前端开发需要确定使用的框架、语言、UI 库等技术选型。

### 契约接口

```
创建项目      →  npm create vite@latest 或 vue create
安装依赖      →  npm install
开发运行      →  npm run dev
生产构建      →  npm run build
```

### 用户配置模板

```yaml
frontend:
  framework: "react"          # 可选: react | vue | miniprogram
  language: "typescript"      # 可选: typescript | javascript
  # --- React 专用 ---
  react:
    stateManagement: "zustand"  # zustand | redux | jotai
    uiLibrary: "antd"           # antd | arco-design | none
    router: "react-router"
  # --- Vue 专用 ---
  vue:
    stateManagement: "pinia"    # pinia | vuex
    uiLibrary: "element-plus"   # element-plus | naive-ui | none
    router: "vue-router"
  # --- 小程序专用 ---
  miniprogram:
    platform: "wechat"          # wechat
    uiLibrary: "tdesign"        # tdesign | weui
```

---

## 接入方式

用户在项目根目录创建 `integrations/user-config.yml`，按以上模板填写自己的环境信息。开发、部署阶段，团队成员会读取此配置生成对应的脚本和连接代码。

```bash
# 目录结构
integrations/
├── integration-points.md     # 本文档：集成点定义（只读）
├── user-config.yml           # 【用户填写】个性化配置
└── examples/                 # 各环境配置示例
    ├── config-local.yml      # 本地开发环境
    ├── config-cloud.yml      # 云服务环境（Supabase + K8s）
    └── config-legacy.yml     # 传统部署（SSH + 自建MySQL）
```
