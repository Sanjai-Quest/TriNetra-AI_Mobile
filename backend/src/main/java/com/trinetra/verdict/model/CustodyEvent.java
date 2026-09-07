package com.trinetra.verdict.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "custody_events", uniqueConstraints = @UniqueConstraint(name = "uk_custody_event", columnNames = "client_event_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustodyEvent {
    @Id
    @Column(name = "custody_event_id", length = 64)
    private String custodyEventId;

    @Column(name = "verification_id", nullable = false, length = 64)
    private String verificationId;

    @Column(name = "checkpoint_id", nullable = false, length = 64)
    private String checkpointId;

    @Column(name = "from_actor", length = 64)
    private String fromActor;

    @Column(name = "to_actor", nullable = false, length = 64)
    private String toActor;

    @Column(name = "location", length = 128)
    private String location;

    @Column(name = "action", nullable = false, length = 32)
    private String action;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "client_event_id", nullable = false, length = 64)
    private String clientEventId;
}
