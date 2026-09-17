ALTER TABLE conversation_participant
    ADD COLUMN IF NOT EXISTS delivered_through_message_id bigint;
