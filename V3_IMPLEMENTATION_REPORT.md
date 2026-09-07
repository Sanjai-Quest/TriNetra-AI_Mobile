# TriNetra V3 Implementation Report

**Report date:** 2026-09-07  
**Published branch:** `v3-logistics-control`  
**Latest published commit:** `6ad5d84`  
**Repository:** `https://github.com/Sanjai-Quest/TriNetra-AI_Mobile`

## 1. Executive Summary

TriNetra has progressed from a legacy evidence/verdict prototype to a working V3 logistics-control path.

The verified path is:

```text
Mobile physical identity capture
        -> V3 verification API
        -> persisted checkpoint
        -> deterministic identity and continuity decision
        -> custody and audit records
        -> release gate
        -> React investigation view
```

The core V3 proof is executable:

```text
Checkpoint 1: SN001 / IMEI001 -> PASS
Handoff: persisted
Checkpoint 2: SN999 / IMEI999 -> HOLD
Release/handoff after HOLD -> rejected
Web investigation: identity, checkpoints, custody, and audit visible
```

Three real complaint records from the xscrapper dataset were also connected to controlled serialized-unit demo records and tested through the same backend and web flow.

## 2. Work Completed

### Decision core

- Replaced compact-backend weight, timestamp, and legacy SKU verdict logic with deterministic V3 evaluation.
- Added explicit `PASS`, `REVIEW`, and `HOLD` decisions.
- Added serial reconciliation.
- Added IMEI reconciliation where applicable.
- Added continuity-break HOLD behavior.
- Added mandatory-evidence and custody validation.
- Added damaged/tampered condition REVIEW behavior.
- Added reasoning text based on actual decision inputs.
- Added regression coverage proving legacy weight and timestamp changes do not change the V3 result.

### Logistics-control layer

- Added `SerializedUnit` persistence.
- Added `Verification` persistence and workflow state.
- Added ordered `Checkpoint` persistence.
- Added sequence validation.
- Added previous-checkpoint identity comparison.
- Added immutable `CustodyEvent` persistence.
- Added `AuditEvent` persistence for verification, checkpoint, handoff, and release actions.
- Added client-event idempotency for checkpoints and custody events.
- Added server-side release gating.
- Prevented custody transitions from overwriting `HOLD` or `REVIEW`.
- Added aggregate investigation retrieval.

### Mobile application

- Added observed physical serial input.
- Added observed IMEI input.
- Persisted captured identity in the Room offline record.
- Migrated WorkManager sync toward the V3 verification/checkpoint API.
- Added Room schema version update.
- Verified the Android APK builds and installs on a physical device.
- Verified the phone can reach the backend through `adb reverse`.

### React web application

- Connected the existing Phase 2 dashboard to `/api/verifications`.
- Connected detail view to `/api/verifications/{id}/investigation`.
- Added V3 status filters: `PASS`, `REVIEW`, `HOLD`, and all.
- Added expected and observed serial/IMEI display.
- Added checkpoint display.
- Added custody and audit counts.
- Added xscrapper source case, platform, and complaint type display.
- Corrected queue badges to display V3 decision names.

### Documentation and source control

- Added `DECISION_ENGINE.md`.
- Added `V3_LOGISTICS_API.md`.
- Added `REAL_DATA_DEMO.md`.
- Added this report.
- Initialized Git locally because the original workspace was not a Git repository.
- Added the supplied GitHub repository as `origin`.
- Rebuilt the branch on top of upstream `main` so GitHub could compare it normally.
- Published the `v3-logistics-control` branch.

## 3. Authoritative V3 Decision Rules

The authoritative decision path is:

```text
CheckpointRequest
      -> LogisticsService
      -> VerdictService
      -> Verdict(decision, conflictType, reasoning)
      -> persisted Checkpoint and Verification state
      -> API and React investigation view
```

Authoritative signals:

- Expected serial versus observed serial.
- Expected IMEI versus observed IMEI.
- Previous checkpoint identity versus current checkpoint identity.
- Required evidence completeness.
- Custody validity.
- Structured condition status.

Decision semantics:

| Situation | Result |
| --- | --- |
| Matching identity, complete required evidence, valid continuity, normal condition | `PASS` |
| Matching identity with damaged/tampered condition | `REVIEW` |
| Missing identity, evidence, or custody information | `REVIEW` |
| Serial mismatch | `HOLD` |
| IMEI mismatch | `HOLD` |
| Identity changes between checkpoints | `HOLD` |

The following do not determine V3 decisions:

- Weight or weight delta.
- Laplacian variance.
- Canny or edge density.
- Temperature or humidity.
- Wear metrics.
- ML fraud scores.
- Voice output.
- AI recommendations.

## 4. End-to-End Application Flow

### 4.1 Mobile operator flow

```text
Open TriNetra Android app
        |
        v
Load or identify the expected unit
        |
        v
QR/package lookup loads expected record
        |
        v
Capture physical serial and IMEI
        |
        v
Confirm identity and condition
        |
        v
Capture optional camera, voice, and telemetry evidence
        |
        v
Store the event in Room when offline
        |
        v
WorkManager syncs when network is available
        |
        v
POST /api/verifications
POST /api/verifications/{id}/checkpoints
```

The QR/package identifier is a lookup aid. It is not identity proof. The physical serial/IMEI values are reconciled by the backend.

### 4.2 Backend flow

```text
POST /api/verifications
        |
        v
Verification state: IDENTITY_PENDING
        |
        v
POST /api/verifications/{id}/checkpoints
        |
        v
Load SerializedUnit expected identity
        |
        v
Compare current physical identity with expected identity
        |
        v
Compare current identity with previous checkpoint
        |
        v
Run VerdictService
        |
        v
Persist Checkpoint + Verification state + AuditEvent
        |
        v
PASS / REVIEW / HOLD
```

For a handoff:

```text
POST /api/verifications/{id}/custody
        |
        v
Allowed only while verification is PASS
        |
        v
Persist immutable CustodyEvent and AuditEvent
```

For release:

```text
POST /api/verifications/{id}/release
        |
        +-- actor role not SUPERVISOR/ADMIN -> HTTP 403
        +-- verification not PASS -> HTTP 409
        +-- valid PASS and authorized actor -> RELEASED
```

### 4.3 React investigator flow

```text
React app loads http://localhost:3000
        |
        v
GET /api/verifications
        |
        v
Queue displays persisted verification states
        |
        v
Investigator selects a verification
        |
        v
GET /api/verifications/{id}/investigation
        |
        v
Display:
  expected/observed identity
  decision
  checkpoints
  custody history
  audit events
  xscrapper source context
```

The React app is presentation-only. It does not calculate or authorize decisions.

## 5. API Surface

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/verifications` | Open a verification for a serialized unit. |
| `GET` | `/api/verifications` | List persisted verifications for the investigator queue. |
| `GET` | `/api/verifications/{id}` | Retrieve verification state. |
| `POST` | `/api/verifications/{id}/checkpoints` | Submit an ordered physical identity checkpoint. |
| `GET` | `/api/verifications/{id}/checkpoints` | Retrieve ordered checkpoints. |
| `POST` | `/api/verifications/{id}/custody` | Persist a handoff event. |
| `GET` | `/api/verifications/{id}/custody` | Retrieve custody history. |
| `GET` | `/api/verifications/{id}/audit` | Retrieve immutable audit events. |
| `GET` | `/api/verifications/{id}/investigation` | Retrieve the complete investigation view. |
| `POST` | `/api/verifications/{id}/release` | Enforce server-side release control. |

The legacy `/api/claims/*` endpoints remain for compatibility with the older compact demo path. The V3 logistics flow uses `/api/verifications/*`.

## 6. Real Dataset Demo

Source file:

```text
TriNetra AI/xscrapper/trinetra_real_complaints_expanded.csv
```

The dataset supplies complaint provenance and classifications. It does not contain physical serial/IMEI values, so controlled demo identities are used for the physical verification fields.

| Dataset record | Source | Complaint | Demo identity observation | V3 result |
| --- | --- | --- | --- | --- |
| `CMP_00001` | Ajio | Pickup Failure | Wrong observed serial | `HOLD` |
| `CMP_00002` | Amazon | Counterfeit Product | Wrong observed serial | `HOLD` |
| `CMP_00003` | Meesho | Used Product | Matching serial plus `DAMAGED` condition | `REVIEW` |

Seeded unit IDs:

```text
XS-CMP-00001
XS-CMP-00002
XS-CMP-00003
```

The React investigation page displays each source case ID, platform, complaint type, physical identity, checkpoint, and decision.

## 7. Primary Substitution Demo

### Setup

```powershell
# Terminal 1: backend
Set-Location backend
mvn spring-boot:run

# Terminal 2: web
Set-Location 'TriNetra AI/phase-2/frontend-react'
npm run dev

# Terminal 3: phone connection
adb devices
adb reverse tcp:8080 tcp:8080
```

Install and launch the Android app:

```powershell
Set-Location <repository-root>
.\gradlew.bat :app:assembleDebug
adb -s <device-id> install -r android/app/build/outputs/apk/debug/app-debug.apk
adb -s <device-id> shell monkey -p com.trinetra.ai 1
```

Open:

```text
http://localhost:3000
```

### Scenario

```text
Unit: UNIT-SN001
Expected: SN001 / IMEI001

Checkpoint 1:
Observed SN001 / IMEI001
Expected result: PASS

Handoff:
Operator A -> Operator B
Expected result: persisted custody event

Checkpoint 2:
Observed SN999 / IMEI999
Expected result: HOLD / continuity conflict

Release attempt by OPERATOR:
Expected result: HTTP 403 or release rejection

Investigation view:
Expected result: checkpoints, custody, audit, identity, and reasoning visible
```

### Real-data scenarios

Run the three seeded `XS-CMP-*` units through the same phone/backend path:

```text
CMP_00001 -> wrong serial -> HOLD
CMP_00002 -> wrong serial -> HOLD
CMP_00003 -> matching serial + damaged condition -> REVIEW
```

## 8. Tests and Evidence

Verified during this implementation:

- Backend Maven tests: passed, including the V3 decision-core suite.
- Android unit tests: passed.
- Android debug APK build: passed.
- Android APK installation on physical device `RMX1901`: passed.
- Android `MainActivity` launch: passed.
- No fatal Android crash found in recent logcat.
- React production build: passed.
- Backend startup with H2 schema initialization: passed.
- Live PASS API scenario: passed.
- Live REVIEW API scenario: passed.
- Live serial/IMEI mismatch HOLD scenario: passed.
- Three-checkpoint continuity scenario: `PASS`, `PASS`, `HOLD`.
- Duplicate checkpoint idempotency: passed.
- Out-of-order checkpoint rejection: HTTP `409`.
- Unauthorized operator release: HTTP `403`.
- Authorized supervisor release from PASS: passed.
- Post-HOLD custody handoff rejection: HTTP `409`.
- Phone-to-backend request over `adb reverse`: passed.
- React queue displayed real `HOLD`, `HOLD`, and `REVIEW` records.
- React detail displayed `CMP_00003`, Meesho, Used Product, and REVIEW.

## 9. Known Limitations

- Local development currently uses H2 in-memory storage; data resets when the backend restarts.
- The Phase 2 web dashboard is now connected to the compact V3 investigation API, but its old override modal remains unsupported by the compact backend.
- Authentication is not yet backed by signed user identity. The release service validates the supplied role string, not a verified JWT/session identity.
- The phone sync path has been migrated toward V3 verification/checkpoint submission, but full camera evidence upload and complete multi-checkpoint mobile UI orchestration remain additional work.
- Voice and AI remain optional input/assistance layers and are not authoritative.
- Real BLE hardware provisioning, signature validation, and replay protection are not production-complete.
- Office Kit integration is not implemented.
- The three xscrapper records provide real complaint provenance, not real device serial/IMEI evidence.

## 10. Current Conclusion

The repository now demonstrates a working deterministic logistics-control proof across backend, mobile runtime, and React investigation display. It can detect a persisted checkpoint substitution, place the verification on HOLD, preserve custody and audit history, reject invalid release/handoff actions, and show the investigation context in the web application.

It should be described as a verified V3 demo milestone, not a production-complete logistics platform.
