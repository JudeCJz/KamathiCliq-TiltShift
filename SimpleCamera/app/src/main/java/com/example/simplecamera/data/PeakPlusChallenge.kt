package com.example.simplecamera.data

import com.google.mlkit.vision.face.Face
import kotlin.math.roundToInt

data class PeakPlusState(
    val faceDetected: Boolean = false,
    val estimatedDistanceCm: Int = 0,
    val isArmStretched: Boolean = false,     // True when distance is >= 60 cm (target ~70 cm)
    val areEyesClosed: Boolean = false,       // True when both eyes are closed
    val isReadyToShoot: Boolean = false,      // faceDetected && isArmStretched && areEyesClosed
    val distanceProgress: Float = 0f,         // 0f to 1f towards 70cm
    val eyeClosedScore: Float = 0f,           // 0f to 1f
    val guidanceMessage: String = "No face in frame! Point camera at subject."
)

object PeakPlusEvaluator {
    const val TARGET_DISTANCE_CM = 70
    const val MIN_STRETCH_DISTANCE_CM = 60 // Allow >= 60cm as stretched arm

    fun evaluate(face: Face?, frameWidth: Int, frameHeight: Int, isFrontCamera: Boolean): PeakPlusState {
        if (face == null || frameWidth <= 0 || frameHeight <= 0) {
            return PeakPlusState(
                faceDetected = false,
                estimatedDistanceCm = 0,
                isArmStretched = false,
                areEyesClosed = false,
                isReadyToShoot = false,
                distanceProgress = 0f,
                eyeClosedScore = 0f,
                guidanceMessage = "No face in frame! Point camera at face!"
            )
        }

        // Calculate face dimension relative to frame
        val faceBox = face.boundingBox
        val faceW = faceBox.width().toFloat()
        val faceH = faceBox.height().toFloat()
        val relW = (faceW / frameWidth.toFloat()).coerceIn(0.05f, 0.95f)
        val relH = (faceH / frameHeight.toFloat()).coerceIn(0.05f, 0.95f)
        // Combine width and normalized height (face height is ~1.3x face width)
        val faceRatio = maxOf(relW, relH / 1.3f)

        // Distance estimation in cm based on focal length FOV calibration
        // At 35cm (normal selfie): faceRatio ~ 0.28 (front) or 0.32 (back)
        // At 70cm (full arm stretch): faceRatio ~ 0.14 (front) or 0.16 (back)
        val kFactor = if (isFrontCamera) 9.8f else 11.2f
        val rawDistance = (kFactor / faceRatio) * 10f
        val distanceCm = rawDistance.roundToInt().coerceIn(15, 120)

        // Progress towards 70cm (stretch starts from 25cm to 70cm)
        val distanceProgress = ((distanceCm - 25f) / (TARGET_DISTANCE_CM - 25f)).coerceIn(0f, 1f)
        val isArmStretched = distanceCm >= MIN_STRETCH_DISTANCE_CM

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

        val isReady = isArmStretched && areEyesClosed

        val guidance = when {
            !isArmStretched && !areEyesClosed -> "Stretch arm to ~70cm ($distanceCm cm) & close eyes!"
            !isArmStretched -> "Too close ($distanceCm cm)! Stretch your arm to ~70cm! 📏"
            !areEyesClosed -> "Arm stretched ($distanceCm cm) ✅! Now CLOSE YOUR EYES! 😴"
            else -> "ARM STRETCHED & EYES CLOSED! 📸 CLICK TO SHOOT!"
        }

        return PeakPlusState(
            faceDetected = true,
            estimatedDistanceCm = distanceCm,
            isArmStretched = isArmStretched,
            areEyesClosed = areEyesClosed,
            isReadyToShoot = isReady,
            distanceProgress = distanceProgress,
            eyeClosedScore = eyeScore,
            guidanceMessage = guidance
        )
    }
}
