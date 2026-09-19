-- Primary nav items and nav sections never change per-region — hardcoded directly in the
-- Next.js site instead of CMS-managed, dropping the CRUD surface and its admin endpoints
-- along with the tables.
DROP TABLE IF EXISTS primary_nav_item;
DROP TABLE IF EXISTS nav_section;
