# On-Call Ownership Map

## Roles
- Incident Commander (IC): coordinates response and communication
- Backend Owner: investigates server/api issues
- Android Owner: investigates app crash/UI regressions
- Billing Owner: investigates purchase/restore failures
- QA Owner: validates fixes and rollback outcomes

## Escalation Matrix
1. Alert triggered -> IC acknowledges within 5 minutes.
2. IC assigns technical owner (backend/android/billing).
3. If unresolved in 30 minutes, escalate to secondary owner.
4. If customer-impacting for >60 minutes, trigger stakeholder update.

## Coverage Windows
- Weekdays: 09:00-23:00 primary coverage
- Nights/weekends: paging fallback coverage

## Required Contacts
- Engineering lead contact
- Product owner contact
- Play Console admin contact
- Cloud provider admin contact

## Incident Channel Template
- `summary:`
- `impact:`
- `start time:`
- `owner:`
- `mitigation:`
- `next update:`

## Handover Rule
Every active incident must include:
- current hypothesis
- latest dashboard links
- open action list with owner per item
