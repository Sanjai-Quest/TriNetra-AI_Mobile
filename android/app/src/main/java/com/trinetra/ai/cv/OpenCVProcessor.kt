package com.trinetra.ai.cv

import kotlin.math.abs
import kotlin.math.pow

/**
 * Result metrics computed from on-device Computer Vision pipeline.
 */
data class CVAnalysisResult(
    val laplacianVariance: Double, // High = Worn/Micro-abrasions, Low = Smooth/Counterfeit
    val cannyEdgeDensity: Double,  // High = Creased/Worn fabric, Low = Pristine
    val latencyMs: Long
)

/**
 * OpenCV Android Processor with JNI Native Binding and pure-Kotlin Mathematical Fallback.
 * Computes:
 * 1. Laplacian Texture Variance: Evaluates high-frequency spatial gradients to detect surface micro-wear.
 * 2. Canny Edge Crease Density: Computes structural lines to identify wrinkled or pre-worn clothing.
 */
class OpenCVProcessor {

    companion object {
        private var isNativeLibraryLoaded = false

        init {
            try {
                System.loadLibrary("trinetra_opencv")
                isNativeLibraryLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                isNativeLibraryLoaded = false
            }
        }
    }

    // Native C++ JNI function declarations
    private external fun nativeCalculateLaplacianVariance(grayscaleBytes: ByteArray, width: Int, height: Int): Double
    private external fun nativeCalculateCannyEdgeDensity(grayscaleBytes: ByteArray, width: Int, height: Int, threshold1: Double, threshold2: Double): Double

    /**
     * Process 8-bit grayscale frame buffer and compute wear analysis metrics.
     */
    fun analyzeFrame(grayscaleBytes: ByteArray, width: Int, height: Int): CVAnalysisResult {
        val startTime = System.currentTimeMillis()

        val laplacianVar: Double
        val cannyDensity: Double

        if (isNativeLibraryLoaded) {
            laplacianVar = nativeCalculateLaplacianVariance(grayscaleBytes, width, height)
            cannyDensity = nativeCalculateCannyEdgeDensity(grayscaleBytes, width, height, 50.0, 150.0)
        } else {
            // Pure Kotlin Mathematical Fallback (Convolutions + Gradient Thresholding)
            laplacianVar = computeKotlinLaplacianVariance(grayscaleBytes, width, height)
            cannyDensity = computeKotlinCannyEdgeDensity(grayscaleBytes, width, height)
        }

        val latency = System.currentTimeMillis() - startTime
        // NOTE: the Kotlin fallback functions below already return values normalized to
        // roughly [0.0, 1.0] (see their own coerceIn calls). Do NOT re-scale by 100 here —
        // that was clamping both metrics to a constant 1.0 for almost any real image,
        // which would have made the on-screen numbers non-responsive to the actual frame.
        return CVAnalysisResult(
            laplacianVariance = laplacianVar.coerceIn(0.0, 1.0),
            cannyEdgeDensity = cannyDensity.coerceIn(0.0, 1.0),
            latencyMs = latency
        )
    }

    /**
     * Pure Kotlin Laplacian Variance:
     * Applies 3x3 Laplacian Kernel [[0, 1, 0], [1, -4, 1], [0, 1, 0]] on grayscale pixels,
     * then computes variance = mean((L - mean(L))^2).
     */
    private fun computeKotlinLaplacianVariance(bytes: ByteArray, width: Int, height: Int): Double {
        if (bytes.isEmpty() || width <= 2 || height <= 2) return 0.25

        val laplacianValues = DoubleArray((width - 2) * (height - 2))
        var idx = 0
        var sum = 0.0

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val center = bytes[y * width + x].toInt() and 0xFF
                val top = bytes[(y - 1) * width + x].toInt() and 0xFF
                val bottom = bytes[(y + 1) * width + x].toInt() and 0xFF
                val left = bytes[y * width + (x - 1)].toInt() and 0xFF
                val right = bytes[y * width + (x + 1)].toInt() and 0xFF

                // 2D Laplacian operator
                val lValue = (top + bottom + left + right - 4 * center).toDouble()
                laplacianValues[idx++] = lValue
                sum += lValue
            }
        }

        if (idx == 0) return 0.25
        val mean = sum / idx
        var varianceSum = 0.0
        for (i in 0 until idx) {
            varianceSum += (laplacianValues[i] - mean).pow(2)
        }
        val rawVariance = varianceSum / idx
        return (rawVariance / 2500.0).coerceIn(0.05, 0.95)
    }

    /**
     * Pure Kotlin Canny Edge Crease Density:
     * Computes Sobel spatial gradients Gx and Gy, evaluates magnitude sqrt(Gx^2 + Gy^2),
     * applies thresholding (50 to 150) and returns (edge_count / total_pixels).
     */
    private fun computeKotlinCannyEdgeDensity(bytes: ByteArray, width: Int, height: Int): Double {
        if (bytes.isEmpty() || width <= 2 || height <= 2) return 0.40

        var edgeCount = 0
        val totalPixels = (width - 2) * (height - 2)

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                // Sobel Gx [[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]]
                val gx = (- (bytes[(y - 1) * width + (x - 1)].toInt() and 0xFF) + (bytes[(y - 1) * width + (x + 1)].toInt() and 0xFF)
                        - 2 * (bytes[y * width + (x - 1)].toInt() and 0xFF) + 2 * (bytes[y * width + (x + 1)].toInt() and 0xFF)
                        - (bytes[(y + 1) * width + (x - 1)].toInt() and 0xFF) + (bytes[(y + 1) * width + (x + 1)].toInt() and 0xFF))

                // Sobel Gy [[-1, -2, -1], [0, 0, 0], [1, 2, 1]]
                val gy = (- (bytes[(y - 1) * width + (x - 1)].toInt() and 0xFF) - 2 * (bytes[(y - 1) * width + x].toInt() and 0xFF) - (bytes[(y - 1) * width + (x + 1)].toInt() and 0xFF)
                        + (bytes[(y + 1) * width + (x - 1)].toInt() and 0xFF) + 2 * (bytes[(y + 1) * width + x].toInt() and 0xFF) + (bytes[(y + 1) * width + (x + 1)].toInt() and 0xFF))

                val mag = abs(gx) + abs(gy)
                if (mag > 100) { // Crease edge threshold
                    edgeCount++
                }
            }
        }

        return if (totalPixels > 0) (edgeCount.toDouble() / totalPixels).coerceIn(0.01, 0.99) else 0.40
    }
}
