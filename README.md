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
| Cache / Rate Limiting | Redis (ElastiCache) |
| Web Scraping | Jsoup |
| Page Speed | Google PageSpeed Insights API |
| Geolocation | MaxMind GeoLite2 |
| Scheduling | Spring @Scheduled |
| Email | Spring Mail + Gmail SMTP |
| Health Checks | Spring Boot Actuator |

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
| Compute | AWS EC2 (t3.micro × 2, multi-AZ) |
| Load Balancer | AWS ALB + ACM (SSL termination, health checks) |
| Database | AWS RDS PostgreSQL (db.t3.micro) |
| Cache | AWS ElastiCache Redis |
| Frontend Hosting | AWS S3 + CloudFront |
| DDoS / CORS | Cloudflare (proxy + Worker) |
| Container Runtime | Docker |
| CI/CD | GitHub Actions + GitHub Container Registry |
| Auto-deployment | Watchtower |

---

## Architecture

```
Frontend path:
  Browser → CloudFront (edge cache, HTTPS) → S3 (static files)

Backend path:
  Browser → Cloudflare (DDoS, CORS Worker) → ALB (SSL via ACM, health checks)
    → Target Group
        ├── EC2-1 (us-east-1c) → Docker (Spring Boot)
        └── EC2-2 (us-east-1a) → Docker (Spring Boot)
              ├── RDS PostgreSQL
              └── ElastiCache Redis
```

```
CI/CD pipeline:
  GitHub push to main
      ↓
  GitHub Actions
    ├── Run tests against PostgreSQL container
    ├── Build + push Docker image to GHCR (backend)
    └── Build + sync to S3 + invalidate CloudFront (frontend)
             ↓
  Watchtower on EC2 (polls every 5 min)
      └── Detects new image → pulls + redeploys automatically
```

The two EC2 instances are in different Availability Zones. If one AZ fails, the ALB routes 100% of traffic to the surviving instance. The ALB health check hits `/actuator/health` (which includes a database connectivity check) every 30 seconds — unhealthy instances are removed from rotation automatically.

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
A self-contained IIFE that automatically captures page views on load, clicks on buttons/links/`[data-track]` elements, and form submissions with form ID. Sends events to the unauthenticated `POST /api/events` endpoint with UTM parameters parsed from the URL. Exposes `window.MarketingTracker.track(eventType, metadata)` for manual tracking.

### Campaign Manager
Full CRUD for marketing campaigns with soft delete (preserves event history), filterable by status and channel, paginated. Campaigns track optional cost, custom metrics, and benchmark comparisons.

### Event Tracking & Analytics Dashboard
Events are ingested from the JS snippet without authentication — security is enforced on the read side: only the site owner can query events. Each event is enriched server-side with country via MaxMind GeoLite2. The public ingest endpoint is rate-limited at 50 requests per minute per IP via Redis to prevent database pollution from malicious actors. The dashboard visualizes total events and event type breakdown with period-over-period comparison, events over time (30 days or 3 months), and top pages, channels, and countries. All analytics are filterable by channel and event type.

### SEO Auditor
Submits a URL for analysis. The backend crawls the page with Jsoup (title tag, meta description, H1, canonical, alt text, broken links) and calls the Google PageSpeed Insights API (performance score, SEO score, LCP, FCP, TBT). Results are persisted and displayed as Critical Fixes and Opportunities. Supports optional keyword prominence checking.

### Report Sharing & Automated Follow-Up
Email any SEO report to any address via Spring Mail + Gmail SMTP directly from the report detail page. Seven days after any audit, the system automatically re-runs the same audit, compares scores against the original, and emails a delta summary to the site owner via a database-backed polling scheduler.

---

## Key Design Decisions

**JWT over sessions** — stateless authentication means any server instance can validate a token independently. This is what made adding the second EC2 behind the ALB a zero-code-change operation — no shared session store required.

**Unauthenticated event ingest endpoint** — the JS snippet runs in a browser with no JWT available. The security guarantee is on the read side: `GET /api/{site_code}/events` enforces ownership. The write side is protected by per-IP rate limiting via Redis — an attacker can write events but is throttled to 50/minute, and can never read them back.

**Per-IP rate limiting in Redis, not in-memory** — with two EC2 instances behind the ALB, an in-memory counter on each instance would effectively double the rate limit (an attacker alternates between instances). Redis provides a single shared counter across all instances, making the rate limit accurate regardless of which instance handles the request.

**Soft deletes on campaigns** — hard-deleting a campaign would orphan all `events` rows referencing `campaign_id`. Soft delete (setting `isArchived = true`) preserves referential integrity and event history.

**ALB + ACM instead of Nginx + Let's Encrypt** — the ALB handles SSL termination, health checks, and traffic distribution natively. ACM certificates auto-renew silently, eliminating the Certbot renewal cron job and the ECDSA/RSA compatibility issues encountered with Let's Encrypt.

**EC2 instances in different AZs** — EC2-1 in `us-east-1c` and EC2-2 in `us-east-1a`. A single AZ failure can only take out half the fleet; the ALB routes 100% of traffic to the surviving instance automatically.

**EC2 security group locked to ALB** — port 8080 on the EC2 instances only accepts connections from the ALB's security group, not the public internet. The ALB is the single ingress point for all application traffic — any rate limiting or WAF rules at the ALB layer cannot be bypassed by hitting the EC2 directly.

**S3 + CloudFront instead of Vercel** — the full-AWS-native static hosting pattern. S3 stores the build artifacts, CloudFront serves them from global edge locations with HTTPS via ACM. Origin Access Control ensures only CloudFront can read the S3 bucket.

**Database-backed polling for follow-up audits** — pending follow-ups are stored as entity fields (`follow_up_at`, `follow_up_completed`) in PostgreSQL, not in JVM memory. A `@Scheduled` daily job queries for due work. This pattern survives server restarts because the intent lives in the database.

**IIFE wrapper on the tracking snippet** — prevents snippet variables from polluting the global scope of the host website.

**`siteCode` is immutable** — used as a URL path variable across all endpoints and hardcoded into the tracking snippet. If it could be changed, all existing snippet installations would break silently.

**`EnumType.STRING` on all enums** — `EnumType.ORDINAL` couples data to enum declaration order; reordering enum constants silently corrupts existing rows. `STRING` stores the literal name, which is safe and self-documenting.

**JVM memory tuning for t3.micro** — constrained heap (256MB), metaspace (128MB), code cache (64MB), and thread stack size (512KB) to fit within 1GB RAM alongside Docker and Watchtower. Tomcat thread pool reduced from 200 to 20 and HikariCP connection pool from 10 to 5.

---

## Deployment

### Infrastructure Overview

| Resource | Service | Details |
|---|---|---|
| Backend compute | AWS EC2 × 2 | t3.micro, Ubuntu 24.04, us-east-1a + us-east-1c |
| Load balancer | AWS ALB | SSL via ACM, health checks on `/actuator/health` |
| Database | AWS RDS | PostgreSQL 17, db.t3.micro |
| Cache | AWS ElastiCache | Redis 7, cache.t3.micro |
| Frontend hosting | AWS S3 + CloudFront | Static files + global CDN |
| DDoS / CORS | Cloudflare | Proxy + CORS Worker on `api` subdomain |
| Container registry | GHCR | Public, free |

### CI/CD Pipeline

**Backend** — every push to `main` that changes files under `backend/**`:

1. GitHub Actions spins up an `ubuntu-latest` runner with a PostgreSQL 16 service container
2. Tests run against the real PostgreSQL container
3. If tests pass, Docker builds the image and pushes to `ghcr.io/leonwu813/marketing-analytics:latest`
4. Watchtower on both EC2 instances detects the new image within 5 minutes and redeploys automatically

**Frontend** — every push to `main` that changes files under `frontend/**`:

1. GitHub Actions builds the React app with Vite
2. Syncs the `dist/` output to the S3 bucket
3. Invalidates the CloudFront cache so edge servers fetch the new files

### Environment Variables

All secrets are injected via environment variables — never hardcoded. On EC2, they live in `~/app.env` (chmod 600). In GitHub Actions, they are stored as repository secrets.

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection string (RDS endpoint) |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | HMAC-SHA256 signing key (64+ chars) |
| `PAGESPEED_API_KEY` | Google PageSpeed Insights API key |
| `MAIL_USERNAME` | Gmail address for report emails |
| `MAIL_PASSWORD` | Gmail App Password |
| `ALLOWED_ORIGIN` | Frontend URL for CORS (`https://www.siteplusplus.space`) |
| `REDIS_HOST` | ElastiCache Redis endpoint |
| `REDIS_PORT` | Redis port (6379) |

### Cloudflare Worker (CORS)

A Cloudflare Worker intercepts all requests to `api.siteplusplus.space` and injects CORS headers. This is required because Cloudflare's proxy layer intercepts preflight OPTIONS requests before they reach the ALB. The Worker handles OPTIONS with a 204 response and appends CORS headers to all forwarded responses.

---

## Local Setup

### Prerequisites
- Java 21
- Maven
- PostgreSQL
- Redis (via Docker: `docker run -d --name redis -p 6379:6379 redis:7-alpine`)
- Node.js 18+

### Backend

```bash
# 1. Create a PostgreSQL database
createdb marketing_analytics

# 2. Start Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 3. Copy the example properties file
cp backend/src/main/resources/application.properties.example \
   backend/src/main/resources/application.properties

# 4. Fill in your values in application.properties (or create a .env file)

# 5. Run
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
├── security/       # JWT filter, rate limit filter, SecurityConfig
├── config/         # CorsConfig, RedisConfig, ApplicationConfig, DataSeeder
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
├── deploy.yml              # Backend: test → build → push to GHCR
└── deploy-frontend.yml     # Frontend: build → sync to S3 → invalidate CloudFront
```
