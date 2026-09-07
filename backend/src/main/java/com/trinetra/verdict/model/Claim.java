package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @Column(name = "claim_id", length = 36)
    private String claimId;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "outbound_sku", nullable = false)
    private String outboundSku;

    @Column(name = "outbound_weight_grams", nullable = false)
    private Double outboundWeightGrams;

    @Column(name = "return_sku", nullable = false)
    private String returnSku;

    @Column(name = "expected_serial")
    private String expectedSerial;

    @Column(name = "observed_serial")
    private String observedSerial;

    @Column(name = "expected_imei")
    private String expectedImei;

    @Column(name = "observed_imei")
    private String observedImei;

    @Column(name = "condition_status")
    private String conditionStatus;

    @Builder.Default
    @Column(name = "continuity_broken", nullable = false)
    private boolean continuityBroken = false;

    @Builder.Default
    @Column(name = "mandatory_evidence_complete", nullable = false)
    private boolean mandatoryEvidenceComplete = true;

    @Builder.Default
    @Column(name = "custody_valid", nullable = false)
    private boolean custodyValid = true;

    @Column(name = "return_weight_grams", nullable = false)
    private Double returnWeightGrams;

    @Column(name = "outbound_timestamp", nullable = false)
    private LocalDateTime outboundTimestamp;

    @Column(name = "return_timestamp", nullable = false)
    private LocalDateTime returnTimestamp;

    @Column(name = "delivery_timestamp", nullable = false)
    private LocalDateTime deliveryTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status;
}
