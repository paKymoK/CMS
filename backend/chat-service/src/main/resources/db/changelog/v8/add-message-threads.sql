ALTER TABLE message
    ADD COLUMN IF NOT EXISTS parent_message_id bigint REFERENCES message (id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_message_parent_message_id ON message (parent_message_id);
