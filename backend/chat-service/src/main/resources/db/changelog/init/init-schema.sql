CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS conversation
(
    id          uuid              NOT NULL DEFAULT uuid_generate_v4(),
    type        character varying NOT NULL,
    name        character varying,
    created_at  timestamp with time zone,
    created_by  jsonb,
    modified_at timestamp with time zone,
    modified_by jsonb,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS conversation_participant
(
    id              uuid              NOT NULL DEFAULT uuid_generate_v4(),
    conversation_id uuid              NOT NULL REFERENCES conversation (id) ON DELETE CASCADE,
    participant_sub character varying NOT NULL,
    role            character varying NOT NULL DEFAULT 'MEMBER',
    joined_at       timestamp with time zone NOT NULL DEFAULT now(),
    last_read_at    timestamp with time zone,
    PRIMARY KEY (id),
    CONSTRAINT uq_conversation_participant UNIQUE (conversation_id, participant_sub)
);

CREATE INDEX IF NOT EXISTS idx_conversation_participant_sub ON conversation_participant (participant_sub);

CREATE TABLE IF NOT EXISTS message
(
    id              bigserial         NOT NULL,
    conversation_id uuid              NOT NULL REFERENCES conversation (id) ON DELETE CASCADE,
    sender          jsonb             NOT NULL,
    content         text,
    message_type    character varying NOT NULL DEFAULT 'TEXT',
    attachments     jsonb,
    created_at      timestamp with time zone,
    created_by      jsonb,
    modified_at     timestamp with time zone,
    modified_by     jsonb,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_message_conversation_id_id ON message (conversation_id, id DESC);
