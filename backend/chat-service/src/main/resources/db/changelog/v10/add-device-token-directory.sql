-- Upsert-only read mirror of employee-service's device_token table, fed by device-token.events —
-- same shape as employee_directory (v9). This is what FcmSenderService looks up tokens from for
-- subs with no live chat WebSocket session; employee-service remains the source of truth.

CREATE TABLE IF NOT EXISTS device_token_directory
(
    fcm_token varchar(500) NOT NULL,
    sub       varchar(200) NOT NULL,
    platform  varchar(20)  NOT NULL,
    PRIMARY KEY (fcm_token)
);

CREATE INDEX IF NOT EXISTS idx_device_token_directory_sub ON device_token_directory (sub);
