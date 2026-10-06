-- chat-service schema.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS conversation
(
    id              uuid              NOT NULL DEFAULT uuid_generate_v4(),
    type            character varying NOT NULL,
    name            character varying,
    is_private      boolean           NOT NULL DEFAULT true,
    last_message_at timestamp with time zone,
    last_message_id bigint,
    created_at      timestamp with time zone,
    created_by      jsonb,
    modified_at     timestamp with time zone,
    modified_by     jsonb,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS conversation_participant
(
    id                           uuid                     NOT NULL DEFAULT uuid_generate_v4(),
    conversation_id              uuid                     NOT NULL REFERENCES conversation (id) ON DELETE CASCADE,
    participant_sub              character varying        NOT NULL,
    role                         character varying        NOT NULL DEFAULT 'MEMBER',
    joined_at                    timestamp with time zone NOT NULL DEFAULT now(),
    last_read_at                 timestamp with time zone,
    last_read_message_id         bigint,
    delivered_through_message_id bigint,
    PRIMARY KEY (id),
    CONSTRAINT uq_conversation_participant UNIQUE (conversation_id, participant_sub)
);

CREATE INDEX IF NOT EXISTS idx_conversation_participant_sub ON conversation_participant (participant_sub);

CREATE TABLE IF NOT EXISTS message
(
    id                bigserial         NOT NULL,
    conversation_id   uuid              NOT NULL REFERENCES conversation (id) ON DELETE CASCADE,
    parent_message_id bigint            REFERENCES message (id) ON DELETE CASCADE, -- set on thread replies
    sender            jsonb             NOT NULL,
    content           text,
    message_type      character varying NOT NULL DEFAULT 'TEXT',
    attachments       jsonb,
    -- Full-text search over content; generated, so it never needs maintaining by hand.
    search_vector     tsvector GENERATED ALWAYS AS (to_tsvector('english', coalesce(content, ''))) STORED,
    created_at        timestamp with time zone,
    created_by        jsonb,
    modified_at       timestamp with time zone,
    modified_by       jsonb,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_message_conversation_id_id ON message (conversation_id, id DESC);
CREATE INDEX IF NOT EXISTS idx_message_search_vector ON message USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS idx_message_parent_message_id ON message (parent_message_id);

CREATE TABLE IF NOT EXISTS message_reaction
(
    id              uuid                     NOT NULL DEFAULT uuid_generate_v4(),
    message_id      bigint                   NOT NULL REFERENCES message (id) ON DELETE CASCADE,
    participant_sub character varying        NOT NULL,
    emoji           character varying        NOT NULL,
    created_at      timestamp with time zone NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    CONSTRAINT uq_message_reaction UNIQUE (message_id, participant_sub, emoji)
);

CREATE INDEX IF NOT EXISTS idx_message_reaction_message_id ON message_reaction (message_id);

CREATE TABLE IF NOT EXISTS user_presence
(
    sub          character varying NOT NULL,
    last_seen_at timestamp with time zone,
    PRIMARY KEY (sub)
);
