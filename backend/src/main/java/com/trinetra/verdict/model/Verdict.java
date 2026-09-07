package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verdicts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verdict {

    @Id
    @Column(name = "verdict_id", length = 36)
    private String verdictId;

    @Column(name = "claim_id", length = 36, nullable = false)
    private String claimId;

    @Enumerated(EnumType.STRING)
    @Column(name = "conflict_type", nullable = false)
    private ConflictType conflictType;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private DecisionStatus decision;

    @Column(name = "reasoning", columnDefinition = "TEXT", nullable = false)
    private String reasoning;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
}
