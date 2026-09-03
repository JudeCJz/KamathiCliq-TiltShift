package com.example.simplecamera.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.simplecamera.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

// Only 2 difficulties: Baby Mode (level guide on) and Chad Mode (hardest, blind mode)
enum class Difficulty(val label: String, val showLevelHelper: Boolean, val badgeColor: Color) {
    BABY("Baby Mode", true, Color(0xFF00E676)),
    CHAD("Chad Mode", false, Color(0xFFFF1744))
}

enum class CameraMode(val displayName: String, val accentColor: Color) {
    NORMAL("Normal", Color(0xFF00E676)), // Emerald Green - Zoom requirement only
    PRO("Pro", Color(0xFF00E5FF)),       // Cyber Cyan - Level + Zoom requirements
    PEAK("Peak", Color(0xFFFFD54F))      // Golden Amber - All 3 requirements: Level + Compass + Zoom
}

data class ModeTarget(
    val targetPitch: Float,      // Target tilt angle in degrees (-90 to +90)
    val targetCompass: Float,    // Target heading in degrees (0 to 359)
    val targetZoom: Float        // Target zoom ratio (e.g. 1.5x to 3.5x)
)

data class ShotResult(
    val photoUri: Uri,
    val certUri: Uri,
    val photoBitmap: Bitmap,
    val certBitmap: Bitmap,
    val accuracy: Float,
    val grade: String,
    val roast: String,
    val mode: CameraMode,
    val target: ModeTarget,
    val actualPitch: Float,
    val actualCompass: Float,
    val actualZoom: Float
)

fun generateRandomTarget(): ModeTarget {
    val samplePitches = listOf(15f, 25f, 35f, 45f, 60f)
    val pitch = samplePitches.random()

    val compass = (Random.nextInt(0, 8) * 45).toFloat()

    val sampleZooms = listOf(1.5f, 2.0f, 2.5f, 3.0f, 3.5f)
    val zoom = sampleZooms.random()

    return ModeTarget(targetPitch = pitch, targetCompass = compass, targetZoom = zoom)
}

fun getSavageRoast(mode: CameraMode, pitchErr: Float, compassErr: Float, zoomErr: Float): String {
    return when {
        mode != CameraMode.NORMAL && pitchErr > 0.5f -> {
            when {
                pitchErr > 15f -> "Off by ${pitchErr.roundToInt()}°! Are you trying to photograph the ceiling?!"
                pitchErr > 10f -> "Are you holding a phone or steering a pirate ship? Off by ${pitchErr.roundToInt()}°!"
                pitchErr > 6f -> "Off by ${pitchErr.roundToInt()}°! Hands trembling like an earthquake."
                pitchErr > 3f -> "Missed by ${String.format(Locale.US, "%.1f", pitchErr)}°. My grandma tilts a phone straighter than this."
                else -> "Crooked by ${String.format(Locale.US, "%.1f", pitchErr)}°. Steady those shaky fingers!"
            }
        }
        zoomErr > 0.05f -> {
            "Can't dial a zoom slider? Off by ${String.format(Locale.US, "%.2f", zoomErr)}x. It's not rocket science."
        }
        mode == CameraMode.PEAK && compassErr > 1.5f -> {
            "Wrong direction by ${compassErr.roundToInt()}°! Do you even know which way North is?"
        }
        else -> "Surprisingly, you didn't butcher this shot. Pure beginners luck."
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required to use TiltShift*", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraView()
    } else {
        PermissionScreen(onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        })
    }
}

@Composable
fun CameraView() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // ALWAYS DEFAULT TO HARDEST DIFFICULTY (CHAD MODE)
    var currentDifficulty by remember { mutableStateOf(Difficulty.CHAD) }
    var showDifficultyMenu by remember { mutableStateOf(false) }

    // Launch Loading Screen with Tips
    var isLaunchLoading by remember { mutableStateOf(true) }

    var currentMode by remember { mutableStateOf(CameraMode.PRO) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flipRotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedFlipRotation by animateFloatAsState(
        targetValue = flipRotationAngle,
        animationSpec = tween(durationMillis = 350),
        label = "flipRotation"
    )

    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var isCameraReady by remember { mutableStateOf(false) }
    var evIndex by remember { mutableIntStateOf(0) }
    var currentZoomRatio by remember { mutableFloatStateOf(1.0f) }
    var maxZoomRatio by remember { mutableFloatStateOf(5.0f) }

    // Targets for puzzle
    var currentTarget by remember { mutableStateOf(generateRandomTarget()) }

    // Live Sensors: Orientation pitch and compass azimuth
    var currentPitch by remember { mutableFloatStateOf(0f) }
    var currentCompass by remember { mutableFloatStateOf(0f) }

    // Completed Shot Dialog State
    var lastShotResult by remember { mutableStateOf<ShotResult?>(null) }

    // Register Sensors (Rotation Vector / Orientation)
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        val sensorListener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientation = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthDeg = ((Math.toDegrees(orientation[0].toDouble()) + 360) % 360).toFloat()
                    val pitchDeg = abs(Math.toDegrees(orientation[1].toDouble())).toFloat()

                    currentCompass = azimuthDeg
                    currentPitch = pitchDeg
                } else if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    currentCompass = (event.values[0] + 360) % 360
                    currentPitch = abs(event.values[1])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(sensorListener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // Increased threshold (+3 deg -> 5.0 deg tolerance)
    val angleTolerance = 5.0f
    val compassTolerance = 5.5f
    val zoomTolerance = 0.15f

    val isAngleLocked = abs(currentPitch - currentTarget.targetPitch) <= angleTolerance
    val compassDiff = abs((currentCompass - currentTarget.targetCompass + 540) % 360 - 180)
    val isCompassLocked = compassDiff <= compassTolerance
    val isZoomLocked = abs(currentZoomRatio - currentTarget.targetZoom) <= zoomTolerance

    val isShutterUnlocked = when (currentMode) {
        CameraMode.NORMAL -> isZoomLocked
        CameraMode.PRO -> isAngleLocked && isZoomLocked
        CameraMode.PEAK -> isAngleLocked && isCompassLocked && isZoomLocked
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    var isCapturing by remember { mutableStateOf(false) }
    var showFlashFeedback by remember { mutableStateOf(false) }
    var tapFocusCoordinates by remember { mutableStateOf<Offset?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // Bind Camera lifecycle
    LaunchedEffect(lensFacing) {
        isCameraReady = false
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraInstance = camera

                camera.cameraInfo.zoomState.observe(lifecycleOwner) { zoomState ->
                    currentZoomRatio = zoomState.zoomRatio
                    maxZoomRatio = zoomState.maxZoomRatio.coerceAtMost(8.0f)
                }
                isCameraReady = true
            } catch (exc: Exception) {
                Toast.makeText(context, "Failed to start camera: ${exc.localizedMessage}", Toast.LENGTH_SHORT).show()
                isCameraReady = true
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // EV exposure compensation
    LaunchedEffect(evIndex, cameraInstance) {
        try {
            cameraInstance?.cameraControl?.setExposureCompensationIndex(evIndex)
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoomChange, _ ->
                    cameraInstance?.let { cam ->
                        val newZoom = (currentZoomRatio * zoomChange).coerceIn(1.0f, maxZoomRatio)
                        cam.cameraControl.setZoomRatio(newZoom)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    tapFocusCoordinates = offset
                    cameraInstance?.let { cam ->
                        val factory = SurfaceOrientedMeteringPointFactory(
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                        val point = factory.createPoint(offset.x, offset.y)
                        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build()
                        cam.cameraControl.startFocusAndMetering(action)
                    }
                }
            }
    ) {
        // Camera Preview Layer
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Live Leveling Overlay: Under the HUD (Enabled ONLY in Baby Mode)
        if (isCameraReady && currentDifficulty.showLevelHelper && (currentMode == CameraMode.PRO || currentMode == CameraMode.PEAK)) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-20).dp)
            ) {
                LiveSpiritLevel(
                    currentPitch = currentPitch,
                    targetPitch = currentTarget.targetPitch,
                    isLocked = isAngleLocked,
                    accentColor = currentMode.accentColor
                )
            }
        }

        // Tap-to-Focus Reticle
        tapFocusCoordinates?.let { coords ->
            FocusIndicator(
                offset = coords,
                color = currentMode.accentColor,
                onDismiss = { tapFocusCoordinates = null }
            )
        }

        // Capture Shutter White Flash Feedback
        AnimatedVisibility(
            visible = showFlashFeedback,
            enter = fadeIn(animationSpec = tween(40)),
            exit = fadeOut(animationSpec = tween(90)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }

        // Top Controls Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty Dropdown (Top Left) - Only Baby Mode and Chad Mode
                Box {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Black.copy(alpha = 0.70f),
                        border = BorderStroke(1.5.dp, currentDifficulty.badgeColor.copy(alpha = 0.8f)),
                        modifier = Modifier.clickable { showDifficultyMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${currentDifficulty.label} ▾",
                                color = currentDifficulty.badgeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showDifficultyMenu,
                        onDismissRequest = { showDifficultyMenu = false },
                        modifier = Modifier.background(Color(0xFF1B1C24))
                    ) {
                        Difficulty.values().forEach { diff ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = diff.label,
                                            color = if (diff == currentDifficulty) diff.badgeColor else Color.White,
                                            fontWeight = if (diff == currentDifficulty) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = if (diff.showLevelHelper) "Visual Spirit Level ON" else "Hardest (Blind Tilting, No Guide)",
                                            color = Color.White.copy(alpha = 0.45f),
                                            fontSize = 10.sp
                                        )
                                    }
                                },
                                onClick = {
                                    currentDifficulty = diff
                                    showDifficultyMenu = false
                                }
                            )
                        }
                    }
                }

                // Brand & Mode Badge (Center)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Black.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, currentMode.accentColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "TiltShift*",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(currentMode.accentColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentMode.displayName.uppercase(),
                            color = currentMode.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Camera Switch Button (Top Right)
                IconButton(
                    onClick = {
                        flipRotationAngle += 180f
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.50f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cameraswitch,
                        contentDescription = "Switch camera",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(animatedFlipRotation)
                    )
                }
            }
        }

        // Top Status HUD Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 66.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            PuzzleLockStatusHUD(
                mode = currentMode,
                target = currentTarget,
                currentPitch = currentPitch,
                currentCompass = currentCompass,
                currentZoom = currentZoomRatio,
                isAngleLocked = isAngleLocked,
                isCompassLocked = isCompassLocked,
                isZoomLocked = isZoomLocked,
                isAllUnlocked = isShutterUnlocked,
                onRerollTarget = { currentTarget = generateRandomTarget() }
            )
        }

        // Bottom Controls Container (Houses the BOLD READABLE ROAST BANNER + Zoom Slider + Shutter)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.90f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HIGH-CONTRAST BOLD & CLEAR LIVE ROAST BANNER
            if (currentMode != CameraMode.NORMAL) {
                val pErr = abs(currentPitch - currentTarget.targetPitch)
                val isAngleGood = isAngleLocked

                val roastCategory = if (isAngleGood) "✅ ANGLE LOCKED (±5°)" else "🔥 LIVE ROAST"
                val roastText = when {
                    isAngleGood -> "Holding steady! Don't breathe, hit the shutter!"
                    pErr > 18f -> "Off by ${pErr.roundToInt()}°! Are you trying to photograph the ceiling or floor?!"
                    pErr > 10f -> "Off by ${pErr.roundToInt()}°! Are you steering a pirate ship with that phone?"
                    pErr > 6f -> "Off by ${pErr.roundToInt()}°! Hands trembling like a 7.0 earthquake."
                    pErr > 3f -> "Crooked by ${String.format(Locale.US, "%.1f", pErr)}°! My grandma tilts straighter than this."
                    else -> "Off by ${String.format(Locale.US, "%.1f", pErr)}°! Almost there, hold your hands steady!"
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF14151E).copy(alpha = 0.96f),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (isAngleGood) Color(0xFF00E676) else Color(0xFFFF5252)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isAngleGood) Color(0xFF00E676).copy(alpha = 0.25f) else Color(0xFFFF5252).copy(alpha = 0.25f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isAngleGood) "🎯" else "💀",
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = roastCategory,
                                    color = if (isAngleGood) Color(0xFF00E676) else Color(0xFFFF8A80),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isAngleGood) "±${String.format(Locale.US, "%.1f", pErr)}° [LOCKED]" else "Off by ${String.format(Locale.US, "%.1f", pErr)}°",
                                    color = if (isAngleGood) Color(0xFF00E676) else Color(0xFFFF5252),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = roastText,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // Interactive Glassmorphic Zoom Bar matching user design
            val isZoomClose = !isZoomLocked && abs(currentZoomRatio - currentTarget.targetZoom) <= 0.40f
            GlassmorphicZoomBar(
                currentZoom = currentZoomRatio,
                targetZoom = currentTarget.targetZoom,
                minZoom = 1.0f,
                maxZoom = maxZoomRatio.coerceAtLeast(8.0f),
                isZoomLocked = isZoomLocked,
                isZoomClose = isZoomClose,
                onZoomChange = { newZoom ->
                    cameraInstance?.cameraControl?.setZoomRatio(newZoom)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            )

            // Shutter Row + Bottom Right Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bottom Left: Open Gallery Button
                IconButton(
                    onClick = { openPhotosGallery(context) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = "View Photos",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Center: Tactile Shutter Button with Lock / Unlock state
                TactilePuzzleShutterButton(
                    accentColor = currentMode.accentColor,
                    isUnlocked = isShutterUnlocked,
                    isCapturing = isCapturing,
                    onClick = {
                        if (!isCapturing && isShutterUnlocked) {
                            isCapturing = true

                            coroutineScope.launch {
                                showFlashFeedback = true
                                delay(80)
                                showFlashFeedback = false
                            }

                            takePhotoWithCertification(
                                context = context,
                                mode = currentMode,
                                target = currentTarget,
                                actualPitch = currentPitch,
                                actualCompass = currentCompass,
                                actualZoom = currentZoomRatio,
                                imageCapture = imageCapture,
                                onPhotoSaved = { result ->
                                    isCapturing = false
                                    lastShotResult = result
                                    currentTarget = generateRandomTarget()
                                },
                                onError = { exception ->
                                    isCapturing = false
                                    Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else if (!isShutterUnlocked) {
                            val pitchErr = abs(currentPitch - currentTarget.targetPitch)
                            val compassErr = compassDiff
                            val zoomErr = abs(currentZoomRatio - currentTarget.targetZoom)
                            val roastToast = getSavageRoast(currentMode, pitchErr, compassErr, zoomErr)
                            Toast.makeText(context, roastToast, Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Bottom Right: 3 Modes Selector (Normal, Pro, Peak)
                BottomRightModeSelector(
                    selectedMode = currentMode,
                    onModeSelected = { newMode ->
                        currentMode = newMode
                        currentTarget = generateRandomTarget()
                    }
                )
            }
        }

        // Post-Photo Preview & Share Dialog with Accuracy Rating & Roast
        lastShotResult?.let { result ->
            ShotCertificationDialog(
                result = result,
                onDismiss = { lastShotResult = null }
            )
        }

        // Dedicated Launch Loading Screen with Rotating Photography Tips
        if (isLaunchLoading) {
            LaunchTipsLoadingOverlay(
                isCameraReady = isCameraReady,
                onFinishLoading = { isLaunchLoading = false }
            )
        }
    }
}

// Live Spirit Level Indicator (Bullseye Level Tool) placed under the HUD
@Composable
fun LiveSpiritLevel(
    currentPitch: Float,
    targetPitch: Float,
    isLocked: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val deltaPitch = (currentPitch - targetPitch).coerceIn(-15f, 15f)
    val bubbleOffsetY = (deltaPitch / 15f) * 34.dp.value

    Box(
        modifier = modifier
            .size(94.dp)
            .border(
                width = 2.dp,
                color = if (isLocked) accentColor else Color.White.copy(alpha = 0.35f),
                shape = CircleShape
            )
            .background(Color.Black.copy(alpha = 0.45f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Target center ring (±5 deg zone)
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isLocked) accentColor else Color.White.copy(alpha = 0.40f),
                    shape = CircleShape
                )
        )

        // Crosshairs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 1.dp.toPx()
            val col = if (isLocked) accentColor.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.25f)
            drawLine(col, Offset(0f, size.height / 2f), Offset(size.width * 0.25f, size.height / 2f), stroke)
            drawLine(col, Offset(size.width * 0.75f, size.height / 2f), Offset(size.width, size.height / 2f), stroke)
            drawLine(col, Offset(size.width / 2f, 0f), Offset(size.width / 2f, size.height * 0.25f), stroke)
            drawLine(col, Offset(size.width / 2f, size.height * 0.75f), Offset(size.width / 2f, size.height), stroke)
        }

        // Moving Spirit Level Bubble
        Box(
            modifier = Modifier
                .offset(y = bubbleOffsetY.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(if (isLocked) accentColor else Color(0xFFFF5252))
        )
    }
}

// Full-screen App Launch Loading Screen with Rotating Tips
@Composable
fun LaunchTipsLoadingOverlay(
    isCameraReady: Boolean,
    onFinishLoading: () -> Unit
) {
    val tips = listOf(
        "Tilt your phone to match the target angle within ±5° to unlock the tactile shutter.",
        "Chad Mode is active by default! The spirit level is hidden — feel the tilt in your wrists.",
        "Drag the bottom slider to dial into the required zoom magnification.",
        "Peak Mode demands perfection: Angle, Compass heading, AND Zoom must all align!",
        "Every shot automatically saves both the raw photo and an authenticated Certificate Card to your Gallery.",
        "Crooked hands? TiltShift* will brutally roast your photography skills with zero mercy."
    )

    var tipIndex by remember { mutableIntStateOf(0) }
    var countdown by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (countdown > 0 || !isCameraReady) {
            delay(1500)
            tipIndex = (tipIndex + 1) % tips.size
            if (countdown > 0) countdown--
        }
        delay(600)
        onFinishLoading()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E13))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "TiltShift* Logo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(2.dp, Color(0xFF00E676), RoundedCornerShape(22.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "TiltShift*",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "let the camera poss tooooo",
                color = Color(0xFF00E676),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(28.dp))

            CircularProgressIndicator(
                color = Color(0xFF00E676),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Rotating Tip Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF161720),
                border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = "Tip",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PHOTOGRAPHY TIP",
                            color = Color(0xFFFFD54F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedContent(
                        targetState = tips[tipIndex],
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                        label = "tipAnimation"
                    ) { tipText ->
                        Text(
                            text = tipText,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enter Viewfinder Button (active once camera is ready)
            Button(
                onClick = onFinishLoading,
                enabled = isCameraReady,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E676),
                    contentColor = Color.Black,
                    disabledContainerColor = Color.White.copy(alpha = 0.12f),
                    disabledContentColor = Color.White.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp)
            ) {
                Text(
                    text = if (isCameraReady) "Enter Viewfinder" else "Calibrating Sensors...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// Puzzle Lock Status HUD Bar - ONE Sleek Single Capsule Box with NO nested sub-boxes
@Composable
fun PuzzleLockStatusHUD(
    mode: CameraMode,
    target: ModeTarget,
    currentPitch: Float,
    currentCompass: Float,
    currentZoom: Float,
    isAngleLocked: Boolean,
    isCompassLocked: Boolean,
    isZoomLocked: Boolean,
    isAllUnlocked: Boolean,
    onRerollTarget: () -> Unit
) {
    val pitchErr = abs(currentPitch - target.targetPitch)
    val compassDiff = abs((currentCompass - target.targetCompass + 540) % 360 - 180)
    val zoomErr = abs(currentZoom - target.targetZoom)

    // Proximity logic: Orange when close by, Green when perfectly matched
    val isZoomClose = !isZoomLocked && zoomErr <= 0.40f
    val isAngleClose = !isAngleLocked && pitchErr <= 14.0f
    val isCompassClose = !isCompassLocked && compassDiff <= 30.0f

    val anyClose = isZoomClose ||
            (mode != CameraMode.NORMAL && isAngleClose) ||
            (mode == CameraMode.PEAK && isCompassClose)

    // ONE Single Sleek Frosted Capsule Box (No nested boxes)
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF0C1322).copy(alpha = 0.65f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. Left Lock Indicator Circle
            val lockColor by animateColorAsState(
                targetValue = when {
                    isAllUnlocked -> Color(0xFF00E676)  // Green when all matched
                    anyClose -> Color(0xFFFFB300)       // Orange when close
                    else -> Color(0xFFFF5252)           // Red when locked/far
                },
                label = "lockColor"
            )

            Surface(
                shape = CircleShape,
                color = lockColor.copy(alpha = 0.18f),
                border = BorderStroke(1.2.dp, lockColor.copy(alpha = 0.65f)),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isAllUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                        contentDescription = "Lock Status",
                        tint = lockColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Subtle Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(18.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // 2. Zoom Requirement (Direct inline, no nested box)
            RequirementItem(
                icon = { tint ->
                    Icon(
                        imageVector = Icons.Filled.ZoomIn,
                        contentDescription = "Zoom",
                        tint = tint,
                        modifier = Modifier.size(15.dp)
                    )
                },
                label = "Zoom",
                targetValue = "${String.format(Locale.US, "%.1f", target.targetZoom)}x",
                currentValue = "${String.format(Locale.US, "%.1f", currentZoom)}x",
                isMatched = isZoomLocked,
                isClose = isZoomClose,
                isInlineStyle = true
            )

            // 3. Tilt Requirement (Active in Pro & Peak modes, no nested box)
            if (mode == CameraMode.PRO || mode == CameraMode.PEAK) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                RequirementItem(
                    icon = { tint ->
                        TiltedPhoneIcon(tint = tint)
                    },
                    label = "Tilt",
                    targetValue = "${target.targetPitch.roundToInt()}°",
                    currentValue = "${currentPitch.roundToInt()}°",
                    isMatched = isAngleLocked,
                    isClose = isAngleClose
                )
            }

            // 4. Heading / Compass Card with N on top and S on bottom!
            if (mode == CameraMode.PEAK) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                RequirementItem(
                    icon = { tint ->
                        CompassWithNS(accentColor = tint)
                    },
                    label = "Heading",
                    targetValue = "${target.targetCompass.roundToInt()}°",
                    currentValue = "${currentCompass.roundToInt()}°",
                    isMatched = isCompassLocked,
                    isClose = isCompassClose
                )
            }

            // Subtle Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(18.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // 5. Right Refresh Button Circle
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onRerollTarget)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "New Target",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Requirement Item - Borderless, clean element inside the single parent box
@Composable
fun RequirementItem(
    icon: @Composable (Color) -> Unit,
    label: String,
    targetValue: String,
    currentValue: String,
    isMatched: Boolean,
    isClose: Boolean,
    isInlineStyle: Boolean = false,
    modifier: Modifier = Modifier
) {
    val accentColor by animateColorAsState(
        targetValue = when {
            isMatched -> Color(0xFF00E676)   // GREEN when perfectly matched
            isClose -> Color(0xFFFFB300)     // ORANGE when close by
            else -> Color.White
        },
        label = "reqAccent"
    )

    Row(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Icon Badge
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.40f)),
            modifier = Modifier.size(26.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon(accentColor)
            }
        }

        Spacer(modifier = Modifier.width(5.dp))

        // Label & Value Content
        Column {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.60f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false
            )

            if (isInlineStyle) {
                // Inline Target | Current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = targetValue,
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(9.dp)
                            .background(Color.White.copy(alpha = 0.20f))
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = currentValue,
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            } else {
                // Stacked Target with small live value underneath
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = targetValue,
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentValue,
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

// Compass Icon with N on top, nautical needle in center, and S on bottom (no overlap)
@Composable
fun CompassWithNS(accentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Authentic magnetic nautical needle
        Canvas(modifier = Modifier.size(width = 8.dp, height = 11.dp)) {
            val w = size.width
            val h = size.height
            val midX = w / 2f
            val midY = h / 2f

            // North pointer (Red)
            val northRight = Path().apply {
                moveTo(midX, 0f)
                lineTo(w, midY)
                lineTo(midX, midY)
                close()
            }
            drawPath(northRight, color = Color(0xFFFF3B30))

            val northLeft = Path().apply {
                moveTo(midX, 0f)
                lineTo(0f, midY)
                lineTo(midX, midY)
                close()
            }
            drawPath(northLeft, color = Color(0xFFFF6961))

            // South pointer (Light grey / White)
            val southRight = Path().apply {
                moveTo(midX, h)
                lineTo(w, midY)
                lineTo(midX, midY)
                close()
            }
            drawPath(southRight, color = Color.White.copy(alpha = 0.55f))

            val southLeft = Path().apply {
                moveTo(midX, h)
                lineTo(0f, midY)
                lineTo(midX, midY)
                close()
            }
            drawPath(southLeft, color = Color.White.copy(alpha = 0.85f))
        }

        // 'N' letter pinned safely near top edge
        Text(
            text = "N",
            color = Color(0xFFFF3B30),
            fontSize = 6.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 'S' letter pinned safely above bottom border
        Text(
            text = "S",
            color = Color.White.copy(alpha = 0.70f),
            fontSize = 6.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Rotated 45-deg phone/rectangle icon to match user screenshot
@Composable
fun TiltedPhoneIcon(tint: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .rotate(45f)
            .border(1.8.dp, tint, RoundedCornerShape(3.dp))
    )
}

// Glassmorphic Zoom Bar matching user mock
@Composable
fun GlassmorphicZoomBar(
    currentZoom: Float,
    targetZoom: Float,
    minZoom: Float = 1.0f,
    maxZoom: Float = 8.0f,
    isZoomLocked: Boolean,
    isZoomClose: Boolean,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor by animateColorAsState(
        targetValue = when {
            isZoomLocked -> Color(0xFF00E676)
            isZoomClose -> Color(0xFFFFB300)
            else -> Color.White
        },
        label = "zoomAccent"
    )

    // Clean Translucent Capsule with NO Outline and compact padding
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0C1322).copy(alpha = 0.65f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Left Search Icon
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ZoomIn,
                        contentDescription = "Zoom Search",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Left Label: "Zoom"
            Text(
                text = "Zoom",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Middle Clean Slider (Pure minimal track and thumb, no tick marks)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val trackWidthPx = constraints.maxWidth.toFloat()
                val fraction = ((currentZoom - minZoom) / (maxZoom - minZoom)).coerceIn(0f, 1f)
                val thumbXPx = fraction * trackWidthPx

                // Inactive track (no outline, smooth pill)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                )

                // Active track
                Box(
                    modifier = Modifier
                        .width(with(LocalDensity.current) { thumbXPx.toDp() })
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    accentColor.copy(alpha = 0.40f),
                                    accentColor.copy(alpha = 0.90f)
                                )
                            )
                        )
                )

                // Thumb: Clean glowing circle
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF0A101D).copy(alpha = 0.85f),
                    border = BorderStroke(1.8.dp, accentColor),
                    modifier = Modifier
                        .offset { IntOffset((thumbXPx - 9.dp.toPx()).roundToInt(), 0) }
                        .size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = accentColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(6.dp)
                        ) {}
                    }
                }

                // Invisible touch Slider
                Slider(
                    value = currentZoom,
                    onValueChange = onZoomChange,
                    valueRange = minZoom..maxZoom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 4. Right Status Capsule Badge: "2.0x | 🔒" (No outline)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", currentZoom)}x",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(11.dp)
                            .background(Color.White.copy(alpha = 0.20f))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = if (isZoomLocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                        contentDescription = "Zoom Lock",
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomRightModeSelector(
    selectedMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.Black.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
    ) {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CameraMode.values().forEach { mode ->
                val isSelected = mode == selectedMode
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) mode.accentColor.copy(alpha = 0.25f) else Color.Transparent,
                    label = "modeBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) mode.accentColor else Color.White.copy(alpha = 0.55f),
                    label = "modeText"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .clickable { onModeSelected(mode) }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.displayName,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TactilePuzzleShutterButton(
    accentColor: Color,
    isUnlocked: Boolean,
    isCapturing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "shutterPressScale"
    )

    val buttonBorderColor by animateColorAsState(
        targetValue = if (isUnlocked) accentColor else Color.White.copy(alpha = 0.35f),
        label = "shutterBorder"
    )

    val innerButtonColor by animateColorAsState(
        targetValue = when {
            isCapturing -> Color.Gray
            isUnlocked -> Color.White
            else -> Color(0xFF2A2A2E)
        },
        label = "shutterInner"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(86.dp)
            .border(width = 4.dp, color = buttonBorderColor, shape = CircleShape)
            .padding(4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(animatedScale)
                .clip(CircleShape)
                .background(innerButtonColor),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FocusIndicator(offset: Offset, color: Color, onDismiss: () -> Unit) {
    LaunchedEffect(offset) {
        delay(1200)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .offset { IntOffset((offset.x - 36.dp.toPx()).roundToInt(), (offset.y - 36.dp.toPx()).roundToInt()) }
            .size(72.dp)
            .border(width = 1.5.dp, color = color, shape = RoundedCornerShape(8.dp))
    )
}

// Dialog to view the captured photo + certificate with sharing option & savage roast
@Composable
fun ShotCertificationDialog(
    result: ShotResult,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showCertificateView by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.90f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF16171D))
                    .border(BorderStroke(1.5.dp, result.mode.accentColor.copy(alpha = 0.5f)), RoundedCornerShape(24.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TiltShift* Certificate",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Both Photo & Certificate Saved to Gallery",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle tabs: Certificate vs Raw Photo
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (showCertificateView) result.mode.accentColor else Color.Transparent)
                            .clickable { showCertificateView = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Certificate",
                            color = if (showCertificateView) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!showCertificateView) result.mode.accentColor else Color.Transparent)
                            .clickable { showCertificateView = false }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Photo",
                            color = if (!showCertificateView) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image Display
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (showCertificateView) 0.675f else 1.33f)
                ) {
                    val displayBmp = if (showCertificateView) result.certBitmap else result.photoBitmap
                    Image(
                        bitmap = displayBmp.asImageBitmap(),
                        contentDescription = "Capture Result",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accuracy Rating Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(result.mode.accentColor.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, result.mode.accentColor.copy(alpha = 0.35f)), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACCURACY SCORE",
                            color = result.mode.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", result.accuracy)}%",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = result.mode.accentColor
                    ) {
                        Text(
                            text = result.grade,
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Savage Roast Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF231E24),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SAVAGE AUDIT VERDICT 🔥",
                            color = Color(0xFFFF8A80),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.roast,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: Share & Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            shareImage(context, if (showCertificateView) result.certUri else result.photoUri)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = result.mode.accentColor,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Share", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(text = "Next Target", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

fun shareImage(context: Context, uri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share TiltShift* Shot"))
}

private fun openPhotosGallery(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        type = "image/*"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallbackIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_GALLERY)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(fallbackIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to open gallery", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun takePhotoWithCertification(
    context: Context,
    mode: CameraMode,
    target: ModeTarget,
    actualPitch: Float,
    actualCompass: Float,
    actualZoom: Float,
    imageCapture: ImageCapture,
    onPhotoSaved: (ShotResult) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val tempFile = File(context.cacheDir, "temp_capture_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val rawBitmap = BitmapFactory.decodeFile(tempFile.absolutePath)

                        val pitchErr = abs(actualPitch - target.targetPitch)
                        val compassDiff = abs((actualCompass - target.targetCompass + 540) % 360 - 180)
                        val zoomErr = abs(actualZoom - target.targetZoom)

                        val accuracy = when (mode) {
                            CameraMode.NORMAL -> {
                                (100f - (zoomErr / 0.15f) * 8f).coerceIn(85f, 100f)
                            }
                            CameraMode.PRO -> {
                                val aScore = (100f - (pitchErr / 5f) * 6f)
                                val zScore = (100f - (zoomErr / 0.15f) * 6f)
                                ((aScore + zScore) / 2f).coerceIn(88f, 100f)
                            }
                            CameraMode.PEAK -> {
                                val aScore = (100f - (pitchErr / 5f) * 4f)
                                val cScore = (100f - (compassDiff / 5.5f) * 4f)
                                val zScore = (100f - (zoomErr / 0.15f) * 4f)
                                ((aScore + cScore + zScore) / 3f).coerceIn(92f, 100f)
                            }
                        }

                        val grade = when {
                            accuracy >= 98f -> "S+ PERFECT"
                            accuracy >= 95f -> "S MASTER"
                            accuracy >= 90f -> "A ACCURATE"
                            else -> "B QUALIFIED"
                        }

                        val roast = getSavageRoast(mode, pitchErr, compassDiff, zoomErr)

                        val certBitmap = createCertificateBitmap(
                            photoBitmap = rawBitmap,
                            mode = mode,
                            target = target,
                            actualPitch = actualPitch,
                            actualCompass = actualCompass,
                            actualZoom = actualZoom,
                            accuracy = accuracy,
                            grade = grade,
                            roast = roast
                        )

                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
                        val photoUri = saveBitmapToGallery(
                            context = context,
                            bitmap = rawBitmap,
                            displayName = "TiltShift_PHOTO_${mode.displayName.uppercase()}_$timestamp.jpg"
                        )

                        val certUri = saveBitmapToGallery(
                            context = context,
                            bitmap = certBitmap,
                            displayName = "TiltShift_CERT_${mode.displayName.uppercase()}_$timestamp.jpg"
                        )

                        tempFile.delete()

                        withContext(Dispatchers.Main) {
                            if (photoUri != null && certUri != null) {
                                onPhotoSaved(
                                    ShotResult(
                                        photoUri = photoUri,
                                        certUri = certUri,
                                        photoBitmap = rawBitmap,
                                        certBitmap = certBitmap,
                                        accuracy = accuracy,
                                        grade = grade,
                                        roast = roast,
                                        mode = mode,
                                        target = target,
                                        actualPitch = actualPitch,
                                        actualCompass = actualCompass,
                                        actualZoom = actualZoom
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error saving shot: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

// Draw the stylized certification graphic card with savage roast
fun createCertificateBitmap(
    photoBitmap: Bitmap,
    mode: CameraMode,
    target: ModeTarget,
    actualPitch: Float,
    actualCompass: Float,
    actualZoom: Float,
    accuracy: Float,
    grade: String,
    roast: String
): Bitmap {
    val cardWidth = 1080
    val cardHeight = 1600
    val output = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    // 1. Dark Slate Card Background
    val bgPaint = Paint().apply { color = AndroidColor.parseColor("#101216") }
    canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), bgPaint)

    // Outer double border
    val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = AndroidColor.parseColor(if (mode == CameraMode.PEAK) "#FFD54F" else if (mode == CameraMode.PRO) "#00E5FF" else "#00E676")
    }
    canvas.drawRoundRect(RectF(32f, 32f, cardWidth - 32f, cardHeight - 32f), 36f, 36f, borderPaint)

    val innerBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = AndroidColor.parseColor("#33FFFFFF")
    }
    canvas.drawRoundRect(RectF(48f, 48f, cardWidth - 48f, cardHeight - 48f), 24f, 24f, innerBorderPaint)

    // 2. Title & Header
    val headerPaint = Paint().apply {
        color = AndroidColor.WHITE
        textSize = 42f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    canvas.drawText("TILTSHIFT* AUDIT VERIFICATION", cardWidth / 2f, 125f, headerPaint)

    val subHeaderPaint = Paint().apply {
        color = AndroidColor.parseColor("#90CAF9")
        textSize = 23f
        textAlign = Paint.Align.CENTER
        letterSpacing = 0.12f
    }
    canvas.drawText("OFFICIAL SHOT CERTIFICATE & SENSOR AUDIT", cardWidth / 2f, 168f, subHeaderPaint)

    // 3. Embedded Photo Box
    val photoDest = RectF(72f, 205f, cardWidth - 72f, 850f)
    val photoFramePaint = Paint().apply {
        color = AndroidColor.BLACK
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(photoDest, 20f, 20f, photoFramePaint)

    val srcRect = Rect(0, 0, photoBitmap.width, photoBitmap.height)
    canvas.drawBitmap(photoBitmap, srcRect, photoDest, Paint(Paint.FILTER_BITMAP_FLAG))

    val photoOutlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = AndroidColor.parseColor("#55FFFFFF")
    }
    canvas.drawRoundRect(photoDest, 20f, 20f, photoOutlinePaint)

    // 4. Accuracy & Grade Badge
    val badgeBgPaint = Paint().apply {
        color = AndroidColor.parseColor("#1E212B")
        style = Paint.Style.FILL
    }
    val badgeRect = RectF(72f, 880f, cardWidth - 72f, 985f)
    canvas.drawRoundRect(badgeRect, 18f, 18f, badgeBgPaint)

    val scoreLabelPaint = Paint().apply {
        color = AndroidColor.parseColor("#AAAAAA")
        textSize = 20f
        typeface = Typeface.DEFAULT
    }
    canvas.drawText("ACCURACY RATING", 104f, 920f, scoreLabelPaint)

    val scoreValuePaint = Paint().apply {
        color = AndroidColor.WHITE
        textSize = 44f
        isFakeBoldText = true
    }
    canvas.drawText("${String.format(Locale.US, "%.1f", accuracy)}%", 104f, 970f, scoreValuePaint)

    val gradeBadgePaint = Paint().apply {
        color = borderPaint.color
        style = Paint.Style.FILL
    }
    val gradeRect = RectF(cardWidth - 360f, 900f, cardWidth - 100f, 970f)
    canvas.drawRoundRect(gradeRect, 16f, 16f, gradeBadgePaint)

    val gradeTextPaint = Paint().apply {
        color = AndroidColor.BLACK
        textSize = 28f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(grade, gradeRect.centerX(), gradeRect.centerY() + 10f, gradeTextPaint)

    // 5. Sensor Audit Table
    var rowY = 1015f
    val rowHeight = 70f

    fun drawAuditRow(label: String, targetStr: String, actualStr: String, deltaStr: String, verified: Boolean) {
        val rowPaint = Paint().apply {
            color = AndroidColor.parseColor("#191C24")
            style = Paint.Style.FILL
        }
        val rRect = RectF(72f, rowY, cardWidth - 72f, rowY + rowHeight)
        canvas.drawRoundRect(rRect, 12f, 12f, rowPaint)

        val labelP = Paint().apply {
            color = AndroidColor.WHITE
            textSize = 24f
            isFakeBoldText = true
        }
        canvas.drawText(label, 96f, rowY + 44f, labelP)

        val valP = Paint().apply {
            color = AndroidColor.parseColor("#B0BEC5")
            textSize = 21f
        }
        canvas.drawText("Target: $targetStr   Actual: $actualStr ($deltaStr)", 280f, rowY + 44f, valP)

        val statusP = Paint().apply {
            color = if (verified) AndroidColor.parseColor("#00E676") else AndroidColor.parseColor("#FF5252")
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(if (verified) "LOCKED" else "FREE", cardWidth - 96f, rowY + 44f, statusP)

        rowY += rowHeight + 12f
    }

    // Zoom Row
    drawAuditRow(
        label = "ZOOM",
        targetStr = "${String.format(Locale.US, "%.1f", target.targetZoom)}x",
        actualStr = "${String.format(Locale.US, "%.1f", actualZoom)}x",
        deltaStr = "±${String.format(Locale.US, "%.2f", abs(actualZoom - target.targetZoom))}x",
        verified = abs(actualZoom - target.targetZoom) <= 0.15f
    )

    // Tilt Angle Row
    drawAuditRow(
        label = "LEVEL / TILT",
        targetStr = "${target.targetPitch.roundToInt()}°",
        actualStr = "${actualPitch.roundToInt()}°",
        deltaStr = "±${String.format(Locale.US, "%.1f", abs(actualPitch - target.targetPitch))}°",
        verified = mode != CameraMode.NORMAL && abs(actualPitch - target.targetPitch) <= 5.0f
    )

    // Compass Row
    drawAuditRow(
        label = "COMPASS",
        targetStr = "${target.targetCompass.roundToInt()}°",
        actualStr = "${actualCompass.roundToInt()}°",
        deltaStr = "±${abs((actualCompass - target.targetCompass + 540) % 360 - 180).roundToInt()}°",
        verified = mode == CameraMode.PEAK && abs((actualCompass - target.targetCompass + 540) % 360 - 180) <= 5.5f
    )

    // 6. Savage Roast Box on Certificate
    val roastRect = RectF(72f, rowY + 8f, cardWidth - 72f, rowY + 115f)
    val roastBgPaint = Paint().apply {
        color = AndroidColor.parseColor("#261B1E")
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(roastRect, 14f, 14f, roastBgPaint)

    val roastTitlePaint = Paint().apply {
        color = AndroidColor.parseColor("#FF8A80")
        textSize = 20f
        isFakeBoldText = true
    }
    canvas.drawText("SAVAGE AUDIT VERDICT: ", 96f, rowY + 46f, roastTitlePaint)

    val roastContentPaint = Paint().apply {
        color = AndroidColor.WHITE
        textSize = 22f
    }
    canvas.drawText(roast, 96f, rowY + 88f, roastContentPaint)

    // 7. Footer
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(System.currentTimeMillis())
    val footerPaint = Paint().apply {
        color = AndroidColor.parseColor("#66FFFFFF")
        textSize = 19f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("MODE: ${mode.displayName.uppercase()}  |  TIMESTAMP: $dateStr", cardWidth / 2f, cardHeight - 80f, footerPaint)
    canvas.drawText("AUTHENTICATED BY TILTSHIFT* ON-DEVICE HARDWARE SENSORS", cardWidth / 2f, cardHeight - 50f, footerPaint)

    return output
}

// Helper to save a Bitmap to MediaStore Gallery
fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TiltShift")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return null
    try {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, contentValues, null, null)
        }
        return uri
    } catch (e: Exception) {
        context.contentResolver.delete(uri, null, null)
        return null
    }
}

@Composable
fun PermissionScreen(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E1E1E)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Camera Permission",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "TiltShift* Permission",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "TiltShift* needs permission to access your camera hardware to capture photos and save them to your gallery.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFFAAAAAA),
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = onRequestPermission,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Grant Permission",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
