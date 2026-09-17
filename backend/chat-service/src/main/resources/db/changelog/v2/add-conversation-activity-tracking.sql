ALTER TABLE conversation
    ADD COLUMN IF NOT EXISTS last_message_at timestamp with time zone,
    ADD COLUMN IF NOT EXISTS last_message_id bigint;

ALTER TABLE conversation_participant
    ADD COLUMN IF NOT EXISTS last_read_message_id bigint;
