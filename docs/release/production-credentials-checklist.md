# Production Credentials Checklist

## Backend Secrets
- `DATABASE_URL` provisioned (managed Postgres, TLS enabled)
- `DATABASE_USER` and `DATABASE_PASSWORD` provisioned
- `JWT_SECRET` generated (>= 32 chars random)
- `JWT_ISSUER=barcodebite.app`
- `JWT_AUDIENCE=barcodebite-users`
- `OPEN_FOOD_FACTS_USER_AGENT` set to valid app contact

## Android / Play Console
- Play Billing products exist:
  - `barcodebite_premium_monthly`
  - `barcodebite_premium_yearly`
- License tester accounts configured
- Release signing key and upload key stored in secure vault
- Firebase Android app registered for release package id
- Crashlytics + Analytics dashboards accessible

## Infrastructure
- Render (or target host) env vars configured
- DB backup policy enabled (daily + retention policy)
- Access limited to least privilege service accounts
- Incident contact channel defined (Slack/Email/Pager)

## Verification Commands
```bash
# Validate backend env vars
cd backend
./scripts/preflight-production.sh

# Run backend test gate
cd ..
./gradlew --no-daemon :backend:test

# Build Android release candidate
./gradlew --no-daemon :androidApp:assembleRelease
```
