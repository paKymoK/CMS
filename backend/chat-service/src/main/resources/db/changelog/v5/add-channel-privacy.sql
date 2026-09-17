ALTER TABLE conversation
    ADD COLUMN IF NOT EXISTS is_private boolean NOT NULL DEFAULT true;
