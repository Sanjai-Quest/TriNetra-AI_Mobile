# TriNetra V3 Decision Engine

## Authoritative Path

`POST /api/claims/verdict` persists the claim and evidence, then invokes `VerdictService.computeVerdict`. The returned `DecisionStatus` is the authoritative decision in this compact runtime.

```text
Claim identity and workflow facts -> VerdictService -> Verdict -> API response
```

## Authoritative Signals

- expected serial versus observed serial
- expected IMEI versus observed IMEI where applicable
- checkpoint continuity
- mandatory evidence completeness
- custody validity
- structured condition status

Weight, weight deltas, Laplacian variance, Canny or edge density, temperature, humidity, wear metrics, ML/fraud scores, voice output, and AI recommendations are not authoritative V3 decision signals.

## Outcomes

- `PASS`: identity matches, evidence is complete, custody is valid, continuity is intact, and condition is not damaged or tampered.
- `REVIEW`: identity or required evidence is incomplete, custody is incomplete, or a matching unit is damaged or tampered.
- `HOLD`: serial mismatch, IMEI mismatch, or continuity break.

Identity conflicts are evaluated before condition or telemetry, so AI recommendations and contradictory sensor values cannot turn a mismatch into PASS.

## Verified Milestone

Backend tests cover matching identity, serial mismatch, IMEI mismatch, continuity break, missing evidence, damaged condition, and legacy-telemetry invariance. Live API checks returned PASS, REVIEW, and HOLD as expected.

## Logistics-Control Milestone

Checkpoint event storage, immutable custody and audit entities, server-side
release gating, physical serial/IMEI capture in Android, and the aggregate
`GET /api/verifications/{id}/investigation` endpoint are implemented and
verified for the three-checkpoint substitution scenario.

The existing Phase 2 web dashboard is not yet connected to this compact V3
investigation endpoint. Authentication/RBAC remains a deployment limitation;
the release service currently enforces the supplied `SUPERVISOR`/`ADMIN` role
boundary but does not yet validate a signed user identity.
