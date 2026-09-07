package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.CustodyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustodyEventRepository extends JpaRepository<CustodyEvent, String> {
    List<CustodyEvent> findByVerificationIdOrderByOccurredAtAsc(String verificationId);
    Optional<CustodyEvent> findByClientEventId(String clientEventId);
}
