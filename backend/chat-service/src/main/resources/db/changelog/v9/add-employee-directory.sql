CREATE TABLE IF NOT EXISTS employee_directory
(
    sub         character varying NOT NULL,
    name        character varying,
    email       character varying,
    title       character varying,
    department  character varying,
    avatar_url  character varying,
    manager_sub character varying,
    PRIMARY KEY (sub)
);
