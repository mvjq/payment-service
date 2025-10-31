CREATE TABLE webhooks
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    url         VARCHAR(500) NOT NULL,
    description VARCHAR(255),
    secret      VARCHAR(255),
    is_active   BOOLEAN      NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE INDEX idx_webhooks_is_active ON webhooks (is_active);

CREATE TABLE payments
(
    id                    UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    webhook_id            UUID         NOT NULL,
    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100) NOT NULL,
    zip_code              VARCHAR(20)  NOT NULL,
    encrypted_card_number TEXT         NOT NULL,
    amount                DECIMAL(10, 2),
    currency              VARCHAR(3),
    status                VARCHAR(20),
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP    NOT NULL,
    CONSTRAINT fk_payment_webhook FOREIGN KEY (webhook_id) REFERENCES webhooks (id) ON DELETE RESTRICT
);

CREATE INDEX idx_payments_webhook_id ON payments (webhook_id);

CREATE TABLE outbox_events
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    payment_id     UUID         NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    webhook_id     UUID,
    payload        JSONB        NOT NULL,
    published      BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at   TIMESTAMP,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    error_message  TEXT
);
