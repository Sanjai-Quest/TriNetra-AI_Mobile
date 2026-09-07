package com.trinetra.ai.ble

/** Common telemetry contract shared by demo and future physical GATT sources. */
interface ScaleTelemetrySource {
    fun readScaleData(baseWeightGrams: Double): ScaleTelemetryPacket
}

/** Hackathon source: deterministic application-level demo input, clearly not hardware. */
class DemoBleScaleTelemetrySource(
    private val packetManager: BluetoothScaleManager = BluetoothScaleManager()
) : ScaleTelemetrySource {
    override fun readScaleData(baseWeightGrams: Double): ScaleTelemetryPacket =
        packetManager.readScaleData(baseWeightGrams)
}

/** Real GATT-ready source. Physical discovery/pairing remains unavailable in this build. */
class RealBleScaleTelemetrySource(
    private val packetManager: BluetoothScaleManager = BluetoothScaleManager()
) : ScaleTelemetrySource {
    override fun readScaleData(baseWeightGrams: Double): ScaleTelemetryPacket =
        packetManager.readScaleData(baseWeightGrams)
}
