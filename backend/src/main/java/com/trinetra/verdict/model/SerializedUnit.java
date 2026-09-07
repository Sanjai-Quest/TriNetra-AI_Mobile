package com.trinetra.verdict.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "serialized_units")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerializedUnit {
    @Id
    @Column(name = "unit_id", length = 64)
    private String unitId;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "product", nullable = false, length = 128)
    private String product;

    @Column(name = "expected_serial", nullable = false, length = 128)
    private String expectedSerial;

    @Column(name = "expected_imei", length = 128)
    private String expectedImei;

    @Column(name = "source_case_id", length = 64)
    private String sourceCaseId;

    @Column(name = "source_platform", length = 64)
    private String sourcePlatform;

    @Column(name = "source_complaint_type", length = 128)
    private String sourceComplaintType;

    @Column(name = "source_summary", columnDefinition = "TEXT")
    private String sourceSummary;
}
