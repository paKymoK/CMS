-- Snapshot of the 'en' homepage content authored through admin-app during Phase 1 build-out,
-- captured here so a fresh environment (new dev machine, CI, staging) stands up with the same
-- home page instead of empty content tables. site_id is resolved by code, never hardcoded, per
-- the decision log. Image paths point at website/public/images/... static assets; a few (the
-- service cards' photos, testimonial headshots) were sourced free-license from Unsplash as
-- stand-ins for real client-supplied media — swap via admin-app when real assets land.

INSERT INTO stat (site_id, value, label, note, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), '32+', 'Years Of Experience', 'Part of CMC Corporation, founded 1993.', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '30+', 'Countries', 'Delivery presence across three regions.', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '300+', 'Global Clients', 'Bosch, Honda, AIA, IBM, LINE and more.', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '35+', 'Business Partners', 'AWS, Google Cloud, UiPath, Salesforce.', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '30000+', 'Employees', 'Across delivery centres worldwide.', 4, 'PUBLISHED');

INSERT INTO service_card (site_id, name, image, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'AI & Digital Strategy Advisory', '/images/services/ai-digital-strategy.jpg', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Data & AI Solutions', '/images/services/data-ai-solutions.jpg', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Digital & Cloud Solutions', '/images/services/digital-cloud-solutions.jpg', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Enterprise Solutions', '/images/services/enterprise-solutions.jpg', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Infrastructure & Managed Services', '/images/services/infrastructure-managed-services.jpg', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Cybersecurity', '/images/services/cybersecurity.jpg', 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Software Engineering', '/images/services/software-engineering.jpg', 6, 'PUBLISHED');

INSERT INTO office (site_id, lat, lon, flag_color, big, city, address, image, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 21.03, 105.78, '#da251d', true, 'Hanoi Office (Headquarter)', '7 - 10F, CMC Tower, 11 Duy Tan Street, Cau Giay
Ward, Hanoi 100000, Vietnam', '/images/hero-team.webp', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 10.78, 106.70, '#da251d', false, 'Ho Chi Minh City Office', 'Delivery centre · 1200+ engineers
District 1, Ho Chi Minh City, Vietnam', '/images/hero-team.webp', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 34.69, 135.50, '#bc002d', true, 'Osaka Office', 'Mosaiwaki Frontier Bldg. 1F, 1-12-23
Sakaedori-Machi, Kita-ku, Osaka, Japan', '/images/hero-team.webp', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 37.56, 126.98, '#0047a0', false, 'Seoul Office', 'Korea market & partnerships
Gangnam-gu, Seoul, Korea', '/images/hero-team.webp', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 1.29, 103.85, '#ed2939', false, 'Singapore Office', 'APAC hub · Sales & advisory
Downtown Core, Singapore', '/images/hero-team.webp', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), -33.87, 151.21, '#00247d', false, 'Sydney Office', 'ANZ market
Sydney CBD, NSW, Australia', '/images/hero-team.webp', 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 37.34, -121.89, '#b22234', false, 'San Jose Office', 'Silicon Valley
San Jose, California, USA', '/images/hero-team.webp', 6, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 50.11, 8.68, '#1a1a1a', false, 'Frankfurt Office', 'DACH market
Frankfurt am Main, Germany', '/images/hero-team.webp', 7, 'PUBLISHED');

INSERT INTO case_study (site_id, date, image, title, category, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'Jun 2, 2026', '/images/case-studies/cs-1-redcan.png', '38% Higher Productivity & 40% Faster QA With Agentic AI For Intrusion Detection Software', 'Agentic AI Software Delivery', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Jun 2, 2026', '/images/case-studies/cs-2-ai-energy.webp', '15% Increase In Revenue With AI Energy Trading System', 'AI-Powered Trading & Forecasting', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Jun 2, 2026', '/images/case-studies/cs-3-smart-assistant.webp', '30% Ticket Return Reduced With Smart Field Service Software Using Azure OpenAI', 'AI-Powered Field Operations', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Jun 2, 2026', '/images/case-studies/cs-4-envato.webp', 'Beyond Innovation: Revolv CTO On Why Trust And “Unbreakable Connectivity” Won The Deal', 'Software Engineering', 3, 'PUBLISHED');

INSERT INTO post (site_id, category, image, title, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'insight', '/images/news-apec-workshop.webp', 'Vietnam-China AI and Digital Economy Dialogue 2026: A New Chapter for Bilateral Tech Cooperation', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'insight', '/images/news/news-2.webp', 'AI Workshop Series: Turning Pilots Into Production Across The Enterprise', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'insight', '/images/news-nz-partnership.webp', 'Bridging Talent And Technology: Our New Delivery Centre In Ho Chi Minh City', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'insight', '/images/news/nz-partnership.webp', 'AI Business Insight Night: What Boards Are Asking About AI In 2026', 3, 'PUBLISHED');

INSERT INTO logo_badge (site_id, type, name, logo, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 1', '/images/awards/award-1.png', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 2', '/images/awards/award-2.png', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 3', '/images/awards/award-3.png', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 4', '/images/awards/award-4.png', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 5', '/images/awards/award-5.png', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'ISO 9001', '/images/certs/cert-9001.png', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'ISO 27001', '/images/certs/cert-iso27001.png', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'ITIL', '/images/certs/cert-itil.png', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'PCI-DSS', '/images/certs/cert-pcidss.svg', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'CERTIFICATION', 'AWS Advanced Tier Services', '/images/certs/cert-aws-tier.png', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'ITIL', '/images/partners/partner-itil.png', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'AWS', '/images/partners/partner-aws.svg', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'Google Cloud', '/images/partners/partner-google-cloud.webp', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'UiPath', '/images/partners/partner-uipath.png', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'ISTQB', '/images/partners/partner-istqb.png', 4, 'PUBLISHED');

INSERT INTO testimonial (site_id, name, company, photo, flank_logos, title, quote, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'Mr. Nick Benjamin', 'envato', '/images/testimonials/nick-benjamin.jpg',
        '[{"name": "northstar", "color": "#121212"}, {"name": "RSUPPORT", "color": "#b91c22"}]'::jsonb,
        'CTO', 'CMC Global’s happy culture shows up in different ways. One is that CMC Global is very dedicated and they walk the extra mile.', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Ms. Elena Rojas', 'northstar', '/images/testimonials/elena-rojas.jpg',
        '[{"name": "RSUPPORT", "color": "#b91c22"}, {"name": "envato", "color": "#0b63c5"}]'::jsonb,
        'VP Engineering', 'They took our roadmap seriously from week one. The team scaled from four engineers to eighteen without a dip in delivery quality.', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Mr. Jun Park', 'RSUPPORT', '/images/testimonials/jun-park.jpg',
        '[{"name": "envato", "color": "#0b63c5"}, {"name": "northstar", "color": "#121212"}]'::jsonb,
        'Head of Product', 'What impressed us was the handover. Documentation, tests, runbooks — everything was where it should be when we took it in-house.', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Mr. Hiroshi Tanaka', 'Mitsui', '/images/testimonials/hiroshi-tanaka.jpg',
        '[{"name": "northstar", "color": "#121212"}, {"name": "RSUPPORT", "color": "#b91c22"}]'::jsonb,
        'CIO', 'A genuine partner rather than a vendor. They pushed back when our spec was wrong, which is exactly what we needed.', 3, 'PUBLISHED');

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
