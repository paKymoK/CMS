CREATE TABLE IF NOT EXISTS oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization_consent
(
    registered_client_id varchar(100)  NOT NULL,
    principal_name       varchar(200)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization
(
    id                            varchar(100) NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    text          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      text          DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    authorization_code_metadata   text          DEFAULT NULL,
    access_token_value            text          DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         text          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           text          DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    oidc_id_token_metadata        text          DEFAULT NULL,
    refresh_token_value           text          DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    refresh_token_metadata        text          DEFAULT NULL,
    user_code_value               text          DEFAULT NULL,
    user_code_issued_at           timestamp     DEFAULT NULL,
    user_code_expires_at          timestamp     DEFAULT NULL,
    user_code_metadata            text          DEFAULT NULL,
    device_code_value             text          DEFAULT NULL,
    device_code_issued_at         timestamp     DEFAULT NULL,
    device_code_expires_at        timestamp     DEFAULT NULL,
    device_code_metadata          text          DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(256) NOT NULL,
    enabled  BOOLEAN      NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username),
    CONSTRAINT ix_auth_username UNIQUE (username, authority)
);

-- Department/unit are auth-service-owned catalog tables (Phase 7) — they rarely change, so keeping
-- them alongside identity data avoids a second hop to employee-service. employee-service keeps a
-- read-only Kafka-fed mirror of these two tables under the same names.
CREATE TABLE IF NOT EXISTS department
(
    id       BIGSERIAL    NOT NULL,
    name     VARCHAR(200) NOT NULL,
    head     VARCHAR(200),
    location VARCHAR(200),
    CONSTRAINT department_pkey PRIMARY KEY (id),
    CONSTRAINT department_name_uq UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS unit
(
    id            BIGSERIAL    NOT NULL,
    name          VARCHAR(200) NOT NULL,
    department_id BIGINT       NOT NULL,
    head          VARCHAR(200),
    location      VARCHAR(200),
    CONSTRAINT unit_pkey PRIMARY KEY (id),
    CONSTRAINT fk_unit_department FOREIGN KEY (department_id) REFERENCES department (id),
    CONSTRAINT unit_department_name_uq UNIQUE (department_id, name)
);

-- Full column parity with employee-service's `employee` (General) table (Phase 7) — only
-- name/email/department_id/unit_id have real read/write logic; everything else is an inert
-- placeholder until a future HR-editing frontend exists, kept now so that frontend doesn't need a
-- second disruptive migration. employee-service remains authoritative for the placeholder fields.
CREATE TABLE IF NOT EXISTS userinfo
(
    sub           VARCHAR(50) NOT NULL,
    name          VARCHAR,
    email         VARCHAR,
    department_id BIGINT,
    unit_id       BIGINT,
    title         VARCHAR(200),
    work_location VARCHAR(200),
    manager_sub   VARCHAR(50),
    joined_date   DATE,
    shift         VARCHAR(50),
    shift_hours   VARCHAR(100),
    status        VARCHAR(20),
    avatar_url    VARCHAR(500),
    CONSTRAINT userinfo_pkey PRIMARY KEY (sub),
    CONSTRAINT fk_userinfo_users FOREIGN KEY (sub) REFERENCES users (username) ON DELETE CASCADE,
    CONSTRAINT fk_userinfo_department FOREIGN KEY (department_id) REFERENCES department (id),
    CONSTRAINT fk_userinfo_unit FOREIGN KEY (unit_id) REFERENCES unit (id),
    CONSTRAINT fk_userinfo_manager_sub FOREIGN KEY (manager_sub) REFERENCES userinfo (sub)
);

CREATE TABLE user_group (
                            id          VARCHAR(50)  NOT NULL,
                            name        VARCHAR(100) NOT NULL,
                            description VARCHAR(200),
                            CONSTRAINT user_group_pkey    PRIMARY KEY (id),
                            CONSTRAINT user_group_name_uq UNIQUE (name)
);

-- Group membership and role assignment back Spring Security's own `users` table, not `userinfo` —
-- they're authorization data, unrelated to whether a Userinfo/HR record exists for that sub (see
-- Phase 7: Group was deliberately kept independent of the employee-data cutover).
CREATE TABLE user_group_member (
                                   group_id VARCHAR(50) NOT NULL,
                                   user_sub VARCHAR(50) NOT NULL,
                                   CONSTRAINT user_group_member_pkey PRIMARY KEY (group_id, user_sub),
                                   CONSTRAINT fk_ugm_group FOREIGN KEY (group_id) REFERENCES user_group(id)  ON DELETE CASCADE,
                                   CONSTRAINT fk_ugm_user  FOREIGN KEY (user_sub)  REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE client_role_assignment (
                                        id                   VARCHAR(50)  NOT NULL,
                                        registered_client_id VARCHAR(100) NOT NULL,
                                        user_sub             VARCHAR(50),
                                        group_id             VARCHAR(50),
                                        project_id           VARCHAR(50),
                                        role                 VARCHAR(50)  NOT NULL,
                                        CONSTRAINT client_role_assignment_pkey PRIMARY KEY (id),
                                        CONSTRAINT cra_exactly_one CHECK (
                                            (user_sub IS NULL AND group_id IS NOT NULL)
                                                OR
                                            (user_sub IS NOT NULL AND group_id IS NULL)
                                            ),
                                        CONSTRAINT fk_cra_client FOREIGN KEY (registered_client_id) REFERENCES oauth2_registered_client(id) ON DELETE CASCADE,
                                        CONSTRAINT fk_cra_user   FOREIGN KEY (user_sub)             REFERENCES users(username)              ON DELETE CASCADE,
                                        CONSTRAINT fk_cra_group  FOREIGN KEY (group_id)             REFERENCES user_group(id)               ON DELETE CASCADE
);

CREATE TABLE client_session_policy (
                                       registered_client_id VARCHAR(100) NOT NULL,
                                       single_tab           BOOLEAN      NOT NULL DEFAULT FALSE,
                                       fail_open            BOOLEAN      NOT NULL DEFAULT TRUE,
                                       CONSTRAINT client_session_policy_pkey PRIMARY KEY (registered_client_id),
                                       CONSTRAINT fk_csp_client FOREIGN KEY (registered_client_id)
                                           REFERENCES oauth2_registered_client(id) ON DELETE CASCADE
);

-- Roles are project-scoped: one role per (client, user, project) / (client, group, project).
-- COALESCE keeps the global (project_id IS NULL) row unique too, since Postgres treats NULLs
-- as distinct in a plain unique index.
CREATE UNIQUE INDEX ux_cra_client_user_project  ON client_role_assignment(registered_client_id, user_sub, COALESCE(project_id, ''))  WHERE user_sub  IS NOT NULL;
CREATE UNIQUE INDEX ux_cra_client_group_project ON client_role_assignment(registered_client_id, group_id, COALESCE(project_id, '')) WHERE group_id IS NOT NULL;

CREATE INDEX ix_ugm_user_sub           ON user_group_member(user_sub);
CREATE INDEX ix_cra_client_user_sub    ON client_role_assignment(registered_client_id, user_sub);
CREATE INDEX ix_cra_client_group_id    ON client_role_assignment(registered_client_id, group_id);

CREATE INDEX IF NOT EXISTS ix_userinfo_manager_sub ON userinfo (manager_sub);
CREATE INDEX IF NOT EXISTS ix_userinfo_department_id ON userinfo (department_id);
CREATE INDEX IF NOT EXISTS ix_userinfo_unit_id ON userinfo (unit_id);

CREATE INDEX IF NOT EXISTS ix_oa_client_principal ON oauth2_authorization(registered_client_id, principal_name);
CREATE INDEX IF NOT EXISTS ix_oa_access_token     ON oauth2_authorization USING HASH (access_token_value);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";