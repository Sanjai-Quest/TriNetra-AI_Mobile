# V3 Investigation

## Creation

A continuity or identity HOLD creates a persisted investigation linked to the
verification and serialized unit. The case is opened by the backend, not by a
frontend animation.

Investigation fields include:

- investigation ID
- verification ID
- unit ID
- status
- reason
- expected serial
- observed serial
- last matching checkpoint
- first divergence checkpoint
- created timestamp

## Investigation Meaning

The system reports where identity continuity changed. It does not accuse a
specific operator or infer fraud intent.

Example:

```text
Expected: SN001
Observed: SN999
Last verified: CP3
First known divergence: CP4
Status: OPEN
Reason: UNIT_CONTINUITY_BROKEN
```

## Investigation API

```text
GET /api/verifications/{verificationId}/investigation
GET /api/units/{unitId}/history
```

The response is built from persisted unit, verification, checkpoint, custody,
audit, and investigation records.

## Web Console

The React investigation view displays:

- expected and observed identity
- source case provenance where available
- decision and reasoning
- checkpoint timeline
- investigation status
- last matching checkpoint
- first known divergence
- custody events
- audit count

The web UI does not calculate or override the decision.

## Resolution Boundary

Investigation resolution actions and signed authentication are not yet fully
implemented. Until then, an open investigation remains a backend-controlled
exception and release is blocked unless the persisted verification is `PASS`
and the actor has an authorized role.
