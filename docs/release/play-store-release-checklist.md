# Play Store Release Checklist

## App Content
- App name, short description, full description finalized
- Category and content rating submitted
- Contact email and support URL valid
- Privacy policy URL set to published policy
- Data safety form completed and reviewed

## Store Assets
- App icon finalized
- Feature graphic finalized
- At least 2 phone screenshots uploaded
- Optional promo video link verified

## Build & Signing
- `versionCode` incremented
- `versionName` updated
- Release bundle/APK built from clean commit
- Upload key used for signed upload

## Billing & Premium
- Subscription products active in Play Console
- Test purchase and test restore validated
- Paywall copy localized and price labels consistent

## Final QA Gate
- Smoke test completed on at least 2 physical devices
- Crash-free startup verified
- Core flows verified:
  - splash/onboarding/home
  - scanner/result/history
  - profile/paywall/compare

## Rollout
- Start with staged rollout (5%-20%)
- Monitor crashes/ANRs and purchase errors for 24h
- Increase rollout only if metrics remain healthy
