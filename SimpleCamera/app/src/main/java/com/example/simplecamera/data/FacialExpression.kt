package com.example.simplecamera.data

import com.google.mlkit.vision.face.Face
import kotlin.math.abs

enum class FacialExpression(
    val id: String,
    val title: String,
    val prompt: String,
    val hint: String
) {
    BIG_SMILE(
        id = "smile",
        title = "Smile",
        prompt = "CHEESE! Big Smile!",
        hint = "Show those teeth! Smile wider!"
    ),
    DEADPAN(
        id = "deadpan",
        title = "Poker Face",
        prompt = "Deadpan! Serious Poker Face!",
        hint = "Stop laughing! Straight face, eyes open."
    ),
    WINK_LEFT(
        id = "wink_left",
        title = "Left Wink",
        prompt = "Wink your LEFT eye!",
        hint = "Shut left eye, keep right eye wide open!"
    ),
    WINK_RIGHT(
        id = "wink_right",
        title = "Right Wink",
        prompt = "Wink your RIGHT eye!",
        hint = "Shut right eye, keep left eye wide open!"
    ),
    EYES_SHUT(
        id = "eyes_shut",
        title = "Zen Eyes Shut",
        prompt = "Close both eyes completely!",
        hint = "Peaceful sleep... Close both eyes!"
    ),
    TILT_HEAD(
        id = "tilt_head",
        title = "Head Tilt",
        prompt = "Curious tilt! Lean head sideways!",
        hint = "Tilt your head sideways like a confused dog!"
    );

    fun evaluate(face: Face): ExpressionMatchResult {
        val smileProb = face.smilingProbability ?: -1f
        val leftEye = face.leftEyeOpenProbability ?: -1f
        val rightEye = face.rightEyeOpenProbability ?: -1f
        val eulerZ = face.headEulerAngleZ // tilt sideways (-deg is right, +deg is left)

        return when (this) {
            BIG_SMILE -> {
                if (smileProb < 0f) {
                    ExpressionMatchResult(0.2f, false, "Looking for face smile...")
                } else {
                    val score = (smileProb / 0.70f).coerceIn(0f, 1f)
                    val matched = smileProb >= 0.70f
                    val msg = if (matched) "PERFECT SMILE!" else "Smile wider! (${(smileProb * 100).toInt()}%)"
                    ExpressionMatchResult(score, matched, msg)
                }
            }
            DEADPAN -> {
                if (smileProb < 0f) {
                    ExpressionMatchResult(0.2f, false, "Looking for straight face...")
                } else {
                    val isNeutral = smileProb <= 0.18f
                    val eyesOpen = (leftEye > 0.5f || leftEye < 0f) && (rightEye > 0.5f || rightEye < 0f)
                    val score = if (isNeutral && eyesOpen) 1.0f else (1f - smileProb).coerceIn(0f, 0.8f)
                    val matched = isNeutral && eyesOpen
                    val msg = if (matched) "COLD AS ICE!" else "No smiling! Be serious!"
                    ExpressionMatchResult(score, matched, msg)
                }
            }
            WINK_LEFT -> {
                // In mirrored selfie vs normal camera, user winks left eye
                if (leftEye < 0f || rightEye < 0f) {
                    ExpressionMatchResult(0.2f, false, "Checking eyes...")
                } else {
                    val isLeftShut = leftEye < 0.35f
                    val isRightOpen = rightEye > 0.60f
                    val score = if (isLeftShut && isRightOpen) 1.0f else if (isLeftShut) 0.6f else 0.1f
                    val matched = isLeftShut && isRightOpen
                    val msg = if (matched) "KILLER WINK!" else if (!isLeftShut) "Close your left eye!" else "Open your right eye!"
                    ExpressionMatchResult(score, matched, msg)
                }
            }
            WINK_RIGHT -> {
                if (leftEye < 0f || rightEye < 0f) {
                    ExpressionMatchResult(0.2f, false, "Checking eyes...")
                } else {
                    val isRightShut = rightEye < 0.35f
                    val isLeftOpen = leftEye > 0.60f
                    val score = if (isRightShut && isLeftOpen) 1.0f else if (isRightShut) 0.6f else 0.1f
                    val matched = isRightShut && isLeftOpen
                    val msg = if (matched) "CHEEK WINK!" else if (!isRightShut) "Close your right eye!" else "Open your left eye!"
                    ExpressionMatchResult(score, matched, msg)
                }
            }
            EYES_SHUT -> {
                if (leftEye < 0f || rightEye < 0f) {
                    ExpressionMatchResult(0.2f, false, "Checking eyes...")
                } else {
                    val bothShut = leftEye < 0.25f && rightEye < 0.25f
                    val avgShut = 1f - ((leftEye + rightEye) / 2f)
                    val score = avgShut.coerceIn(0f, 1f)
                    val matched = bothShut
                    val msg = if (matched) "ZEN MASTER!" else "Shut both eyes tight!"
                    ExpressionMatchResult(score, matched, msg)
                }
            }
            TILT_HEAD -> {
                val absTilt = abs(eulerZ)
                val score = (absTilt / 22f).coerceIn(0f, 1f)
                val matched = absTilt >= 20f
                val msg = if (matched) "NICE ANGLE!" else "Tilt your head more! (${absTilt.toInt()}°/20°)"
                ExpressionMatchResult(score, matched, msg)
            }
        }
    }

    companion object {
        fun random(): FacialExpression {
            return entries.random()
        }
    }
}

data class ExpressionMatchResult(
    val score: Float,           // 0.0 to 1.0
    val isMatched: Boolean,     // True if criteria satisfied
    val feedback: String        // Short advice for the user
)
