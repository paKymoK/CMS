-- ============================================================
-- Seed: IT Support team (LDAP shadow accounts)
-- sub  = LDAP username
-- JDBC login disabled (authenticated via LDAP only)
-- ============================================================

-- ── Spring Security accounts ──────────────────────────────────

INSERT INTO users (username, password, enabled) VALUES
  ('btngoc2',  '{noop}locked', false),
  ('nmngoc5',  '{noop}locked', false),
  ('nttdung9', '{noop}locked', false),
  ('dvtuan4',  '{noop}locked', false),
  ('ldduc',    '{noop}locked', false),
  ('ndtoi',    '{noop}locked', false),
  ('nmhoang6', '{noop}locked', false),
  ('ntnghia4', '{noop}locked', false),
  ('nvtuan16', '{noop}locked', false),
  ('nxmanh1',  '{noop}locked', false),
  ('phanh5',   '{noop}locked', false),
  ('ptduong1', '{noop}locked', false),
  ('txdat2',   '{noop}locked', false),
  ('nvtu3',    '{noop}locked', false),
  ('natuan25', '{noop}locked', false),
  ('tqthai',   '{noop}locked', false),
  ('ndtu24',   '{noop}locked', false);

-- ── Authorities ───────────────────────────────────────────────

INSERT INTO authorities (username, authority) VALUES
  ('btngoc2',  'ROLE_USER'),
  ('nmngoc5',  'ROLE_USER'),
  ('nttdung9', 'ROLE_USER'),
  ('dvtuan4',  'ROLE_USER'),
  ('ldduc',    'ROLE_USER'),
  ('ndtoi',    'ROLE_USER'),
  ('nmhoang6', 'ROLE_USER'),
  ('ntnghia4', 'ROLE_USER'),
  ('nvtuan16', 'ROLE_USER'),
  ('nxmanh1',  'ROLE_USER'),
  ('phanh5',   'ROLE_USER'),
  ('ptduong1', 'ROLE_USER'),
  ('txdat2',   'ROLE_USER'),
  ('nvtu3',    'ROLE_USER'),
  ('natuan25', 'ROLE_USER'),
  ('tqthai',   'ROLE_USER'),
  ('ndtu24',   'ROLE_USER');

-- ── Department catalog (Phase 7: auth-service-owned) ────────────

INSERT INTO department (name) VALUES ('IT Support');

-- ── User profiles ─────────────────────────────────────────────
-- Manager first (L1/L2/L3 reference ndtu24 as manager_sub)

INSERT INTO userinfo (sub, name, email, title, department_id, manager_sub, avatar_url) VALUES
  ('ndtu24',
   'Nguyen Duc Tu',
   'ndtu24@mycompany.local',
   'IT Service Manager',
   (SELECT id FROM department WHERE name = 'IT Support'),
   NULL,
   'https://api.dicebear.com/9.x/adventurer/svg?seed=ndtu24');

-- L3
INSERT INTO userinfo (sub, name, email, title, department_id, manager_sub, avatar_url) VALUES
  ('tqthai',
   'Truong Quang Thai',
   'tqthai@mycompany.local',
   'Senior IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=tqthai');

-- L2
INSERT INTO userinfo (sub, name, email, title, department_id, manager_sub, avatar_url) VALUES
  ('nvtu3',
   'Nguyen Van Tu',
   'nvtu3@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=nvtu3'),

  ('natuan25',
   'Nguyen Anh Tuan',
   'natuan25@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=natuan25');

-- L1
INSERT INTO userinfo (sub, name, email, title, department_id, manager_sub, avatar_url) VALUES
  ('btngoc2',
   'Bui Thuy Ngoc',
   'btngoc2@mycompany.local',
   'IT Support Analyst',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=btngoc2'),

  ('nmngoc5',
   'Nguyen Minh Ngoc',
   'nmngoc5@mycompany.local',
   'IT Support Analyst',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=nmngoc5'),

  ('nttdung9',
   'Nguyen Thi Thuy Dung',
   'nttdung9@mycompany.local',
   'IT Support Analyst',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=nttdung9'),

  ('dvtuan4',
   'Do Van Tuan',
   'dvtuan4@mycompany.local',
   'IT Support Analyst',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=dvtuan4'),

  ('ldduc',
   'Le Dinh Duc',
   'ldduc@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=ldduc'),

  ('ndtoi',
   'Nguyen Dinh Toi',
   'ndtoi@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=ndtoi'),

  ('nmhoang6',
   'Nguyen Minh Hoang',
   'nmhoang6@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=nmhoang6'),

  ('ntnghia4',
   'Nguyen Tuan Nghia',
   'ntnghia4@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=ntnghia4'),

  ('nvtuan16',
   'Nguyen Vi Tuan',
   'nvtuan16@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=nvtuan16'),

  ('nxmanh1',
   'Nguyen Xuan Manh',
   'nxmanh1@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=nxmanh1'),

  ('phanh5',
   'Pham Hoang Anh',
   'phanh5@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=phanh5'),

  ('ptduong1',
   'Pham Tung Duong',
   'ptduong1@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=ptduong1'),

  ('txdat2',
   'Tran Xuan Dat',
   'txdat2@mycompany.local',
   'IT Support Engineer',
   (SELECT id FROM department WHERE name = 'IT Support'),
   'ndtu24',
   'https://api.dicebear.com/9.x/adventurer/svg?seed=txdat2');

-- ── Groups ────────────────────────────────────────────────────

INSERT INTO user_group (id, name, description) VALUES
  ('grp-l1-support',      'L1 Support',      'First-line IT support analysts'),
  ('grp-l2-support',      'L2 Support',      'Second-line IT support engineers'),
  ('grp-l3-support',      'L3 Support',      'Third-line senior IT support engineers'),
  ('grp-service-manager', 'Service Manager', 'IT service managers');

-- ── Group memberships ─────────────────────────────────────────

INSERT INTO user_group_member (group_id, user_sub) VALUES
  ('grp-l1-support', 'btngoc2'),
  ('grp-l1-support', 'nmngoc5'),
  ('grp-l1-support', 'nttdung9'),
  ('grp-l1-support', 'dvtuan4');

INSERT INTO user_group_member (group_id, user_sub) VALUES
  ('grp-l2-support', 'ldduc'),
  ('grp-l2-support', 'ndtoi'),
  ('grp-l2-support', 'nmhoang6'),
  ('grp-l2-support', 'ntnghia4'),
  ('grp-l2-support', 'nvtuan16'),
  ('grp-l2-support', 'nxmanh1'),
  ('grp-l2-support', 'phanh5'),
  ('grp-l2-support', 'ptduong1'),
  ('grp-l2-support', 'txdat2'),
  ('grp-l2-support', 'nvtu3'),
  ('grp-l2-support', 'natuan25');

INSERT INTO user_group_member (group_id, user_sub) VALUES
  ('grp-l3-support', 'tqthai');

INSERT INTO user_group_member (group_id, user_sub) VALUES
  ('grp-service-manager', 'ndtu24');
