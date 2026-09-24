-- 'faq' is the first (and, for now, only) content type on the generic content_item path — see
-- FaqFields.java. field_schema is descriptive metadata for admin-app's dynamic form only; actual
-- validation is FaqFields's @NotBlank constraints, enforced server-side in ContentItemMapper.
INSERT INTO content_type (key, label, field_schema)
VALUES ('faq', 'FAQ',
        '{"question": "string", "answer": "textarea"}'::jsonb)
ON CONFLICT (key) DO NOTHING;
