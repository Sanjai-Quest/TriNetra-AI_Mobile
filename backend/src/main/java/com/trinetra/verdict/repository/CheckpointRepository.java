package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckpointRepository extends JpaRepository<Checkpoint, String> {
    List<Checkpoint> findByVerificationIdOrderBySequenceNumberAsc(String verificationId);
    Optional<Checkpoint> findByClientEventId(String clientEventId);
}
