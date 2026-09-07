package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestigationRepository extends JpaRepository<Investigation, String> {
    List<Investigation> findByUnitIdOrderByCreatedAtAsc(String unitId);
    Optional<Investigation> findByVerificationId(String verificationId);
}