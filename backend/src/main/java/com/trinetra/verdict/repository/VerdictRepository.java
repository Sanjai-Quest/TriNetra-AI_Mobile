package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.Verdict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerdictRepository extends JpaRepository<Verdict, String> {
    Optional<Verdict> findByClaimId(String claimId);
}
