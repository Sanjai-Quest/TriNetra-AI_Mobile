package com.trinetra.verdict.repository;

import com.trinetra.verdict.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, String> {
    Optional<Claim> findByClaimId(String claimId);
}
