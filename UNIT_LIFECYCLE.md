# V3 Unit Lifecycle

TriNetra follows the evidence state of a serialized physical unit, not generic parcel movement and not customer interaction.

## Lifecycle

```text
OUTBOUND_WAREHOUSE
    -> COURIER_HANDOFF
    -> FINAL_RELEASE
    -> CUSTOMER LIFECYCLE ENDPOINT
    -> RETURN_RECEIVING
    -> INVESTIGATION_RECHECK when required
```

The customer is a lifecycle endpoint. The customer does not operate TriNetra.

## Checkpoints

Each checkpoint belongs to a persisted verification and contains:

- sequence number
- checkpoint type
- location
- operator
- captured timestamp
- expected serial and IMEI
- observed serial and IMEI
- identity result
- V3 decision
- client event ID

Supported checkpoint types are `OUTBOUND_WAREHOUSE`, `COURIER_HANDOFF`,
`FINAL_RELEASE`, `RETURN_RECEIVING`, and `INVESTIGATION_RECHECK`.

## Continuity

The backend compares the expected unit identity and the previous observed identity.
A mismatch creates `CONTINUITY_BROKEN` and produces `HOLD`.

The persisted unit history endpoint is:

```text
GET /api/units/{unitId}/history
```

It returns all checkpoints, the first known divergence, and the last known matching checkpoint.

## First Known Divergence

Given:

```text
CP1 SN001 -> PASS
CP2 SN001 -> PASS
CP3 SN001 -> PASS
CP4 SN999 -> HOLD
```

The history response identifies:

```text
firstKnownDivergence: CP4
lastKnownMatchingCheckpoint: CP3
```

This is an evidence finding. It does not identify an offender or claim intent.

## Return Lifecycle

A return warehouse operator opens a verification for the historical serialized unit,
captures the returned serial/IMEI and condition, and submits a return checkpoint.
The backend compares the returned identity with the persisted historical identity.
A mismatch creates an open investigation and blocks release.
