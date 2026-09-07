package com.trinetra.verdict.controller;

import com.trinetra.verdict.model.Claim;
import com.trinetra.verdict.model.Evidence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerdictRequest {
    private Claim claim;
    private List<Evidence> evidence;
}
