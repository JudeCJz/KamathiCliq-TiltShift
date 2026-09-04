package com.example.simplecamera.data

import com.google.mlkit.vision.face.Face
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

data class PeakPlusState(
    val faceDetected: Boolean = false,
    val estimatedDistanceCm: Int = 0,
    val targetDistanceCm: Int = 70,
    val isDistanceMatched: Boolean = false,   // True when distance matches target within tolerance (+-10cm)
    val isDistanceClose: Boolean = false,     // True when within +-22cm
    val areEyesClosed: Boolean = false,       // True when both eyes are closed
    val isReadyToShoot: Boolean = false,      // faceDetected && isDistanceMatched && areEyesClosed
    val distanceProgress: Float = 0f,         // 0f to 1f relative progress
    val eyeClosedScore: Float = 0f,           // 0f to 1f
    val guidanceMessage: String = "No face in frame. Point camera at subject."
)

object PeakPlusEvaluator {
    const val DISTANCE_TOLERANCE_CM = 10
    const val DISTANCE_CLOSE_TOLERANCE_CM = 22

    fun randomTargetDistance(isFrontCamera: Boolean): Int {
        return if (isFrontCamera) {
            // Front camera (selfie): 30 to 80 cm
            (Random.nextInt(6, 17) * 5)
        } else {
            // Back camera: 40 to 150 cm
            (Random.nextInt(8, 31) * 5)
        }
    }

    fun evaluate(
        face: Face?,
        frameWidth: Int,
        frameHeight: Int,
        isFrontCamera: Boolean,
        targetDistanceCm: Int
    ): PeakPlusState {
        if (face == null || frameWidth <= 0 || frameHeight <= 0) {
            return PeakPlusState(
                faceDetected = false,
                estimatedDistanceCm = 0,
                targetDistanceCm = targetDistanceCm,
                isDistanceMatched = false,
                isDistanceClose = false,
                areEyesClosed = false,
                isReadyToShoot = false,
                distanceProgress = 0f,
                eyeClosedScore = 0f,
                guidanceMessage = "No face in frame. Point camera at face."
            )
        }

        // Calculate face dimension relative to frame
        val faceBox = face.boundingBox
        val faceW = faceBox.width().toFloat()
        val faceH = faceBox.height().toFloat()
        val relW = (faceW / frameWidth.toFloat()).coerceIn(0.04f, 0.95f)
        val relH = (faceH / frameHeight.toFloat()).coerceIn(0.04f, 0.95f)
        val faceRatio = maxOf(relW, relH / 1.3f)

        // Distance estimation in cm based on focal length FOV calibration
        val kFactor = if (isFrontCamera) 9.8f else 11.2f
        val rawDistance = (kFactor / faceRatio) * 10f
        val distanceCm = rawDistance.roundToInt().coerceIn(15, 200)

        // Match evaluation
        val distDiff = abs(distanceCm - targetDistanceCm)
        val isDistanceMatched = distDiff <= DISTANCE_TOLERANCE_CM
        val isDistanceClose = distDiff <= DISTANCE_CLOSE_TOLERANCE_CM

        // Normalized progress towards target
        val distanceProgress = (1f - (distDiff / 50f)).coerceIn(0f, 1f)

        // Eye state evaluation
        val leftEyeProb = face.leftEyeOpenProbability ?: -1f
        val rightEyeProb = face.rightEyeOpenProbability ?: -1f

        val (areEyesClosed, eyeScore) = if (leftEyeProb < 0f && rightEyeProb < 0f) {
            Pair(false, 0f)
        } else {
            val validLeft = if (leftEyeProb >= 0f) leftEyeProb else rightEyeProb
            val validRight = if (rightEyeProb >= 0f) rightEyeProb else leftEyeProb
            val avgOpen = (validLeft + validRight) / 2f
            val closed = (validLeft <= 0.30f && validRight <= 0.30f) || avgOpen <= 0.25f
            val score = (1f - avgOpen).coerceIn(0f, 1f)
            Pair(closed, score)
        }

        val isReady = isDistanceMatched && areEyesClosed

        val guidance = when {
            !isDistanceMatched && !areEyesClosed -> {
                if (distanceCm < targetDistanceCm) "Move camera back to ${targetDistanceCm}cm (current: ${distanceCm}cm) and close eyes."
                else "Move camera closer to ${targetDistanceCm}cm (current: ${distanceCm}cm) and close eyes."
            }
            !isDistanceMatched -> {
                if (distanceCm < targetDistanceCm) "Move back: off by ${distDiff}cm (target: ${targetDistanceCm}cm, current: ${distanceCm}cm)."
                else "Move closer: off by ${distDiff}cm (target: ${targetDistanceCm}cm, current: ${distanceCm}cm)."
            }
            !areEyesClosed -> "Distance locked (${distanceCm}cm). Close both eyes now."
            else -> "Distance and eyes locked. Capture now."
        }

        return PeakPlusState(
            faceDetected = true,
            estimatedDistanceCm = distanceCm,
            targetDistanceCm = targetDistanceCm,
            isDistanceMatched = isDistanceMatched,
            isDistanceClose = isDistanceClose,
            areEyesClosed = areEyesClosed,
            isReadyToShoot = isReady,
            distanceProgress = distanceProgress,
            eyeClosedScore = eyeScore,
            guidanceMessage = guidance
        )
    }
}
