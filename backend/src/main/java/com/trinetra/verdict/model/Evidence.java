package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidence {

    @Id
    @Column(name = "evidence_id", length = 36)
    private String evidenceId;

    @Column(name = "claim_id", length = 36, nullable = false)
    private String claimId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private EvidenceSource source;

    @Column(name = "payload_json", columnDefinition = "TEXT", nullable = false)
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
