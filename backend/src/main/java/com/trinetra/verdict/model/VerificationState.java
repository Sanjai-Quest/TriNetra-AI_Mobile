package com.trinetra.verdict.model;

public enum VerificationState {
    RECEIVED,
    IDENTITY_PENDING,
    IDENTITY_VERIFIED,
    READY_FOR_DECISION,
    PASS,
    REVIEW,
    HOLD,
    HANDOFF,
    RELEASED,
    INVESTIGATION
}
