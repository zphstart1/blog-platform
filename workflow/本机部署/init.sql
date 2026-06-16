-- 案例 10 的两张业务表，随 postgres 首次启动自动建（建在 n8n 库里，demo 够用）

-- 告警去重：同一个错误指纹在冷却窗口内只排查一次
CREATE TABLE IF NOT EXISTS alert_dedup (
  fingerprint TEXT PRIMARY KEY,
  last_seen   TIMESTAMPTZ,
  hit_count   INT DEFAULT 1
);

-- 排障审计：每次 AI 排查结果留底，便于事后评估准确率
CREATE TABLE IF NOT EXISTS diagnose_audit (
  id          BIGSERIAL PRIMARY KEY,
  fingerprint TEXT,
  service     TEXT,
  severity    TEXT,
  report      JSONB,
  created_at  TIMESTAMPTZ DEFAULT NOW()
);
