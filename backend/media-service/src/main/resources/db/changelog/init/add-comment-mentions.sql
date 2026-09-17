ALTER TABLE comment ADD COLUMN IF NOT EXISTS mentioned_subs text[] DEFAULT '{}';
