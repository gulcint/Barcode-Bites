# BarcodeBite Beta Test Plan

## Build
- Android debug/release candidate APK
- Backend staging deployment

## Test Streams
1. Core flow: splash -> onboarding -> home -> scanner -> result
2. Data quality: clean-label, junk-food, additive metadata
3. History flow: save, open, clear history
4. Premium flow: paywall, verify/restore subscription, compare
5. Failure handling: offline backend, malformed barcode, quota exceeded

## Exit Criteria
- No blocker or critical issues open
- Crash-free session ratio >= 99%
- Backend p95 latency < 600ms for product+analysis requests
- Play billing purchase/restore validated on test accounts

## Reporting
- Daily triage of beta feedback
- Bug severity labels: blocker/high/medium/low
