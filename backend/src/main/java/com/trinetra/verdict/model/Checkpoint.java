package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoints", uniqueConstraints = @UniqueConstraint(name = "uk_checkpoint_event", columnNames = "client_event_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkpoint {
    @Id
    @Column(name = "checkpoint_id", length = 64)
    private String checkpointId;

    @Column(name = "verification_id", nullable = false, length = 64)
    private String verificationId;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(name = "location", length = 128)
    private String location;

    @Column(name = "operator_id", nullable = false, length = 64)
    private String operatorId;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;

    @Column(name = "expected_serial", nullable = false, length = 128)
    private String expectedSerial;

    @Column(name = "observed_serial", nullable = false, length = 128)
    private String observedSerial;

    @Column(name = "expected_imei", length = 128)
    private String expectedImei;

    @Column(name = "observed_imei", length = 128)
    private String observedImei;

    @Column(name = "condition_status", length = 32)
    private String conditionStatus;

    @Column(name = "identity_result", nullable = false, length = 32)
    private String identityResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 16)
    private DecisionStatus decision;

    @Column(name = "client_event_id", nullable = false, length = 64)
    private String clientEventId;
}
