package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "investigations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investigation {
    @Id
    @Column(name = "investigation_id", length = 64)
    private String investigationId;

    @Column(name = "verification_id", nullable = false, length = 64)
    private String verificationId;

    @Column(name = "unit_id", nullable = false, length = 64)
    private String unitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private InvestigationStatus status;

    @Column(name = "reason", nullable = false, length = 64)
    private String reason;

    @Column(name = "expected_serial", nullable = false, length = 128)
    private String expectedSerial;

    @Column(name = "observed_serial", nullable = false, length = 128)
    private String observedSerial;

    @Column(name = "last_matching_checkpoint_id", length = 64)
    private String lastMatchingCheckpointId;

    @Column(name = "first_divergence_checkpoint_id", length = 64)
    private String firstDivergenceCheckpointId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}