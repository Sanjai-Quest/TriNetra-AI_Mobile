package com.trinetra.ai

import com.trinetra.ai.cv.OpenCVProcessor
import com.trinetra.ai.nlp.VoiceClassifier
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Performance Validation Suite for TriNetra Mobile Client.
 * Verifies strict SLA budgets:
 * - TF-IDF NLP Voice Classifier latency < 20ms
 * - OpenCV Laplacian & Canny processing latency < 1000ms per frame
 */
class PerformanceBenchmarkTest {

    @Test
    fun benchmarkVoiceClassifierSla() {
        val classifier = VoiceClassifier()
        val testInputs = listOf(
            "Box phata hai, weight light, seal intact",
            "Package damaged torn open weight empty",
            "Counterfeit suspected, duplicate tape, rattle inside",
            "Good condition, intact sealed sahihai"
        )

        for (input in testInputs) {
            classifier.classify(input)
            val startTime = System.nanoTime()
            val result = classifier.classify(input)
            val elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0

            println("Voice NLP Parse Latency for '$input': ${String.format("%.3f", elapsedMs)} ms")
            assertTrue("TF-IDF parse exceeded 20ms SLA budget", elapsedMs < 20.0)
            assertTrue("Confidence score should be valid", result.confidenceScore > 0.0)
        }
    }

    @Test
    fun benchmarkOpenCVProcessingSla() {
        val processor = OpenCVProcessor()
        val frame640x480 = ByteArray(640 * 480) { (Math.random() * 255).toInt().toByte() }

        val startTime = System.nanoTime()
        val result = processor.analyzeFrame(frame640x480, 640, 480)
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0

        println("OpenCV Frame Processing Latency (640x480): ${String.format("%.3f", elapsedMs)} ms")
        println("Calculated Laplacian Variance: ${result.laplacianVariance}")
        println("Calculated Canny Edge Density: ${result.cannyEdgeDensity}")

        assertTrue("OpenCV processing exceeded 1000ms SLA budget", elapsedMs < 1000.0)
        assertTrue("Laplacian variance out of bounds", result.laplacianVariance in 0.0..1.0)
        assertTrue("Canny edge density out of bounds", result.cannyEdgeDensity in 0.0..1.0)
    }
}
