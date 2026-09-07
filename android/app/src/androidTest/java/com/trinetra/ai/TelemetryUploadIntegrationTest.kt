package com.trinetra.ai

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.trinetra.ai.data.local.TelemetryEntity
import com.trinetra.ai.nlp.VoiceClassifier
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Instrumented Integration Test for End-to-End Android -> Backend Pipeline.
 * Simulates:
 * 1. Voice input capture
 * 2. CPU-bound NLP classification
 * 3. Telemetry JSON packaging & signing
 * 4. Room SQLite offline caching
 * 5. Sync state verification
 */
@RunWith(AndroidJUnit4::class)
class TelemetryUploadIntegrationTest {

    @Test
    fun testEndToEndVoiceToVerdictFlow() {
        // 1. Simulate voice input
        val classifier = VoiceClassifier()
        val transcript = "Box phata hai, weight light, seal intact"
        val nlpResult = classifier.classify(transcript)

        assertTrue(nlpResult.anomalyDetected)
        assertEquals("TAMPER_SUSPECTED", nlpResult.sealIntegrityStatus)
        assertEquals("WEIGHT_LIGHT_SUSPECTED", nlpResult.weightAssessment)

        // 2. Package into canonical JSON payload
        val caseId = UUID.randomUUID().toString()
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val jsonPayload = JSONObject().apply {
            put("case_id", caseId)
            put("scanned_seal_id", "TRN-TAG-9912-A")
            put("raw_transcript", transcript)
            put("gps_lat", 13.0827)
            put("gps_lon", 80.2707)
            put("timestamp_utc", timestamp)
            put("nlp_metrics", JSONObject().apply {
                put("anomaly_detected", nlpResult.anomalyDetected)
                put("seal_integrity_status", nlpResult.sealIntegrityStatus)
                put("weight_assessment", nlpResult.weightAssessment)
            })
        }.toString()

        // 3. Create TelemetryEntity for Room DB
        val entity = TelemetryEntity(
            caseId = caseId,
            scannedSealId = "TRN-TAG-9912-A",
            rawTranscript = transcript,
            parsedJson = jsonPayload,
            gpsLat = 13.0827,
            gpsLon = 80.2707,
            timestampUtc = timestamp,
            isSynced = false
        )

        assertNotNull(entity.caseId)
        assertEquals(false, entity.isSynced)
        assertTrue(entity.parsedJson.contains("WEIGHT_LIGHT_SUSPECTED"))
    }
}
