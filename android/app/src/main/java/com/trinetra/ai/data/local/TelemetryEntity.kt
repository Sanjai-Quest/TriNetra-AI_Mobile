package com.trinetra.ai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room SQLite Telemetry Entity.
 * Represents an offline-cached evidence telemetry record captured at doorstep or warehouse docks.
 */
@Entity(tableName = "telemetry_records")
data class TelemetryEntity(
    @PrimaryKey
    val caseId: String = UUID.randomUUID().toString(),
    val orderId: String = "ORD-98402",
    val packageId: String = "TRN-PKG-7729-A",
    val productName: String = "Smartphone",
    val scannedSealId: String,
    val observedSerial: String? = null,
    val observedImei: String? = null,
    val rawTranscript: String,
    val parsedJson: String,
    val gpsLat: Double? = null,
    val gpsLon: Double? = null,
    val timestampUtc: String,
    val isSynced: Boolean = false,
    // Seeded outbound (warehouse dispatch) weight for the demo order — this is the
    // known-good baseline the return weight gets reconciled against.
    val outboundWeightGrams: Double = 642.0,
    // Weight actually captured from the BLE scale reading on this device. Null until
    // the operator triggers a scale read; the sync worker falls back to the outbound
    // value (i.e. no discrepancy) if no reading was taken, rather than a fabricated number.
    val returnWeightGrams: Double? = null,
    // Signed HMAC-SHA256 BLE telemetry packet JSON, if a scale reading was captured.
    val scaleSignatureJson: String? = null,
    val cameraMetricsJson: String? = null,
    val parserMode: String = "LOCAL_CPU_PARSER",
    val cameraMode: String = "CAMERAX_OPENCV",
    val scaleMode: String = "DEMO_GATT_SCALE"
)
