# V3 Logistics API

The compact backend exposes the persisted logistics-control workflow at
`http://localhost:8080`.

## Open Verification

`POST /api/verifications`

```json
{"unitId":"UNIT-SN001","operatorId":"operator-a"}
```

Returns `201` with a verification in `IDENTITY_PENDING`. The seeded demo unit
is `UNIT-SN001` with expected identity `SN001 / IMEI001`.

## Submit Checkpoint

`POST /api/verifications/{verificationId}/checkpoints`

```json
{
  "checkpointId":"cp-1",
  "clientEventId":"evt-cp-1",
  "sequenceNumber":1,
  "location":"Hub A",
  "operatorId":"operator-a",
  "observedSerial":"SN001",
  "observedImei":"IMEI001"
}
```

The backend compares physical identity and the previous checkpoint. Duplicate
`clientEventId` submissions return the original checkpoint. Invalid sequence
numbers return `409 Conflict`.

## Custody and Investigation

- `POST /api/verifications/{id}/custody` records an immutable handoff.
- `GET /api/verifications/{id}/checkpoints` returns ordered checkpoints.
- `GET /api/verifications/{id}/custody` returns custody history.
- `GET /api/verifications/{id}/audit` returns audit events.
- `GET /api/verifications/{id}/investigation` returns all four views together.

## Release

`POST /api/verifications/{verificationId}/release`

```json
{"actorId":"supervisor-a","actorRole":"SUPERVISOR"}
```

Release requires persisted state `PASS` and role `SUPERVISOR` or `ADMIN`.
Operators receive `403`; a non-PASS verification receives `409`.