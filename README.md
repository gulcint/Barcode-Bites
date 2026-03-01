# BarcodeBite

BarcodeBite is a barcode-based food analysis app with:
- `androidApp`: Android client (Compose)
- `shared`: Kotlin Multiplatform contracts/data/domain
- `backend`: Ktor backend API

## Status
- Phase 1: Completed
- Phase 2: Completed
- Phase 3: Completed (paywall, premium gate, compare, splash/onboarding)
- Phase 4: Completed (release-readiness baseline)
- Phase 5: Completed (production operations docs + preflight scripts)
- Phase 6: Completed (CI/CD and quality gates)

Detailed status: `docs/phase-status.md`

## Quick Commands
```bash
# Backend tests
./gradlew --no-daemon :backend:test

# Shared tests
./gradlew --no-daemon :shared:allTests

# Android debug build
./gradlew --no-daemon :androidApp:assembleDebug
```

## Production Prep
- Backend env template: `backend/.env.production.example`
- Env preflight validator: `backend/scripts/preflight-production.sh`
- Release runbooks/checklists: `docs/release/`
