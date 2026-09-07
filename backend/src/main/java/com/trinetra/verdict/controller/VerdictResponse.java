package com.trinetra.verdict.controller;

import com.trinetra.verdict.model.ConflictType;
import com.trinetra.verdict.model.DecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerdictResponse {
    private String verdictId;
    private String claimId;
    private ConflictType conflictType;
    private DecisionStatus decision;
    private String reasoning;
    private LocalDateTime timestamp;
}
