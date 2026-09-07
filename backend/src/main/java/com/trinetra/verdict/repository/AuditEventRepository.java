package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {
    List<AuditEvent> findByVerificationIdOrderByCreatedAtAsc(String verificationId);
}
