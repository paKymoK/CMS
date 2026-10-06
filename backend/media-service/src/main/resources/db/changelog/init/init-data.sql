-- media-service initial data: the seed media library for site 'en' — the images the seeded
-- homepage / posts / case studies reference — registered under fixed (name-based, uuid v5) ids, so
-- content-service's init-data can point at them by origin-less URL (/media-service/images/<id><ext>)
-- identically in every environment.
--
-- The files themselves are copied into storage.images-dir at startup by SeedImageInstaller, from
-- src/main/resources/seed-images/<id><ext>; set media.seed-images.enabled=false in an environment
-- that should not carry seed data (and drop this file's rows with it).

INSERT INTO upload_file (id, name, extension, created_at, created_by, modified_at, modified_by, site_id)
VALUES
       ('5237a74e-adf6-5ccf-ad9b-43bc8be17680', 'award-1.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('f93ca49b-06b3-5103-8a91-6cbdd25f2eb5', 'award-2.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('22d6810f-169d-5d72-8b15-c2c0244c661d', 'award-3.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('dc7e4f60-7000-5105-ae05-bcb3faee39ec', 'award-4.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('26849312-5779-5cb4-ba54-47f27b57c86e', 'award-5.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('f193eedf-82c6-544a-a03a-1e4c4753711e', 'cs-1-redcan.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('89705d24-d075-5d3e-a945-d4caf115a1ef', 'cs-2-ai-energy.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('598d8c53-00aa-51a1-9c9f-b5bc5a8aafc2', 'cs-3-smart-assistant.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('2cd9e6d6-29ed-5836-a255-0df42c96604c', 'cs-4-envato.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('b88204c9-390f-5b1d-86dc-6fe458b167bf', 'cert-9001.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('9073dc4b-2da9-564b-9a6e-7ecd53bfd4a2', 'cert-aws-tier.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('6d90b65d-c1a1-5da0-8d5b-01a32beb3071', 'cert-iso27001.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('ea369b38-0e2a-57fd-bae7-64e4da13fd59', 'cert-itil.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('80fcaea0-1b7f-5ed2-a593-28ecd12dfde1', 'cert-pcidss.svg', '.svg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('249edde3-116e-551f-a8c5-c51ebc320264', 'hero-team.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('cc6d4e10-5da6-5020-9de4-e5effc9d51de', 'news-apec-workshop.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('b51a01dd-ccf7-53f5-b6aa-31b63a6628cb', 'news-nz-partnership.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('75239b3d-8291-54ba-8a9d-f42614038105', 'news-2.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('31d94776-c11b-5fb6-a5d2-f431a143dbf2', 'nz-partnership.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('1fc2bdd1-3a47-575d-9e2c-1335a584f408', 'partner-aws.svg', '.svg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('adccd9b4-0f69-5a4e-9f3f-5c748a2778d5', 'partner-google-cloud.webp', '.webp', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('07747331-7981-5bca-933a-88482dbc0eb0', 'partner-istqb.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('9da4207a-03ab-5d02-9078-dca1638331ac', 'partner-itil.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('fc905c4f-0f16-5f1f-94a0-5dda8d926e1f', 'partner-uipath.png', '.png', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('2574eac9-a2eb-53b2-8f68-4d92fe1cb693', 'ai-digital-strategy.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('3ce83b57-2093-5948-9b9c-a8494c0f16c1', 'cybersecurity.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('c9b37ed5-2066-5eb5-ad97-c65ee570f169', 'data-ai-solutions.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('f51d0009-870c-5977-981a-c3363a34c43d', 'digital-cloud-solutions.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('46be6f5f-5307-529c-b232-340bd10160e6', 'enterprise-solutions.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('c669401a-2b4c-5209-a122-c189cb3853b1', 'infrastructure-managed-services.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('411d3736-e1cc-50df-86ec-b7c288743f1f', 'software-engineering.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('5fdcd7eb-90e6-5cd6-aee4-0f24b33e3893', 'elena-rojas.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('6a74dbb9-1f24-54bb-a8ea-8b8428c0276c', 'hiroshi-tanaka.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('df05f992-0541-5566-89ec-b1f12fee908f', 'jun-park.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en'),
       ('c63d3ad1-e521-58a4-8cc5-40867140b712', 'nick-benjamin.jpg', '.jpg', now(), '{"sub": "seed"}'::jsonb, now(), '{"sub": "seed"}'::jsonb, 'en')
ON CONFLICT (id) DO NOTHING;
