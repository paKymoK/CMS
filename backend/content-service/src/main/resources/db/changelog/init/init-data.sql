-- content-service initial data: the tenant registry, the content-type registry, and the 'en'
-- homepage / Insights / Case Study content, so a fresh environment stands up with a complete site
-- instead of empty tables. Everything is a content_item row; see init-schema.sql.
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

-- 'hero_slide' / 'assistant_band' back the homepage's hero carousel and AI-assistant band (see
-- HeroSlideFields / AssistantBandFields). Further design sections are added the same way.
INSERT INTO content_type (key, label, field_schema)
VALUES ('hero_slide', 'Hero slide',
        '{"title": "textarea", "subtitle": "textarea", "ctaLabel": "string", "ctaUrl": "string", "layout": "select", "ctaStyle": "select", "backgroundImage": "image", "overlayImage": "image", "backgroundColor": "string", "backgroundColorEnd": "string"}'::jsonb),
       ('assistant_band', 'Assistant band',
        '{"title": "textarea", "greeting": "string", "welcome": "textarea", "suggestions": "string-list", "backdropImage": "image", "orbImage": "image"}'::jsonb)
ON CONFLICT (key) DO NOTHING;

INSERT INTO content_type (key, label, field_schema)
VALUES ('section_intro', 'Section intro',
        '{"section": "string", "eyebrow": "string", "title": "textarea", "body": "textarea", "kicker": "string", "linkLabel": "string", "linkUrl": "string", "buttonLabel": "string", "image": "image"}'::jsonb),
       ('feature_band', 'Feature band',
        '{"eyebrow": "string", "title": "textarea", "body": "textarea", "ctaLabel": "string", "ctaUrl": "string", "image": "image"}'::jsonb),
       ('about_hero', 'About hero',
        '{"breadcrumb": "string", "title": "textarea", "subtitle": "textarea", "ctaLabel": "string", "ctaUrl": "string", "image": "image", "stats": "stat-list"}'::jsonb),
       ('about_intro', 'About intro',
        '{"eyebrow": "string", "title": "string", "body": "textarea", "image": "image", "decorImage": "image", "statsTitle": "string"}'::jsonb),
       ('about_statement', 'About statement',
        '{"variant": "select", "eyebrow": "string", "title": "textarea", "body": "textarea", "image": "image"}'::jsonb),
       ('leader', 'Leader',
        '{"name": "string", "title": "string", "photo": "image"}'::jsonb),
       ('site_footer', 'Site footer',
        '{"title": "string", "ctaLabel": "string", "ctaUrl": "string", "copyright": "string", "privacyLabel": "string", "privacyUrl": "string", "termsLabel": "string", "termsUrl": "string", "facebookUrl": "string", "twitterUrl": "string", "instagramUrl": "string"}'::jsonb),
       ('service_category', 'Service category',
        '{"slug": "string", "name": "string"}'::jsonb),
       ('service_page', 'Service page',
        '{"slug": "string", "category": "string", "navLabel": "string"}'::jsonb),
       ('industry_card', 'Industry card',
        '{"label": "string", "image": "image", "url": "string"}'::jsonb),
       ('about_value', 'About value',
        '{"title": "textarea", "image": "image"}'::jsonb),
       ('industry_hero', 'Industry hero',
        '{"breadcrumb": "string", "title": "textarea", "subtitle": "textarea", "ctaLabel": "string", "ctaUrl": "string", "image": "image", "stats": "stat-list"}'::jsonb),
       ('industry_solution', 'Industry solution',
        '{"group": "string", "tabLabel": "string", "title": "string", "logo": "image", "image": "image", "columnA": "textarea", "columnB": "textarea"}'::jsonb),
       ('industry_reason', 'Industry reason',
        '{"title": "string", "image": "image"}'::jsonb),
       ('industry_story', 'Industry success story',
        '{"tag": "string", "title": "string", "body": "textarea", "image": "image", "linkLabel": "string", "linkUrl": "string", "heading": "string", "metrics": "stat-list"}'::jsonb)
ON CONFLICT (key) DO NOTHING;

-- Content that used to have a table each (service_card, office, testimonial, case_study) is stored
-- as content_item rows of these types; see ServiceCardFields / OfficeFields / TestimonialFields /
-- CaseStudyFields.
INSERT INTO content_type (key, label, field_schema)
VALUES ('service_card', 'Service card',
        '{"name": "string", "description": "textarea", "image": "image"}'::jsonb),
       ('office', 'Office',
        '{"city": "string", "address": "string", "lat": "number", "lon": "number", "flagColor": "string", "big": "boolean", "image": "image"}'::jsonb),
       ('testimonial', 'Testimonial',
        '{"name": "string", "company": "string", "title": "string", "photo": "image", "quote": "textarea", "logo": "image", "backgroundImage": "image", "flankLogo": "image", "flankFrom": "string", "flankTo": "string"}'::jsonb),
       ('case_study', 'Case study',
        '{"title": "string", "slug": "string", "date": "string", "category": "string", "image": "image", "summary": "textarea", "body": "richtext", "results": "stat-list", "testimonialId": "number"}'::jsonb)
ON CONFLICT (key) DO NOTHING;

-- Everything else that used to have a table each (stat, post, logo badges, footer navigation,
-- banner) is a content_item too; see StatFields / PostFields / LogoBadgeFields / ... The three logo
-- strips are separate types sharing one field shape.
INSERT INTO content_type (key, label, field_schema)
VALUES ('stat', 'Stat',
        '{"value": "string", "label": "string", "note": "string"}'::jsonb),
       ('award', 'Award',
        '{"name": "string", "logo": "image"}'::jsonb),
       ('certification', 'Certification',
        '{"name": "string", "logo": "image"}'::jsonb),
       ('partner', 'Partner',
        '{"name": "string", "logo": "image"}'::jsonb),
       ('footer_nav_category', 'Footer nav category',
        '{"label": "string", "links": "string-list"}'::jsonb),
       ('banner', 'Banner',
        '{"title": "string", "body": "textarea", "image": "image", "ctaLabel": "string", "ctaUrl": "string", "activeFrom": "datetime", "activeUntil": "datetime"}'::jsonb),
       ('post', 'Post',
        '{"title": "string", "slug": "string", "category": "string", "image": "image", "excerpt": "textarea", "date": "string", "body": "richtext", "authorName": "string", "authorRole": "string", "authorBio": "textarea", "authorAvatar": "image", "tags": "string-list", "featured": "boolean", "layout": "select"}'::jsonb)
ON CONFLICT (key) DO NOTHING;

-- ── 'en' hero slides + assistant band (copy and imagery from the Figma "Homepage" frame) ───────
INSERT INTO content_item (site_id, content_type, data, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'hero_slide', '{
  "title": "Riding The Next Wave of AI Transformation",
  "subtitle": "Reinvent Your Base - Scale Without Limits - Engineer Your Tomorrow",
  "ctaLabel": "Contact Us", "ctaUrl": "#contact", "layout": "center", "ctaStyle": "white",
  "backgroundColor": "#000812", "backgroundColorEnd": "#00152d",
  "overlayImage": "/media-service/images/6fad6d33-2060-592e-8d65-2a4509f37041.webp"
}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'hero_slide', '{
  "title": "Powering Innovation with\nSecured AI – Shielded by\n7 Layers of Unbreakable\nCybersecurity",
  "ctaLabel": "Learn More", "ctaUrl": "#whatwedo", "layout": "left", "ctaStyle": "blue",
  "backgroundImage": "/media-service/images/9db704ef-6cc6-58ca-a2b3-cf6334572868.webp"
}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'hero_slide', '{
  "title": "AI Survival Guide To Thrive In The Next Tech Revolution",
  "subtitle": "A guide for the AI era, providing actionable frameworks to build readiness today for tomorrow’s challenges.",
  "ctaLabel": "Download Ebook", "ctaUrl": "#contact", "layout": "right", "ctaStyle": "blue",
  "backgroundImage": "/media-service/images/84dbc07a-e485-58da-bb26-35531e4354f5.webp"
}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'hero_slide', '{
  "title": "Your Trusted\nAI Transformation Partner",
  "subtitle": "Committed to Your Success, Every Step of the Way",
  "ctaLabel": "Meet Our Experts", "ctaUrl": "#about", "layout": "left-wide", "ctaStyle": "light",
  "backgroundImage": "/media-service/images/3151c00c-c83b-5420-8cbc-9d2e8dc94d3f.webp"
}'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'hero_slide', '{
  "title": "CMC Global Officially Recognized As\nGreat Place to Work",
  "layout": "bottom-left",
  "backgroundImage": "/media-service/images/cbe9a60d-ded8-57d5-8768-d0a097bf260e.webp"
}'::jsonb, 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'assistant_band', '{
  "title": "The Intelligent Way To Know More About CMC",
  "greeting": "Welcome to CMC Global. What brings you here today?",
  "welcome": "Thank you for checking out our services. If you don’t mind me asking, what brings you to CMC Global today?",
  "suggestions": ["Tell me about CMC", "What service do you have?", "Show me your AI Capabilities"],
  "backdropImage": "/media-service/images/1c1e9133-7b8f-5156-9e7b-12d893778be2.webp",
  "orbImage": "/media-service/images/1ab3964b-82d6-5e66-99ed-97baeae4fa0b.webp"
}'::jsonb, 0, 'PUBLISHED');

-- Section intros (eyebrow / heading / paragraph / list label / "see all" link), keyed by anchor id.
INSERT INTO content_item (site_id, content_type, data, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "whatwedo", "eyebrow": "What We Do",
  "title": "Architecting Your Next-Gen Digital Core",
  "body": "We accelerate your digital evolution by blending advanced AI engineering, robust cloud infrastructure, and deep domain expertise."
}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "testimonials", "eyebrow": "Testimonials", "title": "Hear More from Our Clients"
}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "global", "eyebrow": "Global Delivery",
  "title": "Seamless global presence. Non-stop innovation.\nWe accelerate the digital evolution across every time zone"
}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "cases", "eyebrow": "Case Studies", "title": "Stories of Transformation",
  "kicker": "Latest Case Study", "linkLabel": "Explore all Case Studies", "linkUrl": "#cases"
}'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "insights", "eyebrow": "Insights", "title": "Building Tomorrow Together",
  "linkLabel": "Explore all Insights", "linkUrl": "/insights", "buttonLabel": "Read this letter"
}'::jsonb, 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "recognition", "title": "Global Recognition for Innovative Engineering",
  "body": "Our technical expertise and proven business impact earn us industry-leading awards and elite credentials."
}'::jsonb, 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "contact", "title": "Maximize Your ROI in the Cloud\nLet''s Chat!"
}'::jsonb, 6, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'feature_band', '{
  "eyebrow": "Your Extended Tech Workforce",
  "title": "Scale your tech capabilities with precision and agility",
  "body": "Expand your engineering bandwidth with flexible, cross-functional tech teams that seamlessly align with your workflows and scale as your goals evolve.",
  "ctaLabel": "Meet our experts", "ctaUrl": "#contact",
  "image": "/media-service/images/b5bf601b-153d-5766-9252-af68a3813c1b.webp"
}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'site_footer', '{
  "title": "Let''s connect", "ctaLabel": "Get in touch", "ctaUrl": "#contact",
  "copyright": "©2026 CMC Global. All rights reserved",
  "privacyLabel": "Privacy & Policy", "privacyUrl": "#",
  "termsLabel": "Terms & Condition", "termsUrl": "#",
  "facebookUrl": "#", "twitterUrl": "#", "instagramUrl": "#"
}'::jsonb, 0, 'PUBLISHED');


-- ── 'en' About page (copy and imagery from the Figma "About Us" frame) ─────────────────────────
INSERT INTO content_item (site_id, content_type, data, display_order, status)
VALUES ((SELECT id FROM site WHERE code = 'en'), 'about_hero', '{
  "breadcrumb": "About Us",
  "title": "Transforming Ideas into Global Impact, Together",
  "subtitle": "We unite passionate experts with advanced technology to scale your most ambitious digital vision",
  "ctaLabel": "Meet the people behind our promise", "ctaUrl": "#leaders",
  "image": "/media-service/images/dc66d6ec-213b-555b-976d-f4c389750a75.webp",
  "stats": [
    {"value": "33+", "label": "Years of Engineering Excellence"},
    {"value": "3000+", "label": "Engineering Experts"},
    {"value": "10+", "label": "Global Offices"}
  ]
}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_intro', '{
  "eyebrow": "Meet CMC Global", "title": "Meet CMC Global",
  "body": "At CMC Global, we believe true digital transformation is not about chasing the next big trend. It’s about mastering it to redefine what’s possible, and making technology work seamlessly for the business. Built on the deep technical foundation of CMC Corporation, we exist to turn complex technology into your clearest competitive advantage.\n\nAs global business enters the AI-first landscape, we architect intelligent, future-ready organizations from the ground up. By bridging technical mastery with global scale, we stand as the reliable partner behind your boldest ambitions, helping over 300 global leaders modernize with certainty and unlock steady growth.",
  "image": "/media-service/images/4cdf62ba-f05f-5384-9ed3-b224308cd66b.webp", "decorImage": "/media-service/images/853efbb8-b6d5-583d-be2e-5a07468c2da9.webp",
  "statsTitle": "Our Growth in Numbers"
}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_statement', '{
  "variant": "vision", "eyebrow": "Our Vision",
  "title": "Establishing a global presence\n& providing world-class services",
  "image": "/media-service/images/3e0f1666-e7d6-58f9-9b7f-ad905b8cea24.webp"
}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_statement', '{
  "variant": "mission", "eyebrow": "Our Mission",
  "body": "Our mission is simple: to make complex technology work for people and businesses, everywhere.\n\nWe empower organizations to grow with confidence through practical innovation, while building a culture where talent thrives, learns, and creates meaningful impact every day.",
  "image": "/media-service/images/3c11354d-e12c-5ab8-bb7b-9e8e1dfb5339.webp"
}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "leaders", "eyebrow": "Our People", "title": "Leadership Team",
  "image": "/media-service/images/b0ec2e5c-6d91-5d0d-9880-697ad31c24d2.webp"
}'::jsonb, 7, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{
  "section": "insights-page", "eyebrow": "Insights", "title": "Perspectives",
  "image": "/media-service/images/5618b749-88d7-5bc5-9e3f-6e499fdb3194.webp"
}'::jsonb, 8, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'leader', '{"name": "Mr. Ho Thanh Tung", "title": "President of CMC Global", "photo": "/media-service/images/bcffa7c5-5272-56dd-bba1-04801dd02208.webp"}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'leader', '{"name": "Mr. Dang Ngoc Bao", "title": "Chief Executive Officer of CMC Global", "photo": "/media-service/images/a02e0ce2-c478-58c7-8179-d2d377b79737.webp"}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'leader', '{"name": "Mr. Nguyen Viet Bach", "title": "Deputy CEO / Chief Delivery Officer of CMC Global", "photo": "/media-service/images/eae5d034-5a79-544d-80f1-80fabc1bf1b4.webp"}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'leader', '{"name": "Ms. Hoang Thi Thanh Hoa", "title": "Chief Finance Officer of CMC Global", "photo": "/media-service/images/0aa04ce3-4345-5884-862e-288364fe0a0f.webp"}'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'leader', '{"name": "Mr. Nguyen Cao Cuong", "title": "Chief Sales Officer of CMC Global", "photo": "/media-service/images/c7c6d745-7a25-50a5-a901-52512e5f71a4.webp"}'::jsonb, 4, 'PUBLISHED');

-- About page: Our Values 4C
INSERT INTO content_item (site_id, content_type, data, display_order, status)
VALUES
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "values", "eyebrow": "Our Values 4C", "title": "The DNA of Our Growth", "body": "We empower our global team to do their best work in a culture of dedication, creativity, and global collaboration."}'::jsonb, 13, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_value', '{"title": "Commitment", "image": "/media-service/images/13c10ea3-a2a6-5eab-a68a-54bba2708cab.webp"}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_value', '{"title": "Creativity", "image": "/media-service/images/c9926596-5469-5d41-aec8-b4c3cc8792c7.webp"}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_value', '{"title": "Customer\nCentricity", "image": "/media-service/images/f9e51d36-62d2-58d7-9828-f3a6918ced82.webp"}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'about_value', '{"title": "Speed", "image": "/media-service/images/03a414f9-4a03-583a-b3f7-649961c1b52e.webp"}'::jsonb, 3, 'PUBLISHED');

-- Case studies featured on the service pages (display order after the homepage ones)
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'case_study', jsonb_strip_nulls(jsonb_build_object(
       'slug', slug,
       'date', date,
       'image', image,
       'title', title,
       'category', category,
       'summary', summary,
       'body', body,
       'results', results,
       'testimonialId', testimonial_id)), display_order, status
FROM (VALUES
       ((SELECT id FROM site WHERE code = 'en'), 'partior-virtual-bank-simulator', 'May 12, 2026', '/media-service/images/43501e20-c924-50da-a581-791f4c35f0a3.webp', '95% Accuracy in Scenario Replication with Virtual Bank Simulator', 'Banking & Finance',
        'Transforming complex banking tests into seamless simulations',
        '<h2>The challenge</h2><p>Transforming complex banking tests into seamless simulations This sample case study outlines the approach.</p><h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"value": "70%", "label": "reduction in test cycle time"}, {"value": "95%", "label": "accuracy in transaction scenario replication"}, {"value": "3X", "label": "faster onboarding of new test cases"}]'::jsonb, NULL, 10, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'global-exchange-platform', 'April 28, 2026', '/media-service/images/d2cefe59-d217-5889-9839-a6e3ceec64b1.webp', '3X Faster Resolution for a Global Exchange Platform', 'Banking & Finance',
        'Enabling seamless liquidity operations with a ReactJS web solution',
        '<h2>The challenge</h2><p>Enabling seamless liquidity operations with a ReactJS web solution This sample case study outlines the approach.</p><h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"value": "3X", "label": "faster resolution"}]'::jsonb, NULL, 11, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'vault-core-banking', 'April 10, 2026', '/media-service/images/49c9a11c-b68e-55cc-ae80-2bab84b0a0d6.webp', 'Modernizing Banking with Next-gen core', 'Banking & Finance',
        'CMC Global power bank’s digital leap with Vault Core integration',
        '<h2>The challenge</h2><p>CMC Global power bank’s digital leap with Vault Core integration This sample case study outlines the approach.</p><h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"value": "90%", "label": "of problematic transactions handled automatically"}, {"value": "95%", "label": "uptime after go-live"}, {"value": "3X", "label": "faster transaction resolution time"}, {"value": "40%", "label": "increase in user satisfaction"}]'::jsonb, NULL, 12, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'entrepreneurial-business-bank', 'March 22, 2026', '/media-service/images/34127678-0316-5b9c-b0ea-ed7c9952c0af.webp', 'An Entrepreneurial Business Bank', 'Banking & Finance',
        'A well-established bank offers a range of financial products and services include business and personal banking, equipment loans, working capital, acquisition financing, small business administration lending, cash management, and so on.',
        '<h2>The challenge</h2><p>A well-established bank offers a range of financial products and services include business and personal banking, equipment loans, working capital, acquisition financing, small business administration lending, cash management, and so on. This sample case study outlines the approach.</p><h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"value": "100%", "label": "customer satisfied"}]'::jsonb, NULL, 13, 'PUBLISHED')
) AS v (site_id, slug, date, image, title, category, summary, body, results, testimonial_id, display_order, status);

-- Services section
INSERT INTO content_item (site_id, content_type, data, display_order, status)
VALUES
       ((SELECT id FROM site WHERE code = 'en'), 'service_category', '{"slug": "consulting-strategy", "name": "Consulting & Strategy"}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_category', '{"slug": "data-ai", "name": "Data & AI"}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_category', '{"slug": "digital-cloud", "name": "Digital & Cloud"}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_category', '{"slug": "digital-engineering", "name": "Digital Engineering"}'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_category', '{"slug": "enterprise-solutions", "name": "Enterprise Solutions"}'::jsonb, 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_category', '{"slug": "managed-services", "name": "Managed Services"}'::jsonb, 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_page', '{"slug": "web-app", "category": "digital-engineering", "navLabel": "Web App Development", "shortLabel": "Web App", "heroTitle": "Engineering\nDigital Platforms for the Digital-First Era", "heroSubtitle": "Architect digital foundations for innovation and long-term scalability.", "ctaLabel": "Let''s talk", "ctaUrl": "#contact", "heroImage": "/media-service/images/c4f4d901-20da-5bde-9fd9-436df6e20b50.webp", "capabilitiesEyebrow": "Capabilities", "capabilitiesTitle": "Our Capabilities", "capabilitiesSubtitle": "Scalable engineering capabilities to power your next generation of digital platforms.", "capabilities": [{"title": "UX/UI Design for Web", "body": "Create intuitive, user-centric interfaces that enhance engagement and drive conversion.", "image": "/media-service/images/c519a5f9-406c-5faa-989b-e1d27757f142.webp"}, {"title": "API & System Integration", "body": "Connect and orchestrate data, workflows, and interactions across distributed enterprise ecosystems.", "image": "/media-service/images/cad1e3b0-b639-53b4-80bd-be934ed2ed0a.webp"}, {"title": "Custom Web Application Development", "body": "Build tailored web solutions aligned with your business goals, workflows, and user needs.", "image": "/media-service/images/3c442eea-909a-5de4-9468-0aca0e6ec519.webp"}, {"title": "Maintenance & Continuous Improvement", "body": "Ensure ongoing performance, security, and feature evolution through proactive support and optimization.", "image": "/media-service/images/495f08f6-2937-53cc-b27e-6d5f2fa0527c.webp"}], "processTitle": "Our Process", "processSubtitle": "Defined process. Agile execution. Scalable results.", "processSteps": [{"title": "Project Scoping & Definition"}, {"title": "Strategy & Discovery", "image": "/media-service/images/0b1d3b30-98cc-5f88-808d-cdcdbd078be2.webp"}, {"title": "Ideation & Concept Development", "image": "/media-service/images/f2b90900-2bd1-5594-8632-859e4ce73072.webp"}, {"title": "UX/UI Design", "image": "/media-service/images/706b4554-6dc3-53d6-8512-2f90143e2c4f.webp"}, {"title": "Application Development", "image": "/media-service/images/bec9fcf9-057e-5cce-a163-0191fa639be4.webp"}, {"title": "Quality Assurance & Testing", "image": "/media-service/images/a2ec5a3a-ddaa-52ac-bf87-0e53aa6f2aed.webp"}, {"title": "Deployment & Launch", "image": "/media-service/images/0ba66a44-6512-5742-8caa-8a0b72856178.webp"}], "storiesHeading": "Partior Virtual Bank Simulator", "caseStudies": ["partior-virtual-bank-simulator", "global-exchange-platform"], "reasonsTitle": "Reasons to Partner With Us", "reasonsSubtitle": "Built to bring bold digital platforms to life at scale", "reasons": [{"title": "Platform-Centric Engineering", "body": "Engineers design with platform thinking, ensuring scalability, integration, and long-term adaptability from the ground up.", "image": "/media-service/images/f1dd153a-c112-5627-8327-08541dd4c3f8.webp", "sideImage": "/media-service/images/1d81a3bf-189a-5793-9930-cacf5dde6aee.webp"}]}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'service_page', '{"slug": "mobile-app", "category": "digital-engineering", "navLabel": "Mobile App Development", "shortLabel": "Mobile App", "heroTitle": "Built for Scale, Speed, and Real-World Impact", "heroSubtitle": "Turning bold ideas into powerful apps that scale with your business.", "ctaLabel": "Let''s talk", "ctaUrl": "#contact", "heroImage": "/media-service/images/74d38c0e-2876-5a12-8a36-fb55f08cc1f5.webp", "heroStats": [{"value": "$307.98 billion", "label": "is spent on App Development Software in 2025"}, {"value": "$2.3 trillion/ year", "label": "in costs from digital transformation projects failing due to legacy application bottlenecks"}], "capabilitiesEyebrow": "Our services", "capabilitiesTitle": "Service Offerings", "capabilitiesSubtitle": "Flexible development services built to accelerate your mobile innovation.", "capabilities": [{"title": "iOS App Development", "body": "Build high-performance iOS applications with seamless experiences across iPhone, iPad, and the Apple ecosystem.", "image": "/media-service/images/9c5e1729-9cc3-52b0-8afb-ac56daf5787e.webp"}, {"title": "Android App Development", "body": "Develop scalable Android applications optimized for performance across diverse devices and screen sizes.", "image": "/media-service/images/69c580dd-39eb-531a-9c43-b578a4c2a53e.webp"}], "processTitle": "Our Process", "processSubtitle": "Structured steps. Agile execution. Predictable outcomes.", "processSteps": [{"title": "Project Scoping & Definition"}, {"title": "Strategy & Discovery", "image": "/media-service/images/0b1d3b30-98cc-5f88-808d-cdcdbd078be2.webp"}, {"title": "Ideation & Concept Development", "image": "/media-service/images/f2b90900-2bd1-5594-8632-859e4ce73072.webp"}, {"title": "UX/UI Design", "image": "/media-service/images/706b4554-6dc3-53d6-8512-2f90143e2c4f.webp"}, {"title": "Application Development", "image": "/media-service/images/bec9fcf9-057e-5cce-a163-0191fa639be4.webp"}, {"title": "Quality Assurance & Testing", "image": "/media-service/images/a2ec5a3a-ddaa-52ac-bf87-0e53aa6f2aed.webp"}, {"title": "Deployment & Launch", "image": "/media-service/images/0ba66a44-6512-5742-8caa-8a0b72856178.webp"}], "storiesHeading": "Vault Core Banking App", "caseStudies": ["vault-core-banking", "entrepreneurial-business-bank"], "reasonsTitle": "Reasons to Partner With Us", "reasonsSubtitle": "Proven capabilities that help turn ambitious mobile ideas into real-world impact", "reasons": [{"title": "Product-Driven Development", "body": "Engineers think like product owners, aligning user experience, performance, and business outcomes from day one.", "image": "/media-service/images/f1dd153a-c112-5627-8327-08541dd4c3f8.webp", "sideImage": "/media-service/images/fa1c6c85-09d8-51bb-ad0c-a7af233c949c.webp"}]}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_card', '{"label": "Banking, Finance & Insurance", "url": "/industries/bfsi"}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_card', '{"label": "Logistics", "image": "/media-service/images/a71f7659-c2d6-5b18-981a-c8af7ff5d40a.webp"}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_card', '{"label": "Healthcare", "image": "/media-service/images/e37521df-abd2-56d8-8172-8c05d9d4466c.webp", "url": "/industries/healthcare"}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_card', '{"label": "Manufacturing", "image": "/media-service/images/3b7bfcac-5ed3-5b6c-aedf-bcb199042951.webp"}'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_card', '{"label": "Automotive", "url": "/industries/automotive"}'::jsonb, 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "service-stories", "eyebrow": "Success stories", "title": "Success Stories", "body": "Real products. Real challenges. Real results."}'::jsonb, 14, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "service-industries", "eyebrow": "Industries", "title": "Industries"}'::jsonb, 15, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "service-highlights", "eyebrow": "Insight/ news", "title": "Latest Highlights", "linkLabel": "Read more CMC Global’s News >", "linkUrl": "/insights"}'::jsonb, 16, 'PUBLISHED');

-- Banking, Finance & Insurance industry page
INSERT INTO content_item (site_id, content_type, data, display_order, status)
VALUES
       ((SELECT id FROM site WHERE code = 'en'), 'industry_hero', '{"breadcrumb": "Industries > Banking, Finance & Insurance", "title": "Accelerate Financial Innovation with Compliance at the Core", "subtitle": "Turning financial complexity into competitive advantage through intelligent technology.", "ctaLabel": "Let''s talk!", "ctaUrl": "#contact", "image": "/media-service/images/3a73cbd5-77f2-505a-8d14-df5810e84d6c.webp", "stats": [{"value": "15-20%", "label": "higher sales-conversion rates among banks thanks to experience-led banking transformations"}, {"value": "25-30%", "label": "reduction in claims expenses after deploying digital claims transformations"}]}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "industry-solutions", "eyebrow": "Solutions", "title": "Our Solutions", "body": "Designed to modernize banking operations, strengthen resilience, and accelerate digital innovation."}'::jsonb, 9, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "industry-why", "eyebrow": "Why CMCG", "title": "Reasons to Partner with Us", "body": "Built for Finance Reliability, Speed, and Compliance"}'::jsonb, 10, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "industry-stories", "eyebrow": "Case Studies", "title": "Success Stories", "body": "Real products. Real challenges. Real results."}'::jsonb, 11, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'section_intro', '{"section": "industry-insights", "eyebrow": "Insights", "title": "Building Tomorrow Together", "body": "For building secure, efficient, and future-ready financial services.", "linkLabel": "Explore all Insights", "linkUrl": "/insights"}'::jsonb, 12, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_solution', '{"group": "Core Banking", "tabLabel": "Thought Machine Solution", "title": "Core Banking Solutions", "logo": "/media-service/images/098991db-c052-50be-9fb5-2b26b1ff2d10.svg", "image": "/media-service/images/0af4798e-1f0c-58b6-a361-e390785af219.webp", "columnA": "Smart contract customization\nIntegration with channels & fintech APIs\nCloud deployment & DevOps automation", "columnB": "Data migration & automated testing\n24/7 support & maintenance"}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_solution', '{"group": "Core Banking", "tabLabel": "Temenos Solution", "title": "Temenos Solution", "logo": "/media-service/images/b845a55c-372e-579d-94a5-80e4a11944e4.svg", "image": "/media-service/images/931098b1-d024-5cb4-9717-c8f81c17262e.webp", "columnA": "Module customization & integration resources\nBusiness analyst with banking domain expertise\nParameterization, scripting, and interface development", "columnB": "Testing, defect resolution & technical documentation\nFlexible resources scaling across project phases"}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_solution', '{"group": "Digital Banking", "tabLabel": "Backbase Solution", "title": "Digital Banking Solutions", "logo": "/media-service/images/fc3fa6de-1a2f-51fc-b168-fda2c1c889c0.svg", "image": "/media-service/images/28d95483-6148-5146-8370-bccca734c023.webp", "columnA": "Backbase implementation & configuration\nModule customization & branding\nAPI integration with core banking & fintech services", "columnB": "Performance optimization & upgrades\nOngoing support & enhancement"}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_solution', '{"group": "Digital Banking", "tabLabel": "Bespoke Solution", "title": "Digital Banking Solutions", "image": "/media-service/images/204d0d9c-f5db-5e51-97d6-a8ddca9cbac4.webp", "columnA": "End-to-end solution design & architecture\nCustom UI/UX design and development\nAPI design & integration with any backend systems", "columnB": "Cloud-native or on-prem deployment\nLong-term maintenance & continuous improvement"}'::jsonb, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_solution', '{"group": "Open Banking", "tabLabel": "Open banking platform (WSO2, Oracle, Temenos)", "title": "Open Banking Solutions", "image": "/media-service/images/bb93e760-87b5-58ca-8255-5b764ed57104.webp", "columnA": "Platform implementation & configuration\nAPI customization & branding\nIntegration with core banking & fintech", "columnB": "Compliance validation\nOngoing support & version upgrades"}'::jsonb, 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_solution', '{"group": "Open Banking", "tabLabel": "Bespoke Solution", "title": "Open Banking Solutions", "image": "/media-service/images/e888ce94-9f48-5b4a-bd25-c63f0b2375a3.webp", "columnA": "End-to-end API architecture & development\nCustom API gateway, security and monitoring\nFull integration strategy with legacy & modern systems", "columnB": "Regulatory compliance design and testing\nContinuous improvement & new API product creation"}'::jsonb, 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_reason', '{"title": "Financial Services DNA", "image": "/media-service/images/2ed2d966-6dfd-5457-a75e-a29729c698d4.webp"}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_reason', '{"title": "Enterprise-grade Security", "image": "/media-service/images/b9d0c938-b897-5472-9592-656968ae58b3.webp"}'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_reason', '{"title": "Modern Banking Ecosystem Expertise", "image": "/media-service/images/da830127-e869-5517-a0d0-b7a02bd0cc9b.webp"}'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_story', '{"tag": "BFSI", "title": "HD Bank\nBespoke Digital Banking Solution", "body": "One of leading Vietnamese banks offering a wide range of financial services, including retail, corporate banking and digital banking solutions", "image": "/media-service/images/2fc766fc-8dd6-5710-b7ad-043e24ca5b68.webp", "linkLabel": "Read the case study →", "linkUrl": "/insights", "heading": "Bespoke Digital Banking Solution", "metrics": [{"value": "316", "label": "MM"}, {"value": "8", "label": "Months"}, {"value": "100%", "label": "Milestone Completed"}, {"value": "100%", "label": "Customer Satisfied"}]}'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'industry_story', '{"tag": "Banking & Finance", "title": "An Entrepreneurial Business Bank", "body": "A well-established bank offers a range of financial products and services include business and personal banking, equipment loans, working capital, acquisition financing, small business administration lending, cash management, and so on.", "image": "/media-service/images/d65d9edb-b5bf-5eca-9ed5-376df6b07558.webp", "linkLabel": "Read the case study →", "linkUrl": "/insights"}'::jsonb, 1, 'PUBLISHED');

-- ── 'en' homepage content ──────────────────────────────────────────────────────────────────────
-- Stats
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'stat', jsonb_strip_nulls(jsonb_build_object(
       'value', value,
       'label', label,
       'note', note)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), '32+', 'Years Of Experience', 'Part of CMC Corporation, founded 1993.', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '3000+', 'Employees', 'Across delivery centres worldwide.', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '300+', 'Global Clients', 'Bosch, Honda, AIA, IBM, LINE and more.', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '35+', 'Business Partners', 'AWS, Google Cloud, UiPath, Salesforce.', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), '30+', 'Countries', 'Delivery presence across three regions.', 4, 'PUBLISHED')
) AS v (site_id, value, label, note, display_order, status);

-- Service cards (copy + imagery from the Figma "Architect" frame; the Digital & Cloud and Software
-- images are the earlier seed photos, since the design file embeds none for those two).
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'service_card', jsonb_strip_nulls(jsonb_build_object(
       'name', name,
       'description', description,
       'image', image)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 'AI & Digital Strategy Advisory', 'Align your vision with execution through strategic roadmaps that de-risk your digital transformation and accelerate time-to-market.', '/media-service/images/61a49aad-7b67-5d9b-b905-35ba55bf6995.webp', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Data & AI Solutions', 'Standardize complex data silos into actionable intelligence to automate operations and unlock new revenue streams.', '/media-service/images/13d2553a-7ea7-59aa-be16-35da6fe3d283.webp', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Digital & Cloud Solutions', 'Migrate and modernize your infrastructure with scalable, high-performance cloud environments built for ultimate business agility.', '/media-service/images/f51d0009-870c-5977-981a-c3363a34c43d.jpg', 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Enterprise Solutions', 'Streamline core business operations by seamlessly integrating tailored ERP and CRM platforms.', '/media-service/images/0680bca6-d0bd-5ef4-9270-7a68851b9d1a.webp', 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Infrastructure & App Managed Services', 'Ensure 24/7 operational resilience and peak performance. Simultaneously, lower overhead by optimizing costs and resource efficiency.', '/media-service/images/f767d391-9f18-57de-ac67-b5e11500c2ab.webp', 4, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Cybersecurity', 'Safeguard digital assets, ensure strict compliance, and build trust with a proactive, zero-trust security framework.', '/media-service/images/5fa84228-af83-51e8-b466-a44b65772133.webp', 5, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Software Engineering', 'Deliver high-quality, custom applications at global speed with elite tech talent.', '/media-service/images/411d3736-e1cc-50df-86ec-b7c288743f1f.jpg', 6, 'PUBLISHED')
) AS v (site_id, name, description, image, display_order, status);

-- Offices
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'office', jsonb_strip_nulls(jsonb_build_object(
       'lat', lat,
       'lon', lon,
       'flagColor', flag_color,
       'big', big,
       'city', city,
       'address', address,
       'image', image)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 21.03, 105.78, '#da251d', true, 'Hanoi Office (Headquarter)', '7 - 10F, CMC Tower, 11 Duy Tan Street, Cau Giay
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
Frankfurt am Main, Germany', '/media-service/images/249edde3-116e-551f-a8c5-c51ebc320264.webp', 7, 'PUBLISHED')
) AS v (site_id, lat, lon, flag_color, big, city, address, image, display_order, status);

-- Logo badges (awards / certifications / partners)
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, lower(type), jsonb_strip_nulls(jsonb_build_object(
       'name', name,
       'logo', logo)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 'AWARD', 'Award 1', '/media-service/images/5237a74e-adf6-5ccf-ad9b-43bc8be17680.png', 0, 'PUBLISHED'),
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
       ((SELECT id FROM site WHERE code = 'en'), 'PARTNER', 'ISTQB', '/media-service/images/07747331-7981-5bca-933a-88482dbc0eb0.png', 4, 'PUBLISHED')
) AS v (site_id, type, name, logo, display_order, status);

-- Testimonials — before the case studies below, which can reference one
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'testimonial', jsonb_strip_nulls(jsonb_build_object(
       'name', name,
       'company', company,
       'photo', photo,
       'logo', logo,
       'backgroundImage', background_image,
       'flankLogo', flank_logo,
       'flankFrom', flank_from,
       'flankTo', flank_to,
       'title', title,
       'quote', quote)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 'Mr. Nick Benjamin', 'envato', '/media-service/images/63da191a-82c2-5927-8b35-fc70df49c616.webp', '/media-service/images/e126ca3b-4a8f-5b7c-acd6-d69b5d1c0645.svg', '/media-service/images/6b06cfc9-e9de-58f9-8b06-54215ee22391.webp', '/media-service/images/62e8fd32-66b7-5df6-9dcc-72e80d3ca63a.svg', '#87e64c', '#000000',
        'CTO', 'CMC Global’s helped us in a number of different ways. One is that CMC Global’s actually delivered what they said they would do. And so for me, that’s foundational. It’s important that when CMC Global say they can deliver teams, expertise or capability that they’re able to. And the second thing that I value is that actually many of the team members are actually coming with their own ideas and actually bringing alternative viewpoints for us to consider.', 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Mr. Kim Teak Joong', 'RSUPPORT', '/media-service/images/6ff4916f-16d9-5431-b03b-1d416a701ac3.webp', '/media-service/images/c759c17b-3318-5bc1-b142-baf9f985974a.webp', '/media-service/images/1dcb33c5-df5d-5f6b-ac75-bb1d31629984.webp', '/media-service/images/c14a4559-239a-54ec-9cec-cbdb12aef99d.webp', '#ad0021', '#47000d',
        'CTO', 'CMC Global’s greatest strength lies in its proactive approach to understanding and addressing customer needs. When challenges arise, CMC Global views them from the customer’s perspective and proposes effective solutions. This problem-solving mindset fosters a strong sense of trust, making it clear that we are not just collaborating but growing together as true strategic partners.', 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Mr. Le Minh', 'northstar', '/media-service/images/275a9039-92da-57d2-8678-fa64d11c4b7b.webp', '/media-service/images/367285f7-e062-5b36-bcdf-5ba64e6ecf1f.svg', '/media-service/images/b4aa8aaf-eaf1-5723-96ab-d70b2e7f0383.webp', '/media-service/images/728d1a6c-faf3-5411-8962-101a145430ca.svg', '#404040', '#000000',
        'Chief Product Officer', 'Flexibility is definitely one of the character that we value most about CMC Global. Flexibility in terms of the number of headcounts that we hire, flexibility in terms of how you collaborate with us, flexibility in terms of the skills that we need to hire. Your technical knowledge is also something I definitely value a lot. And having your guidance in terms of what are the capabilities we should grow is definitely a key part of the future collaboration that I would love to see continuously with CMC Global.', 2, 'PUBLISHED')
) AS v (site_id, name, company, photo, logo, background_image, flank_logo, flank_from, flank_to, title, quote, display_order, status);

-- Footer navigation
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'footer_nav_category', jsonb_strip_nulls(jsonb_build_object(
       'label', label,
       'links', links)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 'Services',
        '["Consulting & Strategy", "Data & AI", "Digital & Cloud", "Digital Engineering", "Enterprise Solutions", "Managed Services"]'::jsonb, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Industries',
        '["Banking, Finance & Insurance", "Healthcare", "Manufacturing"]'::jsonb, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'Insights',
        '["Perspectives", "Case Studies", "Blogs", "Client Speak"]'::jsonb, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'About',
        '["Our Organization", "Our Partners", "Our Leaders", "Newsroom", "Events"]'::jsonb, 3, 'PUBLISHED')
) AS v (site_id, label, links, display_order, status);

-- ── 'en' Insights posts and Case Studies ──────────────────────────────────────────────────────

-- Case studies. Results reuse only the figures already stated in each seeded title; the last one
-- is linked to the envato testimonial, the others stay unlinked (exercises the 'no quote' page path).
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'case_study', jsonb_strip_nulls(jsonb_build_object(
       'slug', slug,
       'date', date,
       'image', image,
       'title', title,
       'category', category,
       'summary', summary,
       'body', body,
       'results', results,
       'testimonialId', testimonial_id)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 'agentic-ai-intrusion-detection-software', 'June 25, 2026', '/media-service/images/8bb34389-3409-5daa-8fb3-f70433d5e357.webp', '36% Higher Productivity & 40% Faster QA With Agentic AI For Intrusion Detection Software', 'Agentic AI Software Delivery',
        'How agentic AI across the delivery lifecycle lifted productivity and shortened QA for an intrusion detection software team.',
        ''
               '<h2>The challenge</h2><p>A security software team needed to ship faster without loosening quality gates. This sample case study outlines the approach.</p>'
               '<h2>The approach</h2><p>Agentic AI was introduced across development and testing, with engineers reviewing and steering the output at each step.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"label":"Higher productivity","value":"36%"},{"label":"Faster QA","value":"40%"}]'::jsonb, NULL, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'ai-energy-trading-system', 'June 20, 2026', '/media-service/images/89705d24-d075-5d3e-a945-d4caf115a1ef.webp', '15% Increase In Revenue With AI Energy Trading System', 'AI-Powered Trading & Forecasting',
        'An AI-powered trading and forecasting system that grew revenue for an energy trading business.',
        ''
               '<h2>The challenge</h2><p>Energy markets move quickly and forecasting errors are expensive. This sample case study outlines the approach.</p>'
               '<h2>The approach</h2><p>Forecasting models were integrated into the trading workflow so recommendations arrived where decisions were made.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"label":"Increase in revenue","value":"15%"}]'::jsonb, NULL, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'smart-field-service-azure-openai', 'June 2, 2026', '/media-service/images/41f122a5-ac66-524a-a8ea-655c9e94f8e0.webp', '30% Trips Return Reduced With Smart Field Service Software Using Azure Open AI', 'AI-Powered Field Operations',
        'Smart field service software built on Azure OpenAI that reduced repeat visits and ticket returns.',
        ''
               '<h2>The challenge</h2><p>Field technicians were returning to the same jobs because the right information was not available on site. This sample case study outlines the approach.</p>'
               '<h2>The approach</h2><p>An assistant built on Azure OpenAI surfaced guidance and history to technicians during the job.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[{"label":"Fewer ticket returns","value":"30%"}]'::jsonb, NULL, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'unbreakable-connectivity-trust-won-the-deal', 'March 2, 2026', '/media-service/images/822f35e9-222b-50b0-92ae-bf0fd7cda6ce.webp', 'Beyond a Vendor: Envato CTO on Why Trust and “Unbreakable Connectivity” Won the Deal', 'Software Engineering',
        'Why trust and dependable connectivity, not just price, decided a long-term engineering partnership.',
        ''
               '<h2>The challenge</h2><p>Choosing an engineering partner is a trust decision as much as a technical one. This sample case study outlines what mattered.</p>'
               '<h2>The approach</h2><p>Early delivery, open communication and dependable connectivity between teams built confidence quickly.</p>'
               '<h2>The outcome</h2><p>Editors: replace this sample copy with the real case study for your region.</p>',
        '[]'::jsonb, (SELECT id FROM content_item WHERE site_id = (SELECT id FROM site WHERE code = 'en') AND content_type = 'testimonial' AND data ->> 'company' = 'envato' ORDER BY id LIMIT 1), 3, 'PUBLISHED')
) AS v (site_id, slug, date, image, title, category, summary, body, results, testimonial_id, display_order, status);

-- Posts
INSERT INTO content_item (site_id, content_type, data, display_order, status)
SELECT site_id, 'post', jsonb_strip_nulls(jsonb_build_object(
       'slug', slug,
       'category', category,
       'image', image,
       'title', title,
       'excerpt', excerpt,
       'date', date,
       'body', body,
       'authorName', author_name,
       'authorRole', author_role,
       'authorBio', author_bio,
       'tags', tags,
       'featured', featured)), display_order, status
FROM (VALUES ((SELECT id FROM site WHERE code = 'en'), 'vietnam-china-ai-digital-economy-dialogue-2026', 'insight', '/media-service/images/7adbffc8-3d0b-58ef-9fce-1baa08a502a3.webp', 'Vietnam-China AI and Digital Economy Dialogue 2026: A New Chapter for Bilateral Tech Cooperation',
        'How a new round of bilateral dialogue is shaping cooperation on AI and the digital economy — and what it means for technology teams building across both markets.',
        'August 28, 2026',
        '<p>How a new round of bilateral dialogue is shaping cooperation on AI and the digital economy — and what it means for technology teams building across both markets. This sample article outlines the themes; editors should replace it with the real write-up for your region.</p><h2>Why it matters</h2><p>Technology teams rarely build for a single market. Shared standards, talent exchange and delivery practices decide how quickly work moves between regions.</p><h2>What happens next</h2><p>Editors: replace this sample copy with the real article for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["AI & Automation","Events","Partnerships"]'::jsonb, true, 0, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'apec-workshop-ai-digital-transformation', 'insight', '/media-service/images/1a2f3035-18e5-5e98-a380-41deb5d17037.webp', 'APEC Workshop: Putting AI And Digital Transformation Into Practice',
        'Highlights from a regional workshop on turning AI strategy into delivery across the Asia-Pacific.',
        'July 18, 2026',
        '<p>Highlights from a regional workshop on turning AI strategy into delivery across the Asia-Pacific. This sample article outlines the themes; editors should replace it with the real write-up for your region.</p><h2>Why it matters</h2><p>Technology teams rarely build for a single market. Shared standards, talent exchange and delivery practices decide how quickly work moves between regions.</p><h2>What happens next</h2><p>Editors: replace this sample copy with the real article for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["AI & Automation","Events"]'::jsonb, false, 1, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'cmc-global-new-zealand-cooperation-mou', 'insight', '/media-service/images/bee4b527-26bc-55ca-9c08-6eefd30ed5e6.webp', 'CMC Global Signs A Cooperation MOU With New Zealand Partners',
        'A new memorandum of understanding opens joint work on digital talent and technology delivery.',
        'July 4, 2026',
        '<p>A new memorandum of understanding opens joint work on digital talent and technology delivery. This sample article outlines the themes; editors should replace it with the real write-up for your region.</p><h2>Why it matters</h2><p>Technology teams rarely build for a single market. Shared standards, talent exchange and delivery practices decide how quickly work moves between regions.</p><h2>What happens next</h2><p>Editors: replace this sample copy with the real article for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["Partnerships","Events"]'::jsonb, false, 2, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'cmc-global-salesforce-enterprise-ai', 'insight', '/media-service/images/fd16a859-67a3-572e-9823-3388b3eecab4.webp', 'CMC Global x Salesforce: Accelerating Enterprise AI Adoption',
        'How the partnership helps enterprises move from CRM pilots to AI-assisted operations.',
        'June 20, 2026',
        '<p>How the partnership helps enterprises move from CRM pilots to AI-assisted operations. This sample article outlines the themes; editors should replace it with the real write-up for your region.</p><h2>Why it matters</h2><p>Technology teams rarely build for a single market. Shared standards, talent exchange and delivery practices decide how quickly work moves between regions.</p><h2>What happens next</h2><p>Editors: replace this sample copy with the real article for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["Partnerships","AI & Automation"]'::jsonb, false, 3, 'PUBLISHED'),
       ((SELECT id FROM site WHERE code = 'en'), 'cmc-global-community-day-2026', 'insight', '/media-service/images/5ac98d1f-ebad-5aa3-9c9d-abb0d53dc289.webp', 'Event Highlights: Building The Next Generation Of Engineers',
        'A look back at the event that brought engineers, students and partners together.',
        'June 6, 2026',
        '<p>A look back at the event that brought engineers, students and partners together. This sample article outlines the themes; editors should replace it with the real write-up for your region.</p><h2>Why it matters</h2><p>Technology teams rarely build for a single market. Shared standards, talent exchange and delivery practices decide how quickly work moves between regions.</p><h2>What happens next</h2><p>Editors: replace this sample copy with the real article for your region.</p>',
        'CMC Global Editorial', 'Insights Team', 'The CMC Global Insights team writes about technology, delivery and the markets we work in.', '["Events"]'::jsonb, false, 4, 'PUBLISHED')
) AS v (site_id, slug, category, image, title, excerpt, date, body, author_name, author_role, author_bio, tags, featured, display_order, status);
