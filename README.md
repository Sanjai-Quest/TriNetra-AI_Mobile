# TriNetra AI

TriNetra AI is an evidence-first platform for delivery, return, and refund disputes. It helps teams capture field observations, reconcile them with the original shipment information, and present a clear evidence trail to an investigator.

This repository contains multiple implementation paths, but the core idea is consistent: convert noisy real-world signals into structured evidence, compare independent sources, and support human decision-making rather than making opaque final accusations.

## Project status

The current checkout includes:

- A working Android demo app for evidence capture.
- A standalone Spring Boot backend for verdict generation.
- A deterministic Python research engine for synthetic and reproducibility work.
- A larger Phase 2 event-driven platform and React dashboard.
- Phase 3 operational hardening and benchmarking assets.
- Datasets, reports, and validation materials.

The most relevant end-to-end path for the current project is the compact Android + backend demo. The Phase 2 and Phase 3 code is an enterprise-oriented reference stack, not a single deployable runtime.

## Core principle

TriNetra separates evidence collection from decision logic.

1. Field devices capture observations from the physical world.
2. The system converts these observations into structured evidence.
3. Independent signals are reconciled using explicit rules.
4. Investigators receive evidence, reasoning, and gaps instead of a hidden black-box decision.

Cloud parsing and local classifiers are ingestion aids only. They do not make the final high-stakes decision.

## Use cases

TriNetra is designed to support disputes involving:

- wrong or substituted items
- empty-box or damaged-package claims
- outbound vs return weight mismatches
- seal and packaging integrity issues

## Architecture
The compact V3 verdict engine checks rules in order:
1. Missing or mismatching physical serial produces REVIEW or HOLD.
2. Missing or mismatching IMEI produces REVIEW or HOLD when applicable.
3. A continuity break produces HOLD.
4. Missing mandatory evidence or invalid custody produces REVIEW.
5. A damaged or tampered matching unit produces REVIEW.
6. Matching identity with complete evidence produces PASS.

Weight, image metrics, timestamps, voice output, and AI recommendations do not
determine the V3 decision.
    B --> E[Camera / CV Metrics]
    B --> F[BLE Scale Reading]
    B --> G[Room Offline Storage]
    G --> H[WorkManager Sync]

    H --> I[Spring Boot Backend]
    I --> J[Claim + Evidence Store]
    I --> K[Deterministic Verdict Engine]
    K --> L[Conflict Detection]
    K --> M[Reasoning + Evidence Summary]

    J --> N[Investigator Dashboard]
    L --> N
    M --> N
```

### Compact demo path

```text
Android app <--> standalone Spring Boot backend (:8080)
     |
     +--> Room local storage
     +--> WorkManager sync retry
     +--> local voice fallback
     +--> CameraX preview and CV metrics
```

### Phase 2 reference platform

```text
React investigator dashboard
            |
            v
Claim Service (:8080) --> Evidence Service (:8081)
            |                     |
            +------ RabbitMQ ------+
            |                     |
            v                     v
Fraud Engine (:8083)      Verdict Generator (:8084)
            |
            v
PostgreSQL + Redis + MinIO
```

## Repository guide

| Path | Purpose |
| --- | --- |
| [android/app](android/app) | Kotlin Android demo client for capture, queueing, local persistence, camera metrics, QR reading, and BLE telemetry. |
| [backend](backend) | Standalone Java 21 + Spring Boot verdict engine used by the compact mobile demo. |
| [TriNetra AI/phase-1](TriNetra%20AI/phase-1) | Research and deterministic reconciliation engine with synthetic evaluation. |
| [TriNetra AI/phase-2](TriNetra%20AI/phase-2) | Event-driven services, multimodal processing, infrastructure, and dashboard reference stack. |
| [TriNetra AI/phase-3](TriNetra%20AI/phase-3) | Operational hardening, benchmarking, resilience, and load-test assets. |
| [datasets](datasets) | Complaint datasets, quality reports, taxonomy, and source analysis. |
| [testing](testing) | Validation reports, scenario scripts, and performance outputs. |
| [TRINETRA_MASTER_PROJECT_DOCUMENTATION.md](TRINETRA_MASTER_PROJECT_DOCUMENTATION.md) | Detailed project documentation and status notes. |
| [TRINETRA_COMPREHENSIVE_SPECIFICATION.md](TRINETRA_COMPREHENSIVE_SPECIFICATION.md) | Product and technical requirements blueprint. |
| [trinetra_master_production_blueprint.md](trinetra_master_production_blueprint.md) | Design and workflow blueprint for production planning. |
| [iqoo_15_ui_workflow.md](iqoo_15_ui_workflow.md) | Mobile workflow specification and field interaction notes. |

Build output directories such as Gradle caches, Maven target folders, and generated frontend artifacts are not source-of-truth implementation.

## Android mobile client

The Android client targets API 34 and is implemented in Kotlin with Jetpack Compose.

### Current app flows

- Handoff and case creation
- transcript capture and parsing
- QR/seal scanning
- BLE scale reading and signed telemetry
- CV inspection metrics
- Room persistence and WorkManager retry

### Relevant implementation points

- [MainActivity.kt](android/app/src/main/java/com/trinetra/ai/MainActivity.kt)
- [HandoffScreen.kt](android/app/src/main/java/com/trinetra/ai/ui/handoff/HandoffScreen.kt)
- [HandoffViewModel.kt](android/app/src/main/java/com/trinetra/ai/ui/handoff/HandoffViewModel.kt)
- [CameraScreen.kt](android/app/src/main/java/com/trinetra/ai/ui/cv/CameraScreen.kt)
- [OpenCVProcessor.kt](android/app/src/main/java/com/trinetra/ai/cv/OpenCVProcessor.kt)
- [VoiceClassifier.kt](android/app/src/main/java/com/trinetra/ai/nlp/VoiceClassifier.kt)
- [BluetoothScaleManager.kt](android/app/src/main/java/com/trinetra/ai/ble/BluetoothScaleManager.kt)
- [TelemetryUploadWorker.kt](android/app/src/main/java/com/trinetra/ai/data/sync/TelemetryUploadWorker.kt)

### Voice intake behavior

The current app attempts the configured backend voice parsing route when available and falls back to a local CPU-bound keyword classifier when not. This is an ingestion aid and not the final high-stakes decision engine.

The shipped fallback is not a true Whisper-style local ASR stack or a locally executed large-language model. It is a lightweight deterministic fallback.

### Camera and CV behavior

The camera preview is live. OpenCV-based Laplacian and Canny-style edge metrics are computed and shown in the UI. These values are attached as evidence metadata and are useful for inspection workflows, but they are not currently the sole determinant of the final verdict.

### BLE and weight path

The scale path was audited and fixed to preserve the exact chosen return weight. The earlier corruption bug mutated the selected value before telemetry was generated. This is now corrected for the demo path so the selected weight is retained without hidden arithmetic scaling.

The project continues to use a demo-scale model and not a production BLE hardware implementation. Real GATT provisioning, signature validation, and replay protection remain product work.

## Standalone backend

The root [backend](backend) module is a compact Spring Boot 3.2 service using Java 21, Spring Web, Spring Data JPA, H2 for local development and tests, and Lombok.

### API surface

Implemented endpoints include:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | /api/claims/verdict | Compute a verdict from claim and evidence data |
| POST | /api/claims/telemetry | Accept synced mobile telemetry |
| POST | /api/claims/voice/parse | Forward transcript text to configured parser |
| GET | /api/claims | List stored claims |
| GET | /api/claims/{claimId} | Get claim details |
| GET | /api/claims/{claimId}/verdict | Recompute verdict for a stored claim |

The backend is available at http://localhost:8080 in the local demo flow.

### Deterministic verdict rules

The compact verdict engine checks rules in order:

1. Identity mismatch between outbound and return SKU
2. Weight drop above tolerance threshold
3. Return timestamp before delivery timestamp
4. Missing or insufficient evidence
5. Otherwise consistent

The actual logic is implemented in [VerdictService.java](backend/src/main/java/com/trinetra/verdict/service/VerdictService.java).

### Verified status

The following checks were executed in the current repo state:

- Android unit tests passed.
- Backend V3 decision tests passed, including serial/IMEI mismatch, continuity,
    missing evidence, damaged condition, and legacy-telemetry invariance.
- Live POST to /api/claims/verdict returned PASS for matching identity, REVIEW
    for damaged matching identity, and HOLD for serial mismatch.

Known current gaps:

- The existing Phase 2 web dashboard is not yet connected to the compact V3 investigation endpoint.
- Authentication/RBAC is not yet backed by signed user identity; the release gate currently validates the supplied supervisor/admin role boundary.

Honest current gap:

- The claim-detail retrieval path still has an error in the current backend implementation and is not yet fully proven end-to-end; this was not hidden or papered over.

## Phase 1 research engine

Phase 1 focuses on reproducible evidence reconciliation and synthetic benchmarking.

```text
Evidence -> canonical normalization -> entity resolution -> baseline comparisons -> multi-source reconciliation -> metrics
```

Important modules:

- [canonical_normalizer.py](TriNetra%20AI/phase-1/normalization/canonical_normalizer.py)
- [entity_resolver.py](TriNetra%20AI/phase-1/resolution/entity_resolver.py)
- [reconciliation_engine.py](TriNetra%20AI/phase-1/engine/reconciliation_engine.py)
- [evaluator.py](TriNetra%20AI/phase-1/evaluation/evaluator.py)

These experiments are valuable for controlled benchmarking and reproducibility, but they are not proof of general live-world fraud detection performance.

## Phase 2 platform

Phase 2 expands the project into an event-driven platform with Spring Boot services, Redis, RabbitMQ, PostgreSQL, MinIO, and a React dashboard. It is a strong reference architecture for larger deployment planning, but it is not a single unified runtime in this repository snapshot.

## Phase 3 hardening

Phase 3 adds operational reference work such as circuit breakers, retries, logging, metrics, and load tests. These artifacts help with resilience planning, but they are not a substitute for real production validation.

## Setup and run

### Android demo

```powershell
cd TriNetra_Mobile
.\gradlew.bat clean assembleDebug
.\gradlew.bat :app:testDebugUnitTest --tests com.trinetra.ai.ble.BluetoothScaleManagerTest
```

### Backend

```powershell
cd TriNetra_Mobile\backend
mvn spring-boot:run
```

Then call the backend at:

- http://localhost:8080/api/claims/verdict
- http://localhost:8080/api/claims

### Phase 1 research

```powershell
cd TriNetra_Mobile\TriNetra AI
python -m pip install -r .\phase-1\requirements.txt
python phase-1/generate_and_evaluate.py
```

### Phase 2 services

```powershell
cd TriNetra_Mobile\TriNetra AI\phase-2
docker compose -f docker-compose.infra.yml up -d
docker compose -f docker-compose.yml up --build -d
```

## Data and validation assets

The repository includes:

- complaint datasets and taxonomies
- synthetic validation and performance reports
- demo-readiness summaries
- real-world validation output
- scenario test scripts

These materials are useful for benchmarking and demo preparation, but they should be treated as project evidence and not as universal performance guarantees.

## Security and production boundaries

The project includes local persistence, signed BLE payload scaffolding, structured evidence storage, and auditable reasoning. These are helpful controls, but before production deployment the project still needs:

- secure key management and rotation
- authenticated access control
- tenant isolation
- stronger BLE replay and freshness validation
- encrypted transport and storage
- formal evidence retention policies
- production-quality CV and voice pipelines
- dedicated security and integration validation

## Responsible use

TriNetra is decision support for investigations, not an autonomous accusation engine. Evidence may be incomplete, ambiguous, or noisy. Every result should remain traceable to its source, rule path, and reviewer decision.

The strongest contribution in the current codebase is the separation between:

- field evidence capture
- evidence persistence and auditability
- deterministic reconciliation rules
- human-led investigation and override

## Related documentation

- [TRINETRA_MASTER_PROJECT_DOCUMENTATION.md](TRINETRA_MASTER_PROJECT_DOCUMENTATION.md)
- [TRINETRA_COMPREHENSIVE_SPECIFICATION.md](TRINETRA_COMPREHENSIVE_SPECIFICATION.md)
- [trinetra_master_production_blueprint.md](trinetra_master_production_blueprint.md)
- [iqoo_15_ui_workflow.md](iqoo_15_ui_workflow.md)
- [TriNetra AI/phase-1/README.md](TriNetra%20AI/phase-1/README.md)
- [TriNetra AI/phase-2/README.md](TriNetra%20AI/phase-2/README.md)
- [TriNetra AI/phase-3/README.md](TriNetra%20AI/phase-3/README.md)
