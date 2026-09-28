repo: paKymoK/CMS
branch: main

## Last sync
date: 2026-09-26T04:21:09Z

### Updated in this project
- New admin-app login page (entry to the OAuth2 flow), same look as the OAuth2 sign-in screen

## Sync history
date: 2026-09-25T15:39:04Z
- New OAuth2 sign-in + consent screens in the landing page's dark "wave band" style
- New admin console (content tables, drawer editor, media library, assistant) using landing tokens

## Screen map
| Screen | Repo files |
|---|---|
| CMS Admin Login.dc.html | admin-app/src/pages/Login.tsx |
| CMS Sign In.dc.html | backend/auth-service/src/main/resources/templates/login.html, consent.html; style from website/components/home/WaveBand.tsx, HeroCanvas.tsx, AssistantPanel.tsx |
| CMS Admin.dc.html | admin-app/src/components/AppShell.tsx, config/navigation.tsx, config/resources.ts, config/sites.ts, pages/ContentPage.tsx, pages/MediaLibraryPage.tsx, pages/AssistantPage.tsx; style from website/app/globals.css, components/layout/SiteHeader.tsx, components/home/ContactForm.tsx, Services.tsx, Insights.tsx |
