-- content-service initial data: the tenant registry, the content-type registry, and the 'en'
-- homepage / Insights / Case Study content, so a fresh environment stands up with a complete site
-- instead of empty tables.
--
-- site_id is always resolved by site code, never hardcoded. Posts and case studies carry SAMPLE copy
-- (clearly marked 'Editors: replace this sample copy') so the detail pages render end to end — it is
-- generic on purpose, with no invented people, quotes or figures beyond what each title already
-- states, and is meant to be replaced by that region's own editors.
--
-- Images are origin-less media-service paths (/media-service/images/<id><ext>). media-service's own
-- init-data registers those ids and bundles the files, so no host is stored here and the data is
-- identical in every environment. admin-app and the website resolve them against their own gateway.
--
-- Only 'en' is seeded; vi/ja/ko/de are registered as sites but start empty.

-- ── Sites ──────────────────────────────────────────────────────────────────────────────────────
-- Rollout order per the decision log: en + vi first, ja/ko/de land later. All 5 are
-- registered from day one since site_id isn't hardcoded to any particular value —
-- rollout is a sequencing/staffing choice, not a schema one.
INSERT INTO site (code, subdomain)
VALUES ('en', 'en.cmcglobal.com'),
       ('vi', 'vn.cmcglobal.com'),
       ('ja', 'jp.cmcglobal.com'),
       ('ko', 'kr.cmcglobal.com'),
       ('de', 'de.cmcglobal.com')
ON CONFLICT (code) DO NOTHING;

-- ── Content types ──────────────────────────────────────────────────────────────────────────────
-- 'faq' is the first (and, for now, only) content type on the generic content_item path — see
-- FaqFields.java. field_schema is descriptive metadata for admin-app's dynamic form only; actual
-- validation is FaqFields's @NotBlank constraints, enforced server-side in ContentItemMapper.
INSERT INTO content_type (key, label, field_schema)
VALUES ('faq', 'FAQ',
        '{"question": "string", "answer": "textarea"}'::jsonb)
ON CONFLICT (key) DO NOTHING;

-- ── 'en' homepage content ──────────────────────────────────────────────────────────────────────
-- Stats
INSERT INTO stat (site_id, value, label, note, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), '32+', 'Years Of Experience', 'Part of CMC Corporation, founded 1993.', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '30+', 'Countries', 'Delivery presence across three regions.', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '300+', 'Global Clients', 'Bosch, Honda, AIA, IBM, LINE and more.', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '35+', 'Business Partners', 'AWS, Google Cloud, UiPath, Salesforce.', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '3000+', 'Employees', 'Across delivery centres worldwide.', 4, 'PUBLISHED');

-- Service cards
INSERT INTO service_card (site_id, name, image, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'AI & Digital Strategy Advisory', '/media-service/images/2574eac9-a2eb-53b2-8f68-4d92fe1cb693.jpg', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Data & AI Solutions', '/media-service/images/c9b37ed5-2066-5eb5-ad97-c65ee570f169.jpg', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Digital & Cloud Solutions', '/media-service/images/f51d0009-870c-5977-981a-c3363a34c43d.jpg', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Enterprise Solutions', '/media-service/images/46be6f5f-5307-529c-b232-340bd10160e6.jpg', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Infrastructure & Managed Services', '/media-service/images/c669401a-2b4c-5209-a122-c189cb3853b1.jpg', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Cybersecurity', '/media-service/images/3ce83b57-2093-5948-9b9c-a8494c0f16c1.jpg', 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Software Engineering', '/media-service/images/411d3736-e1cc-50df-86ec-b7c288743f1f.jpg', 6, 'PUBLISHED');

-- Offices
INSERT INTO office (site_id, lat, lon, flag_color, big, city, address, image, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 21.03, 105.78, '#da251d', true, 'Hanoi Office (Headquarter)', '7 - 10F, CMC Tower, 11 Duy Tan Street, Cau Giay
Ward, Hanoi 100000, Vietnam', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 10.78, 106.70, '#da251d', false, 'Ho Chi Minh City Office', 'Delivery centre · 1200+ engineers
District 1, Ho Chi Minh City, Vietnam', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 34.69, 135.50, '#bc002d', true, 'Osaka Office', 'Mosaiwaki Frontier Bldg. 1F, 1-12-23
Sakaedori-Machi, Kita-ku, Osaka, Japan', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 37.56, 126.98, '#0047a0', false, 'Seoul Office', 'Korea market & partnerships
Gangnam-gu, Seoul, Korea', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 1.29, 103.85, '#ed2939', false, 'Singapore Office', 'APAC hub · Sales & advisory
Downtown Core, Singapore', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), -33.87, 151.21, '#00247d', false, 'Sydney Office', 'ANZ market
Sydney CBD, NSW, Australia', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 37.34, -121.89, '#b22234', false, 'San Jose Office', 'Silicon Valley
San Jose, California, USA', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 6, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 50.11, 8.68, '#1a1a1a', false, 'Frankfurt Office', 'DACH market
Frankfurt am Main, Germany', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 7, 'PUBLISHED');

-- Logo badges (awards / certifications / partners)
INSERT INTO logo_badge (site_id, type, name, logo, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 1', '/media-service/images/5237a74e-adf6-5ccf-ad9b-43bc8be17680.png', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 2', '/media-service/images/f93ca49b-06b3-5103-8a91-6cbdd25f2eb5.png', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 3', '/media-service/images/22d6810f-169d-5d72-8b15-c2c0244c661d.png', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 4', '/media-service/images/dc7e4f60-7000-5105-ae05-bcb3faee39ec.png', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 5', '/media-service/images/26849312-5779-5cb4-ba54-47f27b57c86e.png', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'ISO 9001', '/media-service/images/b88204c9-390f-5b1d-86dc-6fe458b167bf.png', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'ISO 27001', '/media-service/images/6d90b65d-c1a1-5da0-8d5b-01a32beb3071.png', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'ITIL', '/media-service/images/ea369b38-0e2a-57fd-bae7-64e4da13fd59.png', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'PCI-DSS', '/media-service/images/80fcaea0-1b7f-5ed2-a593-28ecd12dfde1.svg', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'AWS Advanced Tier Services', '/media-service/images/9073dc4b-2da9-564b-9a6e-7ecd53bfd4a2.png', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'ITIL', '/media-service/images/9da4207a-03ab-5d02-9078-dca1638331ac.png', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'AWS', '/media-service/images/1fc2bdd1-3a47-575d-9e2c-1335a584f408.svg', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'Google Cloud', '/media-service/images/adccd9b4-0f69-5a4e-9f3f-5c748a2778d5.webp', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'UiPath', '/media-service/images/fc905c4f-0f16-5f1f-94a0-5dda8d926e1f.png', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'ISTQB', '/media-service/images/07747331-7981-5bca-933a-88482dbc0eb0.png', 4, 'PUBLISHED');

-- Testimonials — before case_study, which can reference one
INSERT INTO testimonial (site_id, name, company, photo, flank_logos, title, quote, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'Mr. Nick Benjamin', 'envato', '/media-service/images/c63d3ad1-e521-58a4-8cc5-40867140b712.jpg',
        '[{"name": "northstar", "color": "#121212"}, {"name": "RSUPPORT", "color": "#b91c22"}]'::jsonb,
        'CTO', 'CMC Global’s happy culture shows up in different ways. One is that CMC Global is very dedicated and they walk the extra mile.', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Ms. Elena Rojas', 'northstar', '/media-service/images/5fdcd7eb-90e6-5cd6-aee4-0f24b33e3893.jpg',
        '[{"name": "RSUPPORT", "color": "#b91c22"}, {"name": "envato", "color": "#0b63c5"}]'::jsonb,
        'VP Engineering', 'They took our roadmap seriously from week one. The team scaled from four engineers to eighteen without a dip in delivery quality.', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Mr. Jun Park', 'RSUPPORT', '/media-service/images/df05f992-0541-5566-89ec-b1f12fee908f.jpg',
        '[{"name": "envato", "color": "#0b63c5"}, {"name": "northstar", "color": "#121212"}]'::jsonb,
        'Head of Product', 'What impressed us was the handover. Documentation, tests, runbooks — everything was where it should be when we took it in-house.', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Mr. Hiroshi Tanaka', 'Mitsui', '/media-service/images/6a74dbb9-1f24-54bb-a8ea-8b8428c0276c.jpg',
        '[{"name": "northstar", "color": "#121212"}, {"name": "RSUPPORT", "color": "#b91c22"}]'::jsonb,
        'CIO', 'A genuine partner rather than a vendor. They pushed back when our spec was wrong, which is exactly what we needed.', 3, 'PUBLISHED');

-- Footer navigation
INSERT INTO footer_nav_category (site_id, label, links, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'World-class IT Outsourcing',
        '["Custom Software Development", "Software Maintenance", "Legacy Migration", "Testing Services"]'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Digital Transformation',
        '["Cloud Professional Services", "Data & Analytics", "Artificial Intelligence Solutions", "RPA Services", "Low Code"]'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CMC Solutions',
        '["IoT Smart Device – CIVAMs Face", "CMC Social Listening", "CMC Chatbot", "C-ID Reader", "C-CA", "SOC"]'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Model',
        '["Project-based", "Staff Augmentation", "Hybrid", "Results Based"]'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'About Us',
        '["CMC Global", "CMC Corporation", "Company Profile"]'::jsonb, 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Case Studies',
        '["Customer Stories", "Industry", "Business Size"]'::jsonb, 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Resources',
        '["Blog", "Ebooks & Whitepapers", "News & Events"]'::jsonb, 6, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Careers',
        '["Careers"]'::jsonb, 7, 'PUBLISHED');

-- ── 'en' Insights posts and Case Studies ──────────────────────────────────────────────────────

-- Case studies. Results reuse only the figures already stated in each seeded title; the last one
-- is linked to the envato testimonial, the others stay unlinked (exercises the 'no quote' page path).
INSERT INTO case_study (site_id, slug, date, image, title, category, summary, body, results, testimonial_id, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'agentic-ai-intrusion-detection-software', 'Jun 2, 2026', '/media-service/images/f193eedf-82c6-544a-a03a-1e4c4753711e.png', '38% Higher Productivity & 40% Faster QA With Agentic AI For Intrusion Detection Software', 'Agentic AI Software Delivery',
        'How agentic AI across the delivery lifecycle lifted productivity and shortened QA for an intrusion detection software team.',
        ''
               '<h2>The challenge</h2><p>A security software team needed to ship faster without loosening quality gates. This sample case study outlines the approach.</p>'
               '<h2>The approach</h2><p>Agentic AI was introduced across development and testing, with engineers reviewing and steering the output at each step.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"label":"Higher productivity","value":"38%"},{"label":"Faster QA","value":"40%"}]'::jsonb, NULL, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'ai-energy-trading-system', 'Jun 2, 2026', '/media-service/images/89705d24-d075-5d3e-a945-d4caf115a1ef.webp', '15% Increase In Revenue With AI Energy Trading System', 'AI-Powered Trading & Forecasting',
        'An AI-powered trading and forecasting system that grew revenue for an energy trading business.',
        ''
               '<h2>The challenge</h2><p>Energy markets move quickly and forecasting errors are expensive. This sample case study outlines the approach.</p>'
               '<h2>The approach</h2><p>Forecasting models were integrated into the trading workflow so recommendations arrived where decisions were made.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"label":"Increase in revenue","value":"15%"}]'::jsonb, NULL, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'smart-field-service-azure-openai', 'Jun 2, 2026', '/media-service/images/598d8c53-00aa-51a1-9c9f-b5bc5a8aafc2.webp', '30% Ticket Return Reduced With Smart Field Service Software Using Azure OpenAI', 'AI-Powered Field Operations',
        'Smart field service software built on Azure OpenAI that reduced repeat visits and ticket returns.',
        ''
               '<h2>The challenge</h2><p>Field technicians were returning to the same jobs because the right information was not available on site. This sample case study outlines the approach.</p>'
               '<h2>The approach</h2><p>An assistant built on Azure OpenAI surfaced guidance and history to technicians during the job.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"label":"Fewer ticket returns","value":"30%"}]'::jsonb, NULL, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'unbreakable-connectivity-trust-won-the-deal', 'Jun 2, 2026', '/media-service/images/2cd9e6d6-29ed-5836-a255-0df42c96604c.webp', 'Beyond Innovation: Revolv CTO On Why Trust And “Unbreakable Connectivity” Won The Deal', 'Software Engineering',
        'Why trust and dependable connectivity, not just price, decided a long-term engineering partnership.',
        ''
               '<h2>The challenge</h2><p>Choosing an engineering partner is a trust decision as much as a technical one. This sample case study outlines what mattered.</p>'
               '<h2>The approach</h2><p>Early delivery, open communication and dependable connectivity between teams built confidence quickly.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[]'::jsonb, (SELECT id FROM testimonial WHERE site_id = (SELECT id FROM site WHERE code = 'en') AND company = 'envato' ORDER BY id LIMIT 1), 3, 'PUBLISHED');

-- Posts
INSERT INTO post (site_id, slug, category, image, title, excerpt, date, body, author_name, author_role, author_bio, tags, featured, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'vietnam-china-ai-digital-economy-dialogue-2026', 'insight', '/media-service/images/cc6d4e10-5da6-5020-9de4-e5effc9d51de.webp', 'Vietnam-China AI and Digital Economy Dialogue 2026: A New Chapter for Bilateral Tech Cooperation',
        'How a new round of bilateral dialogue is shaping cooperation on AI and the digital economy — and what it means for technology teams building across both markets.',
        'Jun 2, 2026',
        '<p>Cross-border cooperation on AI and the digital economy is moving from announcements to working practice. This sample article outlines the themes such a dialogue tends to cover.</p>'
               '<h2>Why bilateral cooperation matters</h2><p>Technology teams rarely build for a single market. Shared standards, talent exchange and data practices decide how quickly a product can move between regions.</p>'
               '<h2>What technology teams should watch</h2><p>Regulation, data residency and skills pipelines are the practical constraints. Teams that plan for them early spend less time reworking architecture later.</p>'
               '<h2>Where we go from here</h2><p>Editors: replace this sample copy with the real write-up for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["ai","events","partnerships"]'::jsonb, true, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'ai-workshop-series-pilots-into-production', 'insight', '/media-service/images/75239b3d-8291-54ba-8a9d-f42614038105.webp', 'AI Workshop Series: Turning Pilots Into Production Across The Enterprise',
        'Most enterprise AI pilots stall before production. A practical look at what separates the ones that ship.',
        'May 20, 2026',
        '<p>A pilot proves an idea can work. Production proves it can keep working. This sample article walks through the gap between the two.</p>'
               '<h2>Start from the workflow, not the model</h2><p>Pilots that ship are anchored to a specific team process with a named owner and a measurable outcome.</p>'
               '<h2>Plan for evaluation early</h2><p>Decide how quality will be measured before the first demo, so improvement is something you can show rather than describe.</p>'
               '<h2>Hand over properly</h2><p>Editors: replace this sample copy with the real write-up for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["ai","engineering"]'::jsonb, false, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'new-delivery-centre-ho-chi-minh-city', 'insight', '/media-service/images/b51a01dd-ccf7-53f5-b6aa-31b63a6628cb.webp', 'Bridging Talent And Technology: Our New Delivery Centre In Ho Chi Minh City',
        'Why we are growing engineering capacity in Ho Chi Minh City, and how it changes the way we staff and support client teams.',
        'May 6, 2026',
        '<p>Delivery capacity close to the people building it makes teams easier to staff, support and grow. This sample article describes the idea behind a new delivery centre.</p>'
               '<h2>Closer to talent</h2><p>A local centre gives clients access to a deeper engineering pool and shortens the time it takes to stand up a team.</p>'
               '<h2>Closer to clients</h2><p>Overlapping working hours and shared tooling keep collaboration simple.</p>'
               '<h2>What comes next</h2><p>Editors: replace this sample copy with the real write-up for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["delivery","company-news"]'::jsonb, false, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'ai-business-insight-night-what-boards-are-asking', 'insight', '/media-service/images/31d94776-c11b-5fb6-a5d2-f431a143dbf2.webp', 'AI Business Insight Night: What Boards Are Asking About AI In 2026',
        'The questions boards are raising about AI in 2026 — and a framework for answering them without the hype.',
        'Apr 22, 2026',
        '<p>Boards are asking sharper questions about AI than they were a year ago. This sample article groups them into three themes.</p>'
               '<h2>Value</h2><p>Where will AI change cost, revenue or risk, and how will we know it has?</p>'
               '<h2>Risk</h2><p>Data handling, model behaviour and accountability need clear owners before scale, not after.</p>'
               '<h2>Capability</h2><p>Editors: replace this sample copy with the real write-up for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["ai","events","governance"]'::jsonb, false, 3, 'PUBLISHED');
