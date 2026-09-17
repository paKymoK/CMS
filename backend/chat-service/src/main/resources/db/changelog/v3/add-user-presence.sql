CREATE TABLE IF NOT EXISTS user_presence
(
    sub          character varying        NOT NULL,
    last_seen_at timestamp with time zone,
    PRIMARY KEY (sub)
);
