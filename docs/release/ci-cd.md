# CI/CD Baseline

## CI Workflow
GitHub Actions `CI` workflow on every `pull_request` and `push` to `main`:
- `:backend:test`
- `:shared:allTests`
- `:androidApp:testDebugUnitTest`
- `:androidApp:assembleDebug`

File: `.github/workflows/ci.yml`

## Dependency Automation
Dependabot weekly updates:
- GitHub Actions ecosystem
- Gradle ecosystem

File: `.github/dependabot.yml`

## Merge Gate
A PR is merge-ready only when all CI jobs pass.

## Release Readiness Tie-in
CI complements release documents under `docs/release/` and backend preflight script:
- `backend/scripts/preflight-production.sh`
