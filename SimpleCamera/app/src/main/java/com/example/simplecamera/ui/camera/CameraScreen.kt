package com.example.simplecamera.ui.camera

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.example.simplecamera.data.RoastsRepository
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
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Size
import android.widget.Toast
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.produceState
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
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

fun getCardinalDirection(degrees: Float): String {
    val normalized = (degrees % 360 + 360) % 360
    return when {
        normalized >= 337.5f || normalized < 22.5f -> "N"
        normalized >= 22.5f && normalized < 67.5f -> "NE"
        normalized >= 67.5f && normalized < 112.5f -> "E"
        normalized >= 112.5f && normalized < 157.5f -> "SE"
        normalized >= 157.5f && normalized < 202.5f -> "S"
        normalized >= 202.5f && normalized < 247.5f -> "SW"
        normalized >= 247.5f && normalized < 292.5f -> "W"
        else -> "NW"
    }
}

fun getSavageRoast(mode: CameraMode, pitchErr: Float, compassErr: Float, zoomErr: Float): String {
    return RoastsRepository.generateSavageRoast(mode, pitchErr, compassErr, zoomErr)
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

    // In-App Useless Peaktures Gallery State
    var showUselessPeakturesGallery by remember { mutableStateOf(false) }

    var currentMode by remember { mutableStateOf(CameraMode.PRO) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
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

        // Baby Mode Full Screen Alignment & Directional Assists
        if (isCameraReady && currentDifficulty.showLevelHelper && (currentMode == CameraMode.PRO || currentMode == CameraMode.PEAK)) {
            BabyModeAssistOverlay(
                currentPitch = currentPitch,
                targetPitch = currentTarget.targetPitch,
                currentCompass = currentCompass,
                targetCompass = currentTarget.targetCompass,
                compassSector = getCardinalDirection(currentTarget.targetCompass),
                currentZoom = currentZoomRatio,
                targetZoom = currentTarget.targetZoom,
                currentMode = currentMode,
                isAngleLocked = isAngleLocked,
                isCompassLocked = isCompassLocked,
                isZoomLocked = isZoomLocked,
                isShutterUnlocked = isShutterUnlocked
            )
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

        // Top Controls Header (Symmetric Layout)
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Difficulty Dropdown (Top Left)
                Box(modifier = Modifier.align(Alignment.CenterStart)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.70f),
                        border = BorderStroke(1.5.dp, currentDifficulty.badgeColor),
                        modifier = Modifier.clickable { showDifficultyMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "Difficulty ▾",
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

                // Brand & Mode Badge (Dead Center)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, currentMode.accentColor.copy(alpha = 0.35f)),
                    modifier = Modifier.align(Alignment.Center)
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
                        .size(42.dp)
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.50f))
                        .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cameraswitch,
                        contentDescription = "Switch camera",
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
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
            // HIGH-CONTRAST BOLD & CLEAR MESSAGE BANNER (No emojis, sleek typography)
            if (currentMode != CameraMode.NORMAL) {
                val pErr = abs(currentPitch - currentTarget.targetPitch)
                val isAllMatched = isShutterUnlocked

                val messageCategory = if (isAllMatched) "ALIGNMENT LOCKED (±5°)" else "YOU HAVE A MESSAGE"
                val messageText = if (isAllMatched) {
                    "Alignment holding steady! Don't tremble, capture now."
                } else when {
                    !isAngleLocked -> RoastsRepository.getLiveMessageText(pErr, false)
                    !isZoomLocked -> "Tilt locked! Now dial the zoom slider (off by ${String.format(Locale.US, "%.1f", abs(currentZoomRatio - currentTarget.targetZoom))}x)!"
                    currentMode == CameraMode.PEAK && !isCompassLocked -> "Tilt & Zoom locked! Rotate phone to face ${currentTarget.targetCompass.roundToInt()}° (${getCardinalDirection(currentTarget.targetCompass)})!"
                    else -> "Almost there, steady your hands!"
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF14151E).copy(alpha = 0.96f),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (isAllMatched) Color(0xFF00E676) else Color(0xFFFF5252)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isAllMatched) Color(0xFF00E676).copy(alpha = 0.25f) else Color(0xFFFF5252).copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, if (isAllMatched) Color(0xFF00E676) else Color(0xFFFF5252)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isAllMatched) "OK" else "!",
                                    color = if (isAllMatched) Color(0xFF00E676) else Color(0xFFFF5252),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
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
                                    text = messageCategory,
                                    color = if (isAllMatched) Color(0xFF00E676) else Color(0xFFFF8A80),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isAllMatched) "[ALL LOCKED]" else "Off by ${String.format(Locale.US, "%.1f", pErr)}°",
                                    color = if (isAllMatched) Color(0xFF00E676) else Color(0xFFFF5252),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = messageText,
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
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Shutter Row + Bottom Right Mode Selector (Perfect Horizontal Symmetry)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Bottom Left: Open Useless Peaktures Gallery Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f)),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterStart)
                        .clickable { showUselessPeakturesGallery = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Useless Peaktures Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Center: Tactile Shutter Button with Lock / Unlock state (Dead Center)
                Box(modifier = Modifier.align(Alignment.Center)) {
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
                }

                // Bottom Right: 3 Modes Selector (Normal, Pro, Peak)
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    BottomRightModeSelector(
                        selectedMode = currentMode,
                        onModeSelected = { newMode ->
                            currentMode = newMode
                            currentTarget = generateRandomTarget()
                        }
                    )
                }
            }
        }

        // Post-Photo Preview & Share Dialog with Accuracy Rating & Roast
        lastShotResult?.let { result ->
            ShotCertificationDialog(
                result = result,
                onDismiss = { lastShotResult = null }
            )
        }

        // In-App Useless Peaktures Gallery
        if (showUselessPeakturesGallery) {
            UselessPeakturesGallery(
                onDismiss = { showUselessPeakturesGallery = false }
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

// -------------------------------------------------------------
// BABY MODE COMPLETE ON-SCREEN ALIGNMENT ASSISTS
// -------------------------------------------------------------
@Composable
fun BabyModeAssistOverlay(
    currentPitch: Float,
    targetPitch: Float,
    currentCompass: Float,
    targetCompass: Float,
    compassSector: String,
    currentZoom: Float,
    targetZoom: Float,
    currentMode: CameraMode,
    isAngleLocked: Boolean,
    isCompassLocked: Boolean,
    isZoomLocked: Boolean,
    isShutterUnlocked: Boolean
) {
    val angleTolerance = 5.0f
    val compassTolerance = 5.5f

    // Pitch delta: positive means target is higher (tilt backward/up), negative means tilt forward/down
    val pitchDelta = targetPitch - currentPitch
    val pitchNeedsUp = pitchDelta > angleTolerance
    val pitchNeedsDown = pitchDelta < -angleTolerance

    // Raw compass delta: positive means target is to the right (turn right), negative means turn left
    val rawCompassDiff = (targetCompass - currentCompass + 540) % 360 - 180
    val compassNeedsLeft = (currentMode == CameraMode.PEAK) && (rawCompassDiff < -compassTolerance)
    val compassNeedsRight = (currentMode == CameraMode.PEAK) && (rawCompassDiff > compassTolerance)

    // Pulsing animation for directional guidance
    val infiniteTransition = rememberInfiniteTransition(label = "babyModeBounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. TOP DIRECTIONAL ASSIST: TILT UP / BACK
        if (pitchNeedsUp) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (175 + bounceOffset).dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.5.dp, Color(0xFFFFB300).copy(alpha = glowAlpha))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "▲",
                            color = Color(0xFFFFB300),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "TILT UP / BACK: ${pitchDelta.toInt()}°",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "▲",
                            color = Color(0xFFFFB300),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 2. BOTTOM DIRECTIONAL ASSIST: TILT DOWN / FORWARD
        if (pitchNeedsDown) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-195 - bounceOffset).dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.5.dp, Color(0xFFFFB300).copy(alpha = glowAlpha))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "▼",
                            color = Color(0xFFFFB300),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "TILT DOWN / FORWARD: ${abs(pitchDelta).toInt()}°",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "▼",
                            color = Color(0xFFFFB300),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 3. LEFT DIRECTIONAL ASSIST: TURN LEFT (PEAK mode)
        if (compassNeedsLeft) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .offset(x = (-bounceOffset).dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.5.dp, Color(0xFF00E5FF).copy(alpha = glowAlpha))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "◀",
                            color = Color(0xFF00E5FF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "TURN LEFT",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${abs(rawCompassDiff).toInt()}°",
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 4. RIGHT DIRECTIONAL ASSIST: TURN RIGHT (PEAK mode)
        if (compassNeedsRight) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .offset(x = bounceOffset.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.5.dp, Color(0xFF00E5FF).copy(alpha = glowAlpha))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "▶",
                            color = Color(0xFF00E5FF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "TURN RIGHT",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${rawCompassDiff.toInt()}°",
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 5. CENTER VIEWING GIMBAL / LEVEL RETICLE (Interactive 2D Guide)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp),
            contentAlignment = Alignment.Center
        ) {
            val clampedPitch = pitchDelta.coerceIn(-20f, 20f)
            val clampedYaw = if (currentMode == CameraMode.PEAK) rawCompassDiff.coerceIn(-25f, 25f) else 0f
            val bubbleOffsetX = (clampedYaw / 25f) * 36.dp.value
            val bubbleOffsetY = (-clampedPitch / 20f) * 36.dp.value
            val isGimbalLocked = isAngleLocked && (currentMode != CameraMode.PEAK || isCompassLocked)

            // Outer ring
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .border(
                        width = 2.dp,
                        color = if (isGimbalLocked) Color(0xFF00E676) else Color.White.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
                    .background(Color.Black.copy(alpha = 0.40f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Target Bullseye ring (Zone where user wants to put the bubble)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(
                            width = 2.dp,
                            color = if (isGimbalLocked) Color(0xFF00E676) else Color.White.copy(alpha = 0.50f),
                            shape = CircleShape
                        )
                )

                // Reticle Crosshairs & Direction Vector Line
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 1.dp.toPx()
                    val col = if (isGimbalLocked) Color(0xFF00E676).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.25f)
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Crosshairs
                    drawLine(col, Offset(0f, center.y), Offset(size.width * 0.28f, center.y), stroke)
                    drawLine(col, Offset(size.width * 0.72f, center.y), Offset(size.width, center.y), stroke)
                    drawLine(col, Offset(center.x, 0f), Offset(center.x, size.height * 0.28f), stroke)
                    drawLine(col, Offset(center.x, size.height * 0.72f), Offset(center.x, size.height), stroke)

                    // Connecting guide vector if not locked
                    if (!isGimbalLocked) {
                        val bubblePx = Offset(center.x + bubbleOffsetX.dp.toPx(), center.y + bubbleOffsetY.dp.toPx())
                        drawLine(
                            color = Color(0xFFFFB300).copy(alpha = 0.6f),
                            start = center,
                            end = bubblePx,
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }

                // Moving Target Bubble
                Box(
                    modifier = Modifier
                        .offset(x = bubbleOffsetX.dp, y = bubbleOffsetY.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isGimbalLocked) Color(0xFF00E676) else Color(0xFFFF5252))
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGimbalLocked) {
                        Text(
                            text = "✓",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // 6. FLOATING LIVE CONTEXT GUIDANCE PILL (Directly beneath Gimbal)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 80.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isShutterUnlocked) Color(0xFF00E676) else Color.Black.copy(alpha = 0.80f),
                    border = BorderStroke(
                        1.5.dp,
                        if (isShutterUnlocked) Color.White else Color.White.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isShutterUnlocked) {
                            Text(
                                text = "🎯 PERFECT! TAP SHUTTER TO SHOOT! 🎯",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        } else if (!isAngleLocked) {
                            Text(
                                text = if (pitchDelta > 0) "📱 Tilt back ${pitchDelta.toInt()}°" else "📱 Tilt forward ${abs(pitchDelta).toInt()}°",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (currentMode == CameraMode.PEAK && !isCompassLocked) {
                            Text(
                                text = if (rawCompassDiff > 0) "🧭 Turn right ${rawCompassDiff.toInt()}° ($compassSector)" else "🧭 Turn left ${abs(rawCompassDiff).toInt()}° ($compassSector)",
                                color = Color(0xFF00E5FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (!isZoomLocked) {
                            Text(
                                text = "🔍 Adjust zoom to ${String.format("%.1fx", targetZoom)}",
                                color = Color(0xFFFFB300),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TILTSHIFT* SPRITE SHEET DOODLE LOADING SCREEN
// -------------------------------------------------------------
@Composable
fun LaunchTipsLoadingOverlay(
    isCameraReady: Boolean,
    onFinishLoading: () -> Unit
) {
    val stickmanDrawables = remember {
        listOf(
            R.drawable.stickman_01, R.drawable.stickman_02, R.drawable.stickman_03, R.drawable.stickman_04,
            R.drawable.stickman_05, R.drawable.stickman_06, R.drawable.stickman_07, R.drawable.stickman_08,
            R.drawable.stickman_09, R.drawable.stickman_10, R.drawable.stickman_11, R.drawable.stickman_12,
            R.drawable.stickman_13, R.drawable.stickman_14, R.drawable.stickman_15, R.drawable.stickman_16
        )
    }

    val loadingBarDrawables = remember {
        listOf(
            R.drawable.loading_bar_00,
            R.drawable.loading_bar_01,
            // skipped: loading_bar_02
            R.drawable.loading_bar_03,
            // skipped: loading_bar_04
            R.drawable.loading_bar_05,
            R.drawable.loading_bar_06,
            R.drawable.loading_bar_07,
            // skipped: loading_bar_08
            R.drawable.loading_bar_09,
            // skipped: loading_bar_10
            R.drawable.loading_bar_11,
            R.drawable.loading_bar_12,
            R.drawable.loading_bar_13,
            // skipped: loading_bar_14
            R.drawable.loading_bar_15,
            R.drawable.loading_bar_16
        )
    }

    val messages = remember {
        listOf(
            "CALIBRATING THE CHAOS...",
            "FINDING A GOOD ANGLE...",
            "HOLD ON...",
            "TRYING NOT TO FALL...",
            "MEASURING YOUR QUESTIONABLE CAMERA SKILLS...",
            "ALMOST...!"
        )
    }

    var progress by remember { mutableFloatStateOf(0f) }
    var messageIndex by remember { mutableIntStateOf(0) }
    var isDone by remember { mutableStateOf(false) }
    var characterFrameIndex by remember { mutableIntStateOf(0) }

    // Character animation loop reacting to progress
    LaunchedEffect(progress) {
        val (startFrame, endFrame) = when {
            progress < 20f -> 0 to 3
            progress < 45f -> 4 to 8
            progress < 70f -> 9 to 12
            progress < 90f -> 13 to 14
            else -> 14 to 15
        }

        while (!isDone) {
            val nextFrame = if (characterFrameIndex in startFrame..endFrame) {
                if (characterFrameIndex >= endFrame) startFrame else characterFrameIndex + 1
            } else {
                startFrame
            }
            characterFrameIndex = nextFrame
            val delayMs = listOf(110L, 125L, 140L, 120L).random()
            delay(delayMs)
        }
    }

    // Progress simulation tied to camera initialization
    LaunchedEffect(isCameraReady) {
        while (progress < 85f) {
            delay(120)
            progress += Random.nextFloat() * 4f + 2f
            if (Random.nextFloat() > 0.65f) {
                messageIndex = (messageIndex + 1) % messages.size
            }
        }
        while (!isCameraReady) {
            delay(80)
        }
        while (progress < 100f) {
            delay(60)
            progress = (progress + 6f).coerceAtMost(100f)
        }
        isDone = true
        characterFrameIndex = 15
        delay(1200)
        onFinishLoading()
    }

    val barFrameIndex = remember(progress, isDone) {
        val maxIdx = loadingBarDrawables.lastIndex
        if (!isDone) {
            (progress / 100f * maxIdx).toInt().coerceIn(0, maxIdx)
        } else {
            maxIdx
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F2))
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // GOOFY STICKMAN PHOTOGRAPHER (Hand-drawn doodle on paper)
            Box(
                modifier = Modifier
                    .size(width = 250.dp, height = 220.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = stickmanDrawables[characterFrameIndex.coerceIn(0, 15)]),
                    contentDescription = "Goofy Stickman Photographer",
                    modifier = Modifier.size(if (isDone) 200.dp else 190.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Handwritten-style "LOADING..." text
            Text(
                text = if (isDone) "DONE!" else "LOADING...",
                color = Color(0xFF1A1A1A),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // CRAYON LOADING BAR SPRITE (00 to 16, with ending loading sprite 18 on done)
            Image(
                painter = painterResource(
                    id = if (isDone) R.drawable.loading_bar_18 else loadingBarDrawables[barFrameIndex.coerceIn(0, 16)]
                ),
                contentDescription = "Loading bar",
                modifier = Modifier
                    .width(320.dp)
                    .height(62.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comedic Handwritten Message
            Text(
                text = if (isDone) "Ready to tilt and shoot!" else messages[messageIndex],
                color = Color(0xFF555555),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
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

    // ONE Single Sleek Frosted Capsule Box (No nested boxes, reduced padding)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0C1322).copy(alpha = 0.65f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
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
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isAllUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                        contentDescription = "Lock Status",
                        tint = lockColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Subtle Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // 2. Zoom Requirement (Label on top, pure icon in middle, values on bottom)
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
                isClose = isZoomClose
            )

            // 3. Tilt Requirement (Active in Pro & Peak modes)
            if (mode == CameraMode.PRO || mode == CameraMode.PEAK) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
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

            // 4. Compass Requirement (Renamed to Compass, shows live facing direction under there)
            if (mode == CameraMode.PEAK) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                val targetCardinal = getCardinalDirection(target.targetCompass)
                val currentCardinal = getCardinalDirection(currentCompass)

                RequirementItem(
                    icon = { tint ->
                        Text(
                            text = targetCardinal,
                            color = tint,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    },
                    label = "Compass",
                    targetValue = "${target.targetCompass.roundToInt()}°",
                    currentValue = "${currentCompass.roundToInt()}° ($currentCardinal)",
                    isMatched = isCompassLocked,
                    isClose = isCompassClose
                )
            }

            // Subtle Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // 5. Right Refresh Button Circle
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onRerollTarget)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "New Target",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// Requirement Item - Clean parallel alignment: Label on top, pure icon in center (NO circle), values on bottom
@Composable
fun RequirementItem(
    icon: @Composable (Color) -> Unit,
    label: String,
    targetValue: String,
    currentValue: String,
    isMatched: Boolean,
    isClose: Boolean,
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        // 1. Parallel Label on top ("Zoom", "Tilt", "Heading")
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )

        Spacer(modifier = Modifier.height(1.dp))

        // 2. Pure Icon / Cardinal direction (shifted slightly up so SE, S, W, SW never cut off)
        Box(
            modifier = Modifier
                .height(16.dp)
                .offset(y = (-1.5).dp),
            contentAlignment = Alignment.Center
        ) {
            icon(accentColor)
        }

        Spacer(modifier = Modifier.height(1.dp))

        // 3. Values: Target and Current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = targetValue,
                color = accentColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = currentValue,
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

// Pure Compass with N on top, nautical needle in center, and S on bottom (no circle outline)
@Composable
fun CompassWithNS(accentColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "N",
            color = if (accentColor == Color.White) Color(0xFFFF3B30) else accentColor,
            fontSize = 5.5.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 6.sp
        )
        Canvas(modifier = Modifier.size(width = 7.dp, height = 9.dp)) {
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
        Text(
            text = "S",
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 5.5.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 6.sp
        )
    }
}

// Lucide vibrate phone icon for Tilt HUD
@Composable
fun TiltedPhoneIcon(tint: Color, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_tilt_vibrate),
        contentDescription = "Tilt",
        tint = tint,
        modifier = modifier
            .size(15.dp)
            .offset(y = (-1).dp)
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
        shape = RoundedCornerShape(12.dp),
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
        shape = RoundedCornerShape(12.dp),
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
                        .clip(RoundedCornerShape(8.dp))
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
    var isLinkedInExpanded by remember { mutableStateOf(false) }
    var currentLinkedInCaption by remember {
        mutableStateOf(RoastsRepository.LINKEDIN_PARODY_POSTS.random())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090B10))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TiltShift* Certificate",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Both Photo & Certificate Saved to Gallery",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(20.dp))
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

                // Image Display (Certificate rendered cleanly without nested card clutter)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (showCertificateView) 0.75f else 1.33f)
                ) {
                    val displayBmp = if (showCertificateView) result.certBitmap else result.photoBitmap
                    Image(
                        bitmap = displayBmp.asImageBitmap(),
                        contentDescription = "Capture Result",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Show separate score & roast cards ONLY when looking at raw photo (certificate already contains them)
                if (!showCertificateView) {
                    Spacer(modifier = Modifier.height(14.dp))

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
                                text = "The Verdict",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Share to LinkedIn & Standard Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Dedicated Share to LinkedIn with tailored viral description & Expand/Shuffle option
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10141E))
                            .border(BorderStroke(1.dp, Color(0xFF0A66C2).copy(alpha = 0.45f)), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0xFF0A66C2)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    shareToLinkedIn(context, if (showCertificateView) result.certUri else result.photoUri, currentLinkedInCaption)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(0.dp),
                                modifier = Modifier.weight(1f).fillMaxSize()
                            ) {
                                Text(
                                    text = "in",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Share to LinkedIn", fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )

                            IconButton(
                                onClick = { isLinkedInExpanded = !isLinkedInExpanded },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLinkedInExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Expand description",
                                    tint = Color.White
                                )
                            }
                        }

                        if (isLinkedInExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "THOUGHT LEADERSHIP POST",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                BasicTextField(
                                    value = currentLinkedInCaption,
                                    onValueChange = { currentLinkedInCaption = it },
                                    textStyle = TextStyle(
                                        color = Color.White.copy(alpha = 0.95f),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0C0E14), RoundedCornerShape(8.dp))
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                        .heightIn(min = 80.dp, max = 180.dp)
                                        .verticalScroll(rememberScrollState())
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                                .height(46.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
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
                                .height(46.dp)
                        ) {
                            Text(text = "Next Target", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

fun shareToLinkedIn(context: Context, uri: Uri, caption: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("LinkedIn Post", caption)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied viral LinkedIn post to clipboard!", Toast.LENGTH_LONG).show()

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, caption)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share to LinkedIn"))
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
                            context = context,
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
                            } else {
                                onError(ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "Failed to save to gallery", null))
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onError(ImageCaptureException(ImageCapture.ERROR_UNKNOWN, e.message ?: "Unknown error", e))
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
    context: Context,
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
    val cardHeight = 1440
    val output = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val accentColorInt = AndroidColor.parseColor(
        if (mode == CameraMode.PEAK) "#FFD54F" else if (mode == CameraMode.PRO) "#00E5FF" else "#00E676"
    )

    // 1. Dark Obsidian Card Background
    val bgPaint = Paint().apply { color = AndroidColor.parseColor("#0D1117") }
    canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), bgPaint)

    // Classic Certificate Border (Single Outer Frame with corner notches)
    val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = accentColorInt
    }
    canvas.drawRect(28f, 28f, cardWidth - 28f, cardHeight - 28f, borderPaint)

    val innerLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = AndroidColor.parseColor("#21262D")
    }
    canvas.drawRect(36f, 36f, cardWidth - 36f, cardHeight - 36f, innerLinePaint)

    // Corner decorative brackets
    val cornerPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = accentColorInt
    }
    val r = 24f
    for (cx in listOf(28f, cardWidth - 28f)) {
        for (cy in listOf(28f, cardHeight - 28f)) {
            val sx = if (cx == 28f) 1f else -1f
            val sy = if (cy == 28f) 1f else -1f
            canvas.drawLine(cx, cy, cx + sx * r * 2, cy, cornerPaint)
            canvas.drawLine(cx, cy, cx, cy + sy * r * 2, cornerPaint)
        }
    }

    // 2. Header Emblem & Titles
    val tagPaint = Paint().apply {
        color = accentColorInt
        textSize = 18f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        letterSpacing = 0.14f
    }
    canvas.drawText("★ TILTSHIFT* CERTIFIED ★", cardWidth / 2f, 68f, tagPaint)

    val titlePaint = Paint().apply {
        color = AndroidColor.WHITE
        textSize = 38f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    canvas.drawText("CERTIFICATE OF CONFORMITY", cardWidth / 2f, 115f, titlePaint)

    val subTitlePaint = Paint().apply {
        color = AndroidColor.parseColor("#8B949E")
        textSize = 18f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("Official Proof of Hardware Sensor Alignment & Spatial Discipline", cardWidth / 2f, 155f, subTitlePaint)

    // 3. Embedded Photo Box with Center-Crop (Proper image showcasing without distortion!)
    val photoDest = RectF(50f, 180f, cardWidth - 50f, 800f)
    val photoFramePaint = Paint().apply {
        color = AndroidColor.parseColor("#161B22")
        style = Paint.Style.FILL
    }
    canvas.drawRect(photoDest, photoFramePaint)

    // Showcase image cleanly in its true aspect ratio without stretching
    val destAspect = photoDest.width() / photoDest.height()
    val srcAspect = photoBitmap.width.toFloat() / photoBitmap.height.toFloat()

    val srcRect = if (srcAspect > destAspect) {
        val cropWidth = (photoBitmap.height * destAspect).toInt()
        val left = ((photoBitmap.width - cropWidth) / 2).coerceAtLeast(0)
        Rect(left, 0, (left + cropWidth).coerceAtMost(photoBitmap.width), photoBitmap.height)
    } else {
        val cropHeight = (photoBitmap.width / destAspect).toInt()
        val top = ((photoBitmap.height - cropHeight) / 2).coerceAtLeast(0)
        Rect(0, top, photoBitmap.width, (top + cropHeight).coerceAtMost(photoBitmap.height))
    }

    canvas.drawBitmap(photoBitmap, srcRect, photoDest, Paint(Paint.FILTER_BITMAP_FLAG))

    val photoOutlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = AndroidColor.parseColor("#30363D")
    }
    canvas.drawRect(photoDest, photoOutlinePaint)

    // Certification Badge ("TILTSHIFT CERTIFIED - EST. 2026 -")
    // Dynamically placed on the best/brightest spot of the photo so the all-black stamp stands out
    val badgeBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.badge_certified)
    if (badgeBitmap != null) {
        val badgeSize = 145f
        val pad = 18f
        val candidateRects = listOf(
            RectF(photoDest.right - badgeSize - pad, photoDest.top + pad, photoDest.right - pad, photoDest.top + badgeSize + pad),
            RectF(photoDest.left + pad, photoDest.top + pad, photoDest.left + badgeSize + pad, photoDest.top + badgeSize + pad),
            RectF(photoDest.right - badgeSize - pad, photoDest.bottom - badgeSize - pad, photoDest.right - pad, photoDest.bottom - pad),
            RectF(photoDest.left + pad, photoDest.bottom - badgeSize - pad, photoDest.left + badgeSize + pad, photoDest.bottom - pad)
        )

        var bestRect = candidateRects[0]
        var maxLuminance = -1.0f

        for (cand in candidateRects) {
            val relLeft = ((cand.left - photoDest.left) / photoDest.width()).coerceIn(0f, 1f)
            val relTop = ((cand.top - photoDest.top) / photoDest.height()).coerceIn(0f, 1f)
            val relRight = ((cand.right - photoDest.left) / photoDest.width()).coerceIn(0f, 1f)
            val relBottom = ((cand.bottom - photoDest.top) / photoDest.height()).coerceIn(0f, 1f)

            val sLeft = (srcRect.left + relLeft * srcRect.width()).toInt().coerceIn(0, photoBitmap.width - 1)
            val sTop = (srcRect.top + relTop * srcRect.height()).toInt().coerceIn(0, photoBitmap.height - 1)
            val sRight = (srcRect.left + relRight * srcRect.width()).toInt().coerceIn(1, photoBitmap.width)
            val sBottom = (srcRect.top + relBottom * srcRect.height()).toInt().coerceIn(1, photoBitmap.height)

            var totalLum = 0.0
            var sampleCount = 0
            val stepX = ((sRight - sLeft) / 8).coerceAtLeast(1)
            val stepY = ((sBottom - sTop) / 8).coerceAtLeast(1)

            var y = sTop
            while (y < sBottom) {
                var x = sLeft
                while (x < sRight) {
                    val pixel = photoBitmap.getPixel(x, y)
                    val r = (pixel shr 16) and 0xFF
                    val g = (pixel shr 8) and 0xFF
                    val b = pixel and 0xFF
                    totalLum += (0.299 * r + 0.587 * g + 0.114 * b)
                    sampleCount++
                    x += stepX
                }
                y += stepY
            }

            val avgLum = if (sampleCount > 0) (totalLum / sampleCount).toFloat() else 0f
            if (avgLum > maxLuminance) {
                maxLuminance = avgLum
                bestRect = cand
            }
        }

        // Frosted translucent white circular backing to make the black stamp pop if background isn't pure white
        val backingAlpha = if (maxLuminance > 220f) 50 else if (maxLuminance > 160f) 140 else 220
        val backingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            alpha = backingAlpha
            style = Paint.Style.FILL
        }
        canvas.drawCircle(bestRect.centerX(), bestRect.centerY(), badgeSize * 0.48f, backingPaint)

        // Draw the black certification stamp
        canvas.drawBitmap(badgeBitmap, null, bestRect, Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG))
    }

    // Corner notches on photo
    for (px in listOf(photoDest.left, photoDest.right)) {
        for (py in listOf(photoDest.top, photoDest.bottom)) {
            val sx = if (px == photoDest.left) 1f else -1f
            val sy = if (py == photoDest.top) 1f else -1f
            canvas.drawLine(px, py, px + sx * 16f, py, cornerPaint)
            canvas.drawLine(px, py, px, py + sy * 16f, cornerPaint)
        }
    }

    // 4. NOTICEABLE SAVAGE ROAST CALLOUT (Directly under photo, clean text, no emojis)
    val roastRect = RectF(50f, 820f, cardWidth - 50f, 985f)
    val roastBgPaint = Paint().apply {
        color = AndroidColor.parseColor("#1A0E12")
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(roastRect, 16f, 16f, roastBgPaint)

    val roastBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
        color = AndroidColor.parseColor("#FF5252")
    }
    canvas.drawRoundRect(roastRect, 16f, 16f, roastBorderPaint)

    val roastTagPaint = Paint().apply {
        color = AndroidColor.parseColor("#FF8A80")
        textSize = 17f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        letterSpacing = 0.12f
    }
    canvas.drawText("The Verdict", cardWidth / 2f, 856f, roastTagPaint)

    // Multi-line bold roast text wrapped with StaticLayout, centered horizontally and vertically
    val roastTextPaint = TextPaint().apply {
        color = AndroidColor.WHITE
        textSize = 25f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }
    val wrappedRoast = "\"$roast\""
    val maxTextWidth = (roastRect.width() - 48f).toInt()
    val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder.obtain(wrappedRoast, 0, wrappedRoast.length, roastTextPaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(4f, 1.15f)
            .setIncludePad(false)
            .build()
    } else {
        @Suppress("DEPRECATION")
        StaticLayout(wrappedRoast, roastTextPaint, maxTextWidth, Layout.Alignment.ALIGN_CENTER, 1.15f, 4f, false)
    }

    canvas.save()
    // Vertically center the quote cleanly inside the remaining space below 'The Verdict'
    val availableTop = 872f
    val availableBottom = roastRect.bottom - 12f
    val textY = availableTop + ((availableBottom - availableTop - staticLayout.height) / 2f).coerceAtLeast(0f)
    canvas.translate(cardWidth / 2f, textY)
    staticLayout.draw(canvas)
    canvas.restore()

    // 5. Single Unified Sensor Breakdown Bar (No nested boxes)
    val sRect = RectF(60f, 1005f, cardWidth - 60f, 1260f)
    val sBgPaint = Paint().apply {
        color = AndroidColor.parseColor("#161B22")
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(sRect, 16f, 16f, sBgPaint)

    val sBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = AndroidColor.parseColor("#30363D")
    }
    canvas.drawRoundRect(sRect, 16f, 16f, sBorderPaint)

    // 4 Metric Columns cleanly divided
    val colW = sRect.width() / 4f
    val colDivPaint = Paint().apply {
        color = AndroidColor.parseColor("#21262D")
        strokeWidth = 2f
    }
    for (i in 1..3) {
        val lx = sRect.left + i * colW
        canvas.drawLine(lx, sRect.top + 20f, lx, sRect.bottom - 20f, colDivPaint)
    }

    val isZoomOk = abs(actualZoom - target.targetZoom) <= 0.15f
    val isTiltOk = mode != CameraMode.NORMAL && abs(actualPitch - target.targetPitch) <= 5.0f
    val isHeadingOk = mode == CameraMode.PEAK && abs((actualCompass - target.targetCompass + 540) % 360 - 180) <= 5.5f

    data class CertCol(val title: String, val value: String, val sub: String, val colorInt: Int, val isOk: Boolean)
    val columns = listOf(
        CertCol("ACCURACY", "${String.format(Locale.US, "%.1f", accuracy)}%", "GRADE: $grade", AndroidColor.parseColor("#00E676"), true),
        CertCol("ZOOM", "${String.format(Locale.US, "%.1f", actualZoom)}x", "TARGET: ${String.format(Locale.US, "%.1f", target.targetZoom)}x", if (isZoomOk) AndroidColor.parseColor("#00E676") else AndroidColor.parseColor("#FFB300"), isZoomOk),
        CertCol("TILT", "${actualPitch.roundToInt()}°", "TARGET: ${target.targetPitch.roundToInt()}°", if (isTiltOk) AndroidColor.parseColor("#00E676") else AndroidColor.parseColor("#FF5252"), isTiltOk),
        CertCol("COMPASS", "${actualCompass.roundToInt()}°", "TARGET: ${target.targetCompass.roundToInt()}°", if (isHeadingOk) AndroidColor.parseColor("#00E676") else AndroidColor.parseColor(if (mode == CameraMode.PEAK) "#FF5252" else "#8B949E"), isHeadingOk)
    )

    for (i in columns.indices) {
        val col = columns[i]
        val cx = sRect.left + i * colW + colW / 2f

        val cTitlePaint = Paint().apply {
            color = AndroidColor.parseColor("#8B949E")
            textSize = 16f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(col.title, cx, 1050f, cTitlePaint)

        val cValPaint = Paint().apply {
            color = col.colorInt
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(col.value, cx, 1125f, cValPaint)

        val cSubPaint = Paint().apply {
            color = AndroidColor.WHITE
            textSize = 15f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(col.sub, cx, 1180f, cSubPaint)

        val cStatusPaint = Paint().apply {
            color = col.colorInt
            textSize = 13f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(if (col.isOk) "LOCKED" else "OFF TARGET", cx, 1220f, cStatusPaint)
    }

    // 6. Footer & Signatures
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(System.currentTimeMillis())
    val footerPaint = Paint().apply {
        color = AndroidColor.parseColor("#8B949E")
        textSize = 15f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("MODE: ${mode.displayName.uppercase()}  |  TIMESTAMP: $dateStr", cardWidth / 2f, 1315f, footerPaint)

    val certSubFooter = Paint().apply {
        color = AndroidColor.parseColor("#484F58")
        textSize = 14f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("Cryptographically Signed by TiltShift* In-Device IMU Hardware Sensors", cardWidth / 2f, 1345f, certSubFooter)

    val stampPaint = Paint().apply {
        color = accentColorInt
        textSize = 14f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        letterSpacing = 0.1f
    }
    canvas.drawText("OFFICIAL SHOT ARCHIVE • DO NOT TAMPER", cardWidth / 2f, 1375f, stampPaint)

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
                shape = RoundedCornerShape(12.dp),
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
                shape = RoundedCornerShape(12.dp),
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

// -------------------------------------------------------------
// USELESS PEAKTURES IN-APP GALLERY
// -------------------------------------------------------------

data class UselessPeakture(
    val id: Long,
    val uri: Uri,
    val name: String,
    val isCertificate: Boolean,
    val dateAdded: Long
)

fun loadUselessPeaktures(context: Context): List<UselessPeakture> {
    val list = mutableListOf<UselessPeakture>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
    )
    val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
    val selectionArgs = arrayOf("TiltShift_%")
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    try {
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol) ?: ""
                val dateAdded = cursor.getLong(dateCol)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val isCert = name.contains("CERT", ignoreCase = true)
                list.add(UselessPeakture(id, contentUri, name, isCert, dateAdded))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

@Composable
fun UselessPeakturesGallery(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var peaktures by remember { mutableStateOf<List<UselessPeakture>>(emptyList()) }
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Certificates, 2: Raw Photos
    var viewingPeakture by remember { mutableStateOf<UselessPeakture?>(null) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        peaktures = withContext(Dispatchers.IO) {
            loadUselessPeaktures(context)
        }
    }

    val filteredList = remember(peaktures, selectedFilter) {
        when (selectedFilter) {
            1 -> peaktures.filter { it.isCertificate }
            2 -> peaktures.filter { !it.isCertificate }
            else -> peaktures
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090B10))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Useless Peaktures",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                        }
                        Text(
                            text = "Certified Accidental Art & Tilted Masterpieces",
                            color = Color.White.copy(alpha = 0.50f),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.10f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Filter Pill Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val certCount = peaktures.count { it.isCertificate }
                    val photoCount = peaktures.count { !it.isCertificate }
                    val tabs = listOf("All (${peaktures.size})", "Certificates ($certCount)", "Raw Photos ($photoCount)")

                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedFilter == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.clickable { selectedFilter = index }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gallery Grid
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(text = "📸", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Useless Peaktures yet!",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Go tilt your phone in Chad Mode and take some certified crooked shots.",
                                color = Color.White.copy(alpha = 0.50f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList, key = { it.id }) { item ->
                            PeaktureThumbnailCard(
                                item = item,
                                onClick = { viewingPeakture = item }
                            )
                        }
                    }
                }
            }

            // Fullscreen Peakture Viewer Modal
            viewingPeakture?.let { peakture ->
                PeaktureDetailViewer(
                    peakture = peakture,
                    onDismiss = { viewingPeakture = null },
                    onDelete = {
                        try {
                            context.contentResolver.delete(peakture.uri, null, null)
                            Toast.makeText(context, "Peakture deleted from gallery", Toast.LENGTH_SHORT).show()
                            refreshTrigger++
                            viewingPeakture = null
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not delete: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PeaktureThumbnailCard(
    item: UselessPeakture,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, item.uri) {
        value = withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(item.uri, Size(400, 400), null)
                } else {
                    context.contentResolver.openInputStream(item.uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF141722),
        border = BorderStroke(1.dp, if (item.isCertificate) Color(0xFF00E5FF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if (item.isCertificate) 0.75f else 1.0f)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            // Top Badge
            Surface(
                shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 6.dp),
                color = if (item.isCertificate) Color(0xFF00E5FF).copy(alpha = 0.90f) else Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = if (item.isCertificate) "CERTIFICATE" else "RAW PHOTO",
                    color = if (item.isCertificate) Color.Black else Color.White,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun PeaktureDetailViewer(
    peakture: UselessPeakture,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val fullBitmap by produceState<Bitmap?>(initialValue = null, peakture.uri) {
        value = withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(peakture.uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Main Photo/Certificate Image
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (fullBitmap != null) {
                    Image(
                        bitmap = fullBitmap!!.asImageBitmap(),
                        contentDescription = peakture.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // Top Header Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Text(
                    text = if (peakture.isCertificate) "Certificate of Conformity" else "Raw Useless Peakture",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.Red.copy(alpha = 0.35f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom Action Bar: Share & LinkedIn
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val caption = RoastsRepository.LINKEDIN_PARODY_POSTS.random()
                        shareToLinkedIn(context, peakture.uri, caption)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A66C2),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                ) {
                    Text(text = "in", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "LinkedIn Post", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { shareImage(context, peakture.uri) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.20f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

