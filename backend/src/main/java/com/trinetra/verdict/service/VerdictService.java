package com.trinetra.verdict.service;

import com.trinetra.verdict.model.Claim;
import com.trinetra.verdict.model.ConflictType;
import com.trinetra.verdict.model.DecisionStatus;
import com.trinetra.verdict.model.Evidence;
import com.trinetra.verdict.model.Verdict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Core Deterministic Verdict Engine for TriNetra AI.
 * 
 * The authoritative V3 decision path is deterministic. Legacy telemetry such as
 * weight, image metrics, and timestamps is intentionally absent from this service.
 */
@Service
public class VerdictService {

    public Verdict computeVerdict(Claim claim, List<Evidence> evidence) {
        if (claim == null) {
            throw new IllegalArgumentException("Claim cannot be null");
        }

        List<Evidence> safeEvidence = (evidence == null) ? List.of() : evidence;

        if (!hasText(claim.getExpectedSerial()) || !hasText(claim.getObservedSerial())) {
            return buildVerdict(claim.getClaimId(), DecisionStatus.REVIEW, ConflictType.IDENTITY_MISSING,
                    "Serial identity is incomplete. Expected and observed serials are required before release.");
        }
        if (!same(claim.getExpectedSerial(), claim.getObservedSerial())) {
            return buildVerdict(claim.getClaimId(), DecisionStatus.HOLD, ConflictType.SERIAL_MISMATCH,
                    "Serial mismatch detected. Expected: " + claim.getExpectedSerial() + ". Observed: "
                            + claim.getObservedSerial() + ". Status: HOLD.");
        }
        if (hasText(claim.getExpectedImei()) || hasText(claim.getObservedImei())) {
            if (!hasText(claim.getExpectedImei()) || !hasText(claim.getObservedImei())) {
                return buildVerdict(claim.getClaimId(), DecisionStatus.REVIEW, ConflictType.IDENTITY_MISSING,
                        "IMEI identity is incomplete. Expected and observed IMEI are required for this unit.");
            }
            if (!same(claim.getExpectedImei(), claim.getObservedImei())) {
                return buildVerdict(claim.getClaimId(), DecisionStatus.HOLD, ConflictType.IMEI_MISMATCH,
                        "IMEI mismatch detected. Expected: " + claim.getExpectedImei() + ". Observed: "
                                + claim.getObservedImei() + ". Status: HOLD.");
            }
        }
        if (claim.isContinuityBroken()) {
            return buildVerdict(claim.getClaimId(), DecisionStatus.HOLD, ConflictType.CONTINUITY_BROKEN,
                    "Checkpoint continuity failed for the serialized unit. Status: HOLD.");
        }
        if (!claim.isMandatoryEvidenceComplete() || safeEvidence.isEmpty()) {
            return buildVerdict(claim.getClaimId(), DecisionStatus.REVIEW, ConflictType.MISSING_MANDATORY_EVIDENCE,
                    "Mandatory verification evidence is incomplete. Status: REVIEW.");
        }
        if (!claim.isCustodyValid()) {
            return buildVerdict(claim.getClaimId(), DecisionStatus.REVIEW, ConflictType.CUSTODY_INVALID,
                    "Custody history is incomplete or inconsistent. Status: REVIEW.");
        }
        if ("DAMAGED".equalsIgnoreCase(claim.getConditionStatus())
                || "TAMPERED".equalsIgnoreCase(claim.getConditionStatus())) {
            return buildVerdict(claim.getClaimId(), DecisionStatus.REVIEW, ConflictType.CONDITION_REVIEW,
                    "Identity matches. Condition is marked " + claim.getConditionStatus().toUpperCase()
                            + ". Supervisor review required. Status: REVIEW.");
        }
        return buildVerdict(claim.getClaimId(), DecisionStatus.PASS, ConflictType.NONE,
                "Serial matched. IMEI matched where applicable. Required evidence complete. "
                        + "Checkpoint continuity intact. Custody history consistent. Status: PASS.");
    }

        public Verdict evaluateCheckpoint(String expectedSerial, String observedSerial,
                          String expectedImei, String observedImei,
                          boolean continuityBroken, String checkpointId) {
        Claim checkpointClaim = Claim.builder()
            .claimId(checkpointId)
            .merchantId("checkpoint")
            .customerId("operator")
            .orderId(checkpointId)
            .outboundSku("v3")
            .returnSku("v3")
            .outboundWeightGrams(0.0)
            .returnWeightGrams(0.0)
            .expectedSerial(expectedSerial)
            .observedSerial(observedSerial)
            .expectedImei(expectedImei)
            .observedImei(observedImei)
            .conditionStatus("NORMAL")
            .continuityBroken(continuityBroken)
            .mandatoryEvidenceComplete(true)
            .custodyValid(true)
            .status(com.trinetra.verdict.model.ClaimStatus.PENDING)
            .build();
        return computeVerdict(checkpointClaim, List.of(Evidence.builder()
            .evidenceId("checkpoint-evidence-" + checkpointId)
            .claimId(checkpointId)
                .source(com.trinetra.verdict.model.EvidenceSource.COURIER_VOICE)
            .payloadJson("{}")
            .createdAt(LocalDateTime.now())
            .build()));
        }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean same(String expected, String observed) {
        return expected.trim().equalsIgnoreCase(observed.trim());
    }

    private Verdict buildVerdict(String claimId, DecisionStatus decision, ConflictType conflictType, String reasoning) {
        return Verdict.builder()
                .verdictId(UUID.randomUUID().toString())
                .claimId(claimId)
                .conflictType(conflictType)
                .decision(decision)
                .reasoning(reasoning)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
