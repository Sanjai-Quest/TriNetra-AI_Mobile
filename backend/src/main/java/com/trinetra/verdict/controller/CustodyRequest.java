package com.trinetra.verdict.controller;

import lombok.Data;

@Data
public class CustodyRequest {
    private String custodyEventId;
    private String clientEventId;
    private String checkpointId;
    private String fromActor;
    private String toActor;
    private String location;
    private String action;
    private String occurredAt;
}
