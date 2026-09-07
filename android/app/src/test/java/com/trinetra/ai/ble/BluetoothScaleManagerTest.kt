package com.trinetra.ai.ble

import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothScaleManagerTest {

    @Test
    fun `demo scale returns exact configured weight for consistent and swap scenarios`() {
        val manager = BluetoothScaleManager()

        val consistent = manager.readScaleData(642.0)
        assertEquals(642.0, consistent.weightGrams, 0.0)

        val swap = manager.readScaleData(210.0)
        assertEquals(210.0, swap.weightGrams, 0.0)
    }
}
