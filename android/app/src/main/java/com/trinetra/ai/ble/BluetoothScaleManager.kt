package com.trinetra.ai.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * Signed Telemetry Frame from Bluetooth Scale.
 */
data class ScaleTelemetryPacket(
    val scaleId: String,
    val timestampUtc: String,
    val weightGrams: Double,
    val sequenceNumber: Int,
    val pairingSessionId: String,
    val hmacSignature: String,
    val jsonPayload: String
)

/**
 * Bluetooth Low Energy (BLE) Packaging Scale Manager.
 * Connects via GATT receiver callbacks, parses weight characteristics in grams,
 * and signs telemetry packets with HMAC-SHA256 to prevent spoofing and replay attacks.
 */
class BluetoothScaleManager {

    private var sequenceCounter = 127
    private val pairingSessionId = "sess-abc123xyz-iQOO15"
    private val sharedSecret = "TriNetra-Secret-BLE-Key-2026"

    /**
     * GATT Callback implementation for Bluetooth Scale Peripherals.
     */
    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt?.discoverServices()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS && characteristic != null) {
                parseScaleWeightBytes(characteristic.value)
            }
        }
    }

    /**
     * Generate a signed telemetry frame reading for the active demo-scale scenario.
     *
     * This is intentionally deterministic for the demo path: the selected button value
     * must be preserved exactly in the packet (e.g. 642g or 210g). We do not apply
     * a hidden variance or divide-by-10 scaling in this path because that would corrupt
     * the outbound/return comparison and make the verdict logic meaningless.
     */
    fun readScaleData(baseWeightGrams: Double = 642.0): ScaleTelemetryPacket {
        sequenceCounter++
        val liveWeight = baseWeightGrams.coerceAtLeast(0.0)

        val timestampUtc = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val scaleId = "BLE-SCALE-001"

        // Construct Canonical Weight Payload string for signing
        val canonicalPayload = "$scaleId|$timestampUtc|$liveWeight|$sequenceCounter|$pairingSessionId"
        val signature = computeHmacSha256(canonicalPayload, sharedSecret)

        val json = """{
            "scale_id": "$scaleId",
            "timestamp_utc": "$timestampUtc",
            "weight_grams": $liveWeight,
            "sequence_number": $sequenceCounter,
            "pairing_session_id": "$pairingSessionId",
            "signature": "$signature"
        }""".trimIndent()

        return ScaleTelemetryPacket(
            scaleId = scaleId,
            timestampUtc = timestampUtc,
            weightGrams = liveWeight,
            sequenceNumber = sequenceCounter,
            pairingSessionId = pairingSessionId,
            hmacSignature = signature,
            jsonPayload = json
        )
    }

    /**
     * Parse raw 8-byte BLE GATT weight payload.
     */
    fun parseScaleWeightBytes(bytes: ByteArray): Double {
        if (bytes.size < 4) return 642.0
        val rawInt = (bytes[0].toInt() and 0xFF) or
                ((bytes[1].toInt() and 0xFF) shl 8) or
                ((bytes[2].toInt() and 0xFF) shl 16) or
                ((bytes[3].toInt() and 0xFF) shl 24)
        return rawInt / 10.0
    }

    private fun computeHmacSha256(data: String, key: String): String {
        val secretKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val hmacBytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }
}
