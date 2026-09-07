package com.trinetra.verdict.controller;

import com.trinetra.verdict.model.Claim;
import com.trinetra.verdict.model.ClaimStatus;
import com.trinetra.verdict.model.ConflictType;
import com.trinetra.verdict.model.DecisionStatus;
import com.trinetra.verdict.model.Verdict;
import com.trinetra.verdict.repository.ClaimRepository;
import com.trinetra.verdict.repository.EvidenceRepository;
import com.trinetra.verdict.repository.VerdictRepository;
import com.trinetra.verdict.service.GroqService;
import com.trinetra.verdict.service.VerdictService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*")
public class VerdictController {

    private final VerdictService verdictService;
    private final ClaimRepository claimRepository;
    private final EvidenceRepository evidenceRepository;
    private final VerdictRepository verdictRepository;
    private final GroqService groqService;

    public VerdictController(
            VerdictService verdictService,
            ClaimRepository claimRepository,
            EvidenceRepository evidenceRepository,
            VerdictRepository verdictRepository,
            GroqService groqService) {
        this.verdictService = verdictService;
        this.claimRepository = claimRepository;
        this.evidenceRepository = evidenceRepository;
        this.verdictRepository = verdictRepository;
        this.groqService = groqService;
    }

    @PostMapping("/verdict")
    public ResponseEntity<?> generateVerdict(@RequestBody VerdictRequest request) {
        if (request == null || request.getClaim() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Malformed request payload: 'claim' object is required.");
        }

        try {
            Claim claim = request.getClaim();
            // Save or update claim entity
            claimRepository.save(claim);

            // Save evidence payloads if provided
            if (request.getEvidence() != null && !request.getEvidence().isEmpty()) {
                evidenceRepository.saveAll(request.getEvidence());
            }

            // Compute deterministic verdict
            Verdict verdict = verdictService.computeVerdict(claim, request.getEvidence());
            verdictRepository.save(verdict);

            // Update Claim Status
            claim.setStatus(claimStatusFor(verdict));
            claimRepository.save(claim);

            VerdictResponse response = VerdictResponse.builder()
                    .verdictId(verdict.getVerdictId())
                    .claimId(verdict.getClaimId())
                    .conflictType(verdict.getConflictType())
                    .decision(verdict.getDecision())
                    .reasoning(verdict.getReasoning())
                    .timestamp(verdict.getGeneratedAt())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Verdict Engine Exception: " + e.getMessage());
        }
    }

    @PostMapping("/telemetry")
    public ResponseEntity<?> receiveTelemetry(@RequestBody String telemetryJson) {
        if (telemetryJson == null || telemetryJson.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Malformed telemetry payload.");
        }
        try {
            JsonNode root = new ObjectMapper().readTree(telemetryJson);
            JsonNode claimNode = root.path("claim");
            if (claimNode.isMissingNode() || claimNode.isNull()) {
                System.out.println("[WORKMANAGER SYNC RECEIVER] Legacy telemetry payload acknowledged:");
                System.out.println(telemetryJson);
                return ResponseEntity.ok("{\"status\":\"SYNC_SUCCESS\",\"received_at\":\"" + LocalDateTime.now() + "\"}");
            }
            ObjectNode claimFields = (ObjectNode) claimNode.deepCopy();
            claimFields.remove(List.of("caseId", "packageId", "productName"));
            Claim claim = new ObjectMapper().treeToValue(claimFields, Claim.class);
            claimRepository.save(claim);

            List<com.trinetra.verdict.model.Evidence> evidence = new ObjectMapper()
                    .readerForListOf(com.trinetra.verdict.model.Evidence.class)
                    .readValue(root.path("evidence").toString());
            if (!evidence.isEmpty()) {
                evidenceRepository.saveAll(evidence);
            }

            Verdict verdict = verdictService.computeVerdict(claim, evidence);
            verdictRepository.save(verdict);
                claim.setStatus(claimStatusFor(verdict));
            claimRepository.save(claim);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "SYNC_SUCCESS");
            response.put("caseId", claim.getClaimId());
            response.put("state", stateFor(verdict));
            response.put("conflict", verdict.getConflictType());
            response.put("received_at", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Malformed telemetry payload: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listClaims() {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Claim claim : claimRepository.findAll()) {
            results.add(claimSummary(claim));
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<?> getClaim(@PathVariable String claimId) {
        return claimRepository.findByClaimId(claimId)
                .map(claim -> ResponseEntity.ok(claimDetail(claim)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{claimId}/timeline")
    public ResponseEntity<?> getTimeline(@PathVariable String claimId) {
        if (claimRepository.findByClaimId(claimId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Map<String, Object>> events = new ArrayList<>();
        evidenceRepository.findByClaimId(claimId).forEach(item -> {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", item.getSource().name());
            event.put("timestamp", item.getCreatedAt());
            event.put("payload", item.getPayloadJson());
            events.add(event);
        });
        return ResponseEntity.ok(events);
    }

    private Map<String, Object> claimSummary(Claim claim) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("claimId", claim.getClaimId());
        result.put("caseId", claim.getClaimId());
        result.put("orderId", claim.getOrderId());
        result.put("product", "Smartphone");
        result.put("status", claim.getStatus());
        result.put("outboundWeightGrams", claim.getOutboundWeightGrams());
        result.put("returnWeightGrams", claim.getReturnWeightGrams());
        result.put("state", latestState(claim));
        result.put("updatedAt", claim.getReturnTimestamp());
        return result;
    }

    private Map<String, Object> claimDetail(Claim claim) {
        Map<String, Object> result = claimSummary(claim);
        result.put("outboundSku", claim.getOutboundSku());
        result.put("returnSku", claim.getReturnSku());
        result.put("differencePercent", differencePercent(claim));
        List<com.trinetra.verdict.model.Evidence> evidence = evidenceRepository.findByClaimId(claim.getClaimId());
        result.put("evidence", evidence);
        Verdict verdict = verdictRepository.findByClaimId(claim.getClaimId())
                .orElseGet(() -> verdictService.computeVerdict(claim, evidence));
        result.put("conflictType", verdict.getConflictType());
        result.put("reasoning", verdict.getReasoning());
        result.put("generatedAt", verdict.getGeneratedAt());
        result.put("state", stateFor(verdict));
        return result;
    }

    private String latestState(Claim claim) {
        return verdictRepository.findByClaimId(claim.getClaimId())
                .map(this::stateFor)
                .orElse(claim.getStatus().name());
    }

    private String stateFor(Verdict verdict) {
        return verdict.getDecision().name();
    }

    private ClaimStatus claimStatusFor(Verdict verdict) {
        return switch (verdict.getDecision()) {
            case PASS -> ClaimStatus.CONSISTENT;
            case HOLD -> ClaimStatus.CONFLICT_DETECTED;
            case REVIEW -> ClaimStatus.INVESTIGATING;
        };
    }

    private double differencePercent(Claim claim) {
        if (claim.getOutboundWeightGrams() == null || claim.getOutboundWeightGrams() == 0
                || claim.getReturnWeightGrams() == null) {
            return 0.0;
        }
        return Math.abs(claim.getReturnWeightGrams() - claim.getOutboundWeightGrams())
                / claim.getOutboundWeightGrams() * 100.0;
    }

    @GetMapping("/{claimId}/verdict")
    public ResponseEntity<?> evaluateSeededClaim(@PathVariable String claimId) {
        var claimOpt = claimRepository.findByClaimId(claimId);
        if (claimOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Claim ID '" + claimId + "' not found.");
        }
        Claim claim = claimOpt.get();
        var evidenceList = evidenceRepository.findByClaimId(claimId);
        Verdict verdict = verdictService.computeVerdict(claim, evidenceList);

        VerdictResponse response = VerdictResponse.builder()
                .verdictId(verdict.getVerdictId())
                .claimId(verdict.getClaimId())
                .conflictType(verdict.getConflictType())
                .decision(verdict.getDecision())
                .reasoning(verdict.getReasoning())
                .timestamp(verdict.getGeneratedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/voice/parse
     * Accepts a plain-text courier voice transcript, forwards it to Groq LLM,
     * and returns the structured JSON assessment. Used by the Android mobile app.
     *
     * Body: raw transcript string (text/plain)
     * Response: Groq LLM JSON assessment (application/json)
     */
    @PostMapping(value = "/voice/parse",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> parseVoiceTranscript(@RequestBody String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Transcript body is required and must not be blank.\"}");
        }
        try {
            String llmResult = groqService.analyzeTranscript(transcript.trim());
            System.out.println("[GROQ VOICE PARSE] transcript='" + transcript.trim() + "' → " + llmResult);
            return ResponseEntity.ok(llmResult);
        } catch (Exception e) {
            System.err.println("[GROQ VOICE PARSE ERROR] " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\":\"Groq LLM unavailable: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
