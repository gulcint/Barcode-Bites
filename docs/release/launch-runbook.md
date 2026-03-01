# Launch Monitoring Runbook

## Scope
This runbook covers first 72 hours after production release.

## Primary Signals
- Backend uptime and HTTP 5xx rate
- Backend p95 latency for `/v1/products/{barcode}` and `/v1/analysis`
- Android crash-free sessions
- Billing success vs failure ratio

## SLO Guardrails
- Crash-free sessions >= 99%
- Backend p95 latency < 600ms (product+analysis)
- 5xx error rate < 1%
- Purchase failure rate < 5% (excluding canceled user flows)

## On Release Day
1. Confirm deployment hash and app version in changelog.
2. Run smoke:
   - register/login
   - scan valid barcode
   - open history
   - open profile
   - paywall purchase/restore on test account
3. Verify monitoring dashboards are receiving data.

## Incident Response
1. Detect alert (crash spike, 5xx spike, billing failure spike).
2. Triage severity:
   - Sev1: App unusable, checkout broken, sustained outage
   - Sev2: Major degradation but workaround exists
   - Sev3: Minor degradation
3. Mitigation:
   - rollback backend release or disable risky feature flag
   - pause staged rollout if Android issue
4. Communicate in incident channel with timeline and owner.
5. Publish postmortem action items within 24h.

## Rollback Triggers
- Crash-free sessions drop below 98%
- 5xx error rate stays above 3% for >15 minutes
- Purchase failures exceed 10% for >30 minutes

## Post-Launch Review
- 24h checkpoint: metrics trend + top issues
- 72h checkpoint: close incident tasks and promote full rollout
