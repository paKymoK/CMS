CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS upload_file
(
    id          uuid              NOT NULL DEFAULT uuid_generate_v4(),
    name        character varying NOT NULL,
    extension   character varying NOT NULL,
    created_at  timestamp with time zone,
    created_by  jsonb,
    modified_at timestamp with time zone,
    modified_by jsonb,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comment
(
    id          uuid    NOT NULL DEFAULT uuid_generate_v4(),
    commenter   jsonb   NOT NULL,
    content     text    NOT NULL,
    ticket_id   bigint  NOT NULL,
    is_edited   boolean NOT NULL DEFAULT false,
    created_at  timestamp with time zone,
    created_by  jsonb,
    modified_at timestamp with time zone,
    modified_by jsonb,
    PRIMARY KEY (id)
);

