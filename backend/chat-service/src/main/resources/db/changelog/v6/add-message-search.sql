ALTER TABLE message
    ADD COLUMN IF NOT EXISTS search_vector tsvector
        GENERATED ALWAYS AS (to_tsvector('english', coalesce(content, ''))) STORED;

CREATE INDEX IF NOT EXISTS idx_message_search_vector ON message USING GIN (search_vector);
