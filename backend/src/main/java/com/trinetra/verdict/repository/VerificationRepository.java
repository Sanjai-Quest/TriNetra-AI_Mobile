package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, String> {
}
