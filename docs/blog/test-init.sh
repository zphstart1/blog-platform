#!/bin/bash
# ============================================
# Blog 系统自动化测试 — 数据初始化脚本
# 用途: 测试执行前的环境准备和数据初始化
# 版本: v1.0
# 日期: 2026-06-12
# ============================================

set -e

# ---- 配置区 ----
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-root123}"
DB_NAME="${DB_NAME:-blog_test}"
REDIS_HOST="${REDIS_HOST:-localhost}"
REDIS_PORT="${REDIS_PORT:-6379}"
API_BASE="${API_BASE:-http://localhost:8080}"

echo "=========================================="
echo " Blog 系统测试环境初始化"
echo "=========================================="
echo "DB: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo "Redis: ${REDIS_HOST}:${REDIS_PORT}"
echo "API: ${API_BASE}"
echo "=========================================="

# ---- 1. 数据库初始化 ----
echo "[1/5] 初始化测试数据库..."

mysql -h"${DB_HOST}" -P"${DB_PORT}" -u"${DB_USER}" -p"${DB_PASS}" <<SQL
DROP DATABASE IF EXISTS ${DB_NAME};
CREATE DATABASE ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ${DB_NAME};

-- 导入 DDL 表结构 (由架构文档产出)
SOURCE ../blog-server/sql/schema.sql;

-- 导入测试数据
SOURCE test-data.sql;

-- 验证数据量
SELECT '--- 数据量验证 ---' AS '';
SELECT 'user' AS tbl, COUNT(*) AS cnt FROM user
UNION ALL SELECT 'category', COUNT(*) FROM category
UNION ALL SELECT 'tag', COUNT(*) FROM tag
UNION ALL SELECT 'article', COUNT(*) FROM article
UNION ALL SELECT 'article_tag', COUNT(*) FROM article_tag
UNION ALL SELECT 'comment', COUNT(*) FROM comment;
SQL

echo "[1/5] 数据库初始化完成"

# ---- 2. Redis 初始化 ----
echo "[2/5] 初始化 Redis..."

redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" <<REDIS
-- 清理测试环境所有缓存 key
FLUSHDB

-- 预置搜索热词 (用于集成测试)
ZADD bls:search:hot 100 "Spring Boot"
ZADD bls:search:hot 80 "Vue 3"
ZADD bls:search:hot 60 "MySQL"
ZADD bls:search:hot 50 "TypeScript"
ZADD bls:search:hot 40 "Docker"

-- 预置热门文章排行
ZADD bls:article:hot:top10 1520 "1"
ZADD bls:article:hot:top10 1350 "11"
ZADD bls:article:hot:top10 1300 "21"
ZADD bls:article:hot:top10 1200 "7"
ZADD bls:article:hot:top10 1120 "4"

-- 预置文章缓存 (Cache-Aside 模式)
SET bls:article:detail:spring-boot-3-quick-start '{"id":1,"title":"Spring Boot 3 快速入门指南"}' EX 3600
SET bls:article:list:page1:size10 '{"total":50,"page":1,"size":10}' EX 600

-- 验证
DBSIZE
REDIS

echo "[2/5] Redis 初始化完成"

# ---- 3. 启动后端服务 (如果未运行) ----
echo "[3/5] 检查后端服务..."

HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "${API_BASE}/api/health" 2>/dev/null || echo "000")

if [ "$HEALTH_CHECK" = "200" ]; then
    echo "[3/5] 后端服务已运行 (${API_BASE})"
else
    echo "[3/5] 后端服务未运行，请手动启动: cd blog-server && mvn spring-boot:run"
    echo "      或: docker-compose up -d api"
fi

# ---- 4. 注册测试账号并获取 Token ----
echo "[4/5] 获取测试 Token..."

# 博主账号
BLOGGER_TOKEN=$(curl -s -X POST "${API_BASE}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"blogger","password":"Test123456"}' | jq -r '.data.token // empty')

if [ -n "$BLOGGER_TOKEN" ]; then
    echo "  blogger Token: ${BLOGGER_TOKEN:0:30}..."
else
    echo "  [WARN] blogger 登录失败，请确认后端服务已启动"
fi

# 作者账号
AUTHOR_TOKEN=$(curl -s -X POST "${API_BASE}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"author_zhang","password":"Test123456"}' | jq -r '.data.token // empty')

# 注册用户账号
USER_TOKEN=$(curl -s -X POST "${API_BASE}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"user_alice","password":"Test123456"}' | jq -r '.data.token // empty')

# 写入环境变量文件供测试脚本使用
cat > test-env.sh <<ENV
#!/bin/bash
# 测试环境变量 (自动生成)
export TEST_BLOGGER_TOKEN="${BLOGGER_TOKEN}"
export TEST_AUTHOR_TOKEN="${AUTHOR_TOKEN}"
export TEST_USER_TOKEN="${USER_TOKEN}"
export TEST_API_BASE="${API_BASE}"
ENV

echo "[4/5] Token 获取完成 → 已写入 test-env.sh"

# ---- 5. 运行冒烟测试 ----
echo "[5/5] 运行冒烟测试..."

SMOKE_PASS=0
SMOKE_FAIL=0

# 公开接口冒烟
check_api() {
    local name="$1"
    local method="$2"
    local url="$3"
    local expected_code="$4"
    local actual_code=$(curl -s -o /dev/null -w "%{http_code}" -X "${method}" "${API_BASE}${url}" 2>/dev/null)
    if [ "$actual_code" = "$expected_code" ]; then
        echo "  [PASS] ${name} (${actual_code})"
        SMOKE_PASS=$((SMOKE_PASS + 1))
    else
        echo "  [FAIL] ${name} — expected ${expected_code}, got ${actual_code}"
        SMOKE_FAIL=$((SMOKE_FAIL + 1))
    fi
}

check_api "文章列表"    "GET"  "/api/articles"              "200"
check_api "文章详情"    "GET"  "/api/articles/spring-boot-3-quick-start" "200"
check_api "分类列表"    "GET"  "/api/categories"             "200"
check_api "标签列表"    "GET"  "/api/tags"                   "200"
check_api "搜索"        "GET"  "/api/search?keyword=Spring"  "200"
check_api "评论列表"    "GET"  "/api/articles/1/comments"    "200"
check_api "注册接口"    "POST" "/api/auth/register"          "400"  # 空参数应返回400
check_api "登录接口"    "POST" "/api/auth/login"             "400"  # 空参数应返回400

echo ""
echo "=========================================="
echo " 初始化完成"
echo " 冒烟测试: ${SMOKE_PASS} 通过 / ${SMOKE_FAIL} 失败"
echo "=========================================="
echo ""
echo "下一步: source test-env.sh && 执行自动化测试脚本"
