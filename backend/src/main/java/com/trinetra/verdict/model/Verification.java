package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {
    @Id
    @Column(name = "verification_id", length = 64)
    private String verificationId;

    @Column(name = "unit_id", nullable = false, length = 64)
    private String unitId;

    @Column(name = "operator_id", nullable = false, length = 64)
    private String operatorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 32)
    private VerificationState state;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
