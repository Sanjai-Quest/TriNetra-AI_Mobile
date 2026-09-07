package com.trinetra.verdict.service;

import com.trinetra.verdict.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class V3DecisionEngineTest {

    private final VerdictService service = new VerdictService();
    private final LocalDateTime now = LocalDateTime.of(2026, 9, 7, 10, 0);

    @Test
    void matchingIdentityPasses() {
        Verdict verdict = service.computeVerdict(claim("SN001", "SN001", "IMEI001", "IMEI001"), evidence());

        assertEquals(DecisionStatus.PASS, verdict.getDecision());
        assertEquals(ConflictType.NONE, verdict.getConflictType());
    }

    @Test
    void serialMismatchHolds() {
        Verdict verdict = service.computeVerdict(claim("SN001", "SN999", "IMEI001", "IMEI001"), evidence());

        assertEquals(DecisionStatus.HOLD, verdict.getDecision());
        assertEquals(ConflictType.SERIAL_MISMATCH, verdict.getConflictType());
        assertTrue(verdict.getReasoning().contains("SN999"));
    }

    @Test
    void imeiMismatchHolds() {
        Verdict verdict = service.computeVerdict(claim("SN001", "SN001", "IMEI001", "IMEI999"), evidence());

        assertEquals(DecisionStatus.HOLD, verdict.getDecision());
        assertEquals(ConflictType.IMEI_MISMATCH, verdict.getConflictType());
    }

    @Test
    void continuityBreakHolds() {
        Claim claim = claim("SN001", "SN001", "IMEI001", "IMEI001");
        claim.setContinuityBroken(true);

        Verdict verdict = service.computeVerdict(claim, evidence());

        assertEquals(DecisionStatus.HOLD, verdict.getDecision());
        assertEquals(ConflictType.CONTINUITY_BROKEN, verdict.getConflictType());
    }

    @Test
    void missingEvidenceCannotPass() {
        Claim claim = claim("SN001", "SN001", "IMEI001", "IMEI001");
        claim.setMandatoryEvidenceComplete(false);

        Verdict verdict = service.computeVerdict(claim, evidence());

        assertEquals(DecisionStatus.REVIEW, verdict.getDecision());
        assertEquals(ConflictType.MISSING_MANDATORY_EVIDENCE, verdict.getConflictType());
    }

    @Test
    void damagedMatchingUnitRequiresReview() {
        Claim claim = claim("SN001", "SN001", "IMEI001", "IMEI001");
        claim.setConditionStatus("DAMAGED");

        Verdict verdict = service.computeVerdict(claim, evidence());

        assertEquals(DecisionStatus.REVIEW, verdict.getDecision());
        assertEquals(ConflictType.CONDITION_REVIEW, verdict.getConflictType());
    }

    @Test
    void legacyTelemetryCannotChangeVerdict() {
        Claim normal = claim("SN001", "SN001", "IMEI001", "IMEI001");
        normal.setOutboundWeightGrams(642.0);
        normal.setReturnWeightGrams(642.0);
        Verdict baseline = service.computeVerdict(normal, evidence());

        Claim changedTelemetry = claim("SN001", "SN001", "IMEI001", "IMEI001");
        changedTelemetry.setOutboundWeightGrams(642.0);
        changedTelemetry.setReturnWeightGrams(1.0);
        changedTelemetry.setOutboundTimestamp(now);
        changedTelemetry.setDeliveryTimestamp(now.plusDays(2));
        changedTelemetry.setReturnTimestamp(now.minusDays(1));
        Verdict changed = service.computeVerdict(changedTelemetry, evidence());

        assertEquals(baseline.getDecision(), changed.getDecision());
        assertEquals(baseline.getConflictType(), changed.getConflictType());
    }

    private Claim claim(String expectedSerial, String observedSerial, String expectedImei, String observedImei) {
        return Claim.builder()
                .claimId("case-1")
                .merchantId("merchant-1")
                .customerId("operator-1")
                .orderId("order-1")
                .outboundSku("legacy-value")
                .returnSku("legacy-value")
                .outboundWeightGrams(642.0)
                .returnWeightGrams(642.0)
                .outboundTimestamp(now)
                .deliveryTimestamp(now.plusDays(1))
                .returnTimestamp(now.plusDays(2))
                .expectedSerial(expectedSerial)
                .observedSerial(observedSerial)
                .expectedImei(expectedImei)
                .observedImei(observedImei)
                .conditionStatus("NORMAL")
                .status(ClaimStatus.PENDING)
                .build();
    }

    private List<Evidence> evidence() {
        return List.of(Evidence.builder()
                .evidenceId("evidence-1")
                .claimId("case-1")
                .source(EvidenceSource.COURIER_VOICE)
                .payloadJson("{\"condition\":\"NORMAL\"}")
                .createdAt(now)
                .build());
    }
}