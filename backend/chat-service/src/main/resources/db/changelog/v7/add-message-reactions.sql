CREATE TABLE IF NOT EXISTS message_reaction
(
    id              uuid              NOT NULL DEFAULT uuid_generate_v4(),
    message_id      bigint            NOT NULL REFERENCES message (id) ON DELETE CASCADE,
    participant_sub character varying NOT NULL,
    emoji           character varying NOT NULL,
    created_at      timestamp with time zone NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    CONSTRAINT uq_message_reaction UNIQUE (message_id, participant_sub, emoji)
);

CREATE INDEX IF NOT EXISTS idx_message_reaction_message_id ON message_reaction (message_id);
