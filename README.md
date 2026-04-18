# Marketing Analytics Platform

A full-stack marketing analytics platform for tracking user behavior, managing campaigns, and auditing SEO health across multiple websites. Built with Java Spring Boot and React TypeScript.

---

## Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Runtime | Java 21 |
| Framework | Spring Boot 3.4 |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate 6 |
| Auth | Spring Security 6 + JWT (JJWT 0.12) |
| Web Scraping | Jsoup |
| Page Speed | Google PageSpeed Insights API |
| Geolocation | MaxMind GeoLite2 |
| Scheduling | Spring @Scheduled |
| Email | Spring Mail + Gmail SMTP |

### Frontend
| Layer | Technology |
|---|---|
| Framework | React 18 + TypeScript |
| State Management | Redux Toolkit |
| Routing | React Router v6 |
| HTTP Client | Axios |
| Charts | Recharts |
| Build Tool | Vite |

---

## Domain Architecture
```
User
└── Site
├── Campaign
├── Event
└── SeoReport
├── SeoCheck
└── ReportSendLog
```

Every resource is scoped to a `Site`. Ownership is verified once per request via `siteRepository.findBySiteCodeAndUser(siteCode, user)` — a single check that covers all downstream resources.

---

## Features

### Auth
JWT-based registration and login. Tokens are signed with HMAC-SHA256 and validated on every request by `JwtAuthenticationFilter`. Both wrong email and wrong password return the same error message to prevent user enumeration attacks.

### Site Management
Register websites with a name and domain. On creation, a UUID `siteCode` is auto-generated server-side and a JavaScript tracking snippet is presented in a modal — pre-filled and ready to paste into the site's `<head>`. A **Reconnect Site** button on the dashboard lets users retrieve the snippet at any time.

### JavaScript Tracking Snippet
A self-contained IIFE that automatically captures:
- **Page views** on load
- **Clicks** on buttons, links, and `[data-track]` elements
- **Form submissions** with form ID

Sends events to the unauthenticated `POST /api/events` endpoint with UTM parameters parsed from the URL. Exposes `window.MarketingTracker.track(eventType, metadata)` for manual tracking.

### Campaign Manager
Full CRUD for marketing campaigns with soft delete (preserves event history), filterable by status and channel, paginated. Campaigns track optional cost, custom metrics, and benchmark comparisons.

### Event Tracking & Analytics Dashboard
Events are ingested from the JS snippet without authentication — security is enforced on the read side: only the site owner can query events. Each event is enriched server-side with country via MaxMind GeoLite2. The dashboard visualizes:
- Total events and event type breakdown with period-over-period comparison
- Events over time bar chart (30 days or 3 months)
- Top pages, channels, and countries

All dashboard analytics are filtered by channel and event type.

### SEO Auditor
Submits a URL for analysis. The backend crawls the page with Jsoup (title tag, meta description, H1, canonical, alt text, broken links) and calls the Google PageSpeed Insights API (performance score, SEO score, LCP, FCP, TBT). Results are persisted and displayed as Critical Fixes and Opportunities. Supports optional keyword prominence checking.

### Report Sharing
Email any SEO report to any address via Spring Mail + Gmail SMTP directly from the report detail page.

---

## Key Design Decisions

**JWT over sessions** — stateless authentication means any server instance can validate a token independently. No shared session store required for horizontal scaling.

**Unauthenticated event ingest endpoint** — the JS snippet runs in a browser with no JWT available. The security guarantee is on the read side: `GET /api/{site_code}/events` enforces ownership. An attacker can write events to any site code but can never read them back.

**Soft deletes on campaigns** — hard-deleting a campaign would orphan all `events` rows referencing `campaign_id`. Soft delete (setting `isArchived = true`) preserves referential integrity and event history.

**IIFE wrapper on the tracking snippet** — prevents snippet variables (`SITE_CODE`, `API_URL`, `sendEvent`) from polluting the global scope of the host website.

**`siteCode` is immutable** — used as a URL path variable across all endpoints and hardcoded into the tracking snippet. If it could be changed, all existing snippet installations would break silently.

**`EnumType.STRING` on all enums** — `EnumType.ORDINAL` couples data to enum declaration order; reordering enum constants silently corrupts existing rows. `STRING` stores the literal name, which is safe and self-documenting.

---

## Local Setup

### Prerequisites
- Java 21
- Maven
- PostgreSQL
- Node.js 18+

### Backend

```bash
# 1. Create a PostgreSQL database
createdb marketing_analytics

# 2. Configure environment
# Copy and fill in src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/marketing_analytics
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your-secret-key-at-least-32-characters
jwt.expiration=86400000
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password

# 3. Run
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` and proxies API requests to `http://localhost:8080`.

---

## Project Structure
```
backend/src/main/java/com/leon/marketing_analytics/
├── controller/     # HTTP layer — routes requests to services
├── service/        # Business logic — ownership checks, orchestration
├── repository/     # Spring Data JPA interfaces
├── entity/         # JPA entities (database tables)
├── dto/            # Request/response records
├── security/       # JWT filter, SecurityConfig
├── scheduler/      # @Scheduled follow-up audit job
└── exception/      # GlobalExceptionHandler, custom exceptions
frontend/src/
├── api/            # Axios instance + API functions per module
├── components/     # Reusable UI components
├── pages/          # Route-level page components
├── store/          # Redux store + auth slice
├── types/          # TypeScript interfaces
└── router/         # AppRouter + ProtectedRoute
```# deployment test
