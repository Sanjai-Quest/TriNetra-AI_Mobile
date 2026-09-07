package com.trinetra.verdict.controller;

import lombok.Data;

@Data
public class CheckpointRequest {
    private String checkpointId;
    private String clientEventId;
    private int sequenceNumber;
    private String location;
    private String operatorId;
    private String capturedAt;
    private String observedSerial;
    private String observedImei;
}
