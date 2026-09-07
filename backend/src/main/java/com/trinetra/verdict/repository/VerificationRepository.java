package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRepository extends JpaRepository<Verification, String> {
	List<Verification> findByUnitIdOrderByCreatedAtAsc(String unitId);
}
