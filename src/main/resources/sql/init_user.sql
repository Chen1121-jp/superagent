-- 用户表
CREATE TABLE "user" (
    id              BIGINT          NOT NULL PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    nickname        VARCHAR(100)    NOT NULL DEFAULT '',
    email           VARCHAR(100)    NOT NULL DEFAULT '',
    phone           VARCHAR(20)     NOT NULL DEFAULT '',
    avatar_url      VARCHAR(500)    NOT NULL DEFAULT '',
    gender          SMALLINT        NOT NULL DEFAULT 0,
    role            VARCHAR(20)     NOT NULL DEFAULT 'user',
    status          VARCHAR(20)     NOT NULL DEFAULT 'active',
    last_login_at   TIMESTAMP,
    last_login_ip   VARCHAR(50),
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      SMALLINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX idx_user_username ON "user"(username) WHERE is_deleted = 0;

-- 测试数据（密码均为 123456）
INSERT INTO "user" (id, username, password_hash, nickname, email, role) VALUES
(1, 'admin',  '$2a$10$0YhyLFwcLztT0wfkoQ9bruqFYt8WShOP1aYUnJP5O3giX6mt7s7Ha', '管理员',   'admin@chen.com',  'admin'),
(2, 'vip',    '$2a$10$0YhyLFwcLztT0wfkoQ9bruqFYt8WShOP1aYUnJP5O3giX6mt7s7Ha', 'VIP用户',  'vip@chen.com',    'vip'),
(3, 'normal', '$2a$10$0YhyLFwcLztT0wfkoQ9bruqFYt8WShOP1aYUnJP5O3giX6mt7s7Ha', '普通用户', 'normal@chen.com', 'user');

-- 对话消息表
CREATE TABLE chat_message (
    id              BIGINT          NOT NULL PRIMARY KEY,
    conversation_id VARCHAR(64)     NOT NULL,
    user_id         BIGINT,
    role            VARCHAR(20)     NOT NULL,
    content         TEXT            NOT NULL,
    token_count     INT             DEFAULT 0,
    metadata        JSONB,
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      SMALLINT        NOT NULL DEFAULT 0
);

CREATE INDEX idx_msg_conv ON chat_message(conversation_id, create_time);
