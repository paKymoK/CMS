-- Mirrors D:\Code\Workflow\infrastructure\postgres\init-databases.sql's pattern: each
-- service's DB_SCHEMA env var is actually a separate Postgres DATABASE name, not a
-- schema within one shared database.
CREATE DATABASE authentication
    WITH OWNER = postgres ENCODING = 'UTF8' CONNECTION LIMIT = -1;

CREATE DATABASE media
    WITH OWNER = postgres ENCODING = 'UTF8' CONNECTION LIMIT = -1;

CREATE DATABASE chat
    WITH OWNER = postgres ENCODING = 'UTF8' CONNECTION LIMIT = -1;

CREATE DATABASE content
    WITH OWNER = postgres ENCODING = 'UTF8' CONNECTION LIMIT = -1;

GRANT ALL PRIVILEGES ON DATABASE authentication TO postgres;
GRANT ALL PRIVILEGES ON DATABASE media TO postgres;
GRANT ALL PRIVILEGES ON DATABASE chat TO postgres;
GRANT ALL PRIVILEGES ON DATABASE content TO postgres;
