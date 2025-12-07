-- Создание таблицы для blacklisted tokens в Monolith
CREATE TABLE IF NOT EXISTS blacklisted_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(1000) NOT NULL UNIQUE,
    user_id UUID,
    blacklisted_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_blacklisted_tokens_token ON blacklisted_tokens(token);
CREATE INDEX IF NOT EXISTS idx_blacklisted_tokens_expires_at ON blacklisted_tokens(expires_at);

COMMENT ON TABLE blacklisted_tokens IS 'Хранение отозванных JWT access tokens';
COMMENT ON COLUMN blacklisted_tokens.token IS 'JWT access token который был отозван';
COMMENT ON COLUMN blacklisted_tokens.expires_at IS 'Время когда токен истечет (TTL из JWT)';
