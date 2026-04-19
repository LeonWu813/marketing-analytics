# Marketing Analytics Platform

A full-stack marketing analytics platform for tracking user behavior, managing campaigns, and auditing SEO health across multiple websites. Built with Java Spring Boot and React TypeScript.

**Live Demo:** [https://www.siteplusplus.space](https://www.siteplusplus.space)

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

### Infrastructure
| Layer | Technology |
|---|---|
| Backend Hosting | AWS EC2 (t3.micro) |
| Database Hosting | AWS RDS PostgreSQL (db.t3.micro) |
| Frontend Hosting | Vercel |
| CDN / DDoS Protection | Cloudflare |
| Reverse Proxy / SSL | Nginx + Let's Encrypt |
| Container Runtime | Docker |
| CI/CD | GitHub Actions + GitHub Container Registry |
| Auto-deployment | Watchtower |

---

## Architecture

```
Browser
    ↓ HTTPS
Cloudflare (CDN + DDoS protection + CORS Worker)
    ↓
Nginx (SSL termination, reverse proxy)
    ↓ port 8080
Docker Container (Spring Boot app)
    ↓
AWS RDS PostgreSQL
```

```
GitHub push to main
    ↓
GitHub Actions
  ├── Run tests against PostgreSQL container
  └── Build + push Docker image to GHCR
           ↓
Watchtower on EC2 (polls every 5 min)
    └── Detects new image → pulls + redeploys automatically
```

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

**IIFE wrapper on the tracking snippet** — prevents snippet variables from polluting the global scope of the host website.

**`siteCode` is immutable** — used as a URL path variable across all endpoints and hardcoded into the tracking snippet. If it could be changed, all existing snippet installations would break silently.

**`EnumType.STRING` on all enums** — `EnumType.ORDINAL` couples data to enum declaration order; reordering enum constants silently corrupts existing rows. `STRING` stores the literal name, which is safe and self-documenting.

**Watchtower for zero-SSH deployment** — GitHub Actions pushes the Docker image to GHCR. Watchtower on EC2 polls GHCR every 5 minutes and redeploys automatically. This avoids requiring GitHub Actions to SSH into EC2, which is blocked by some network configurations.

---

## Deployment

### Infrastructure Overview

| Resource | Service | Details |
|---|---|---|
| Backend server | AWS EC2 | t3.micro, Ubuntu 24.04, us-east-1 |
| Database | AWS RDS | PostgreSQL 17, db.t3.micro |
| Frontend | Vercel | Auto-deploy on push to main |
| CDN | Cloudflare | Proxy + DDoS + CORS Worker |
| SSL | Let's Encrypt | Auto-renews via Certbot |
| Container registry | GHCR | Public, free |

### CI/CD Pipeline

Every push to `main` that changes files under `backend/**`:

1. GitHub Actions spins up an `ubuntu-latest` runner
2. A PostgreSQL 16 service container starts alongside the runner
3. Tests run against the real PostgreSQL container
4. If tests pass, Docker builds the image and pushes to `ghcr.io/leonwu813/marketing-analytics:latest`
5. Watchtower on EC2 detects the new image within 5 minutes and redeploys automatically

### Environment Variables

All secrets are injected via environment variables — never hardcoded. On EC2, they live in `~/app.env` (chmod 600). In GitHub Actions, they are stored as repository secrets.

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection string |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | HMAC-SHA256 signing key (64+ chars) |
| `PAGESPEED_API_KEY` | Google PageSpeed Insights API key |
| `MAIL_USERNAME` | Gmail address for report emails |
| `MAIL_PASSWORD` | Gmail App Password |
| `ALLOWED_ORIGIN` | Frontend URL for CORS |

### Nginx Configuration

Nginx acts as a reverse proxy on EC2, handling SSL termination and forwarding traffic to Spring Boot on port 8080:

```
Port 80  → redirect to HTTPS
Port 443 → SSL termination → proxy_pass http://localhost:8080
```

### Cloudflare Worker (CORS)

A Cloudflare Worker intercepts all requests to `api.siteplusplus.space` and injects CORS headers. This is required because Cloudflare's proxy layer intercepts preflight OPTIONS requests before they reach Nginx.

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

# 2. Copy the example properties file
cp backend/src/main/resources/application.properties.example \
   backend/src/main/resources/application.properties

# 3. Fill in your values in application.properties

# 4. Run
cd backend
./mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`. The `dev` profile activates `DataSeeder`, which automatically creates a test user (`test@example.co` / `Test1234!`) and 500 sample events on first run.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` and proxies `/api` requests to `http://localhost:8080` via Vite's dev proxy — no CORS configuration needed locally.

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
├── config/         # CorsConfig, ApplicationConfig, DataSeeder
├── scheduler/      # @Scheduled follow-up audit job
└── exception/      # GlobalExceptionHandler, custom exceptions

frontend/src/
├── api/            # Axios instance + API functions per module
├── components/     # Reusable UI components
├── pages/          # Route-level page components
├── store/          # Redux store + auth slice
├── types/          # TypeScript interfaces
└── router/         # AppRouter + ProtectedRoute

.github/workflows/
└── deploy.yml      # Test → build → push to GHCR
```
