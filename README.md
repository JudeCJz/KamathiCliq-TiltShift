# TiltShift*

TiltShift* is an Android camera application built natively with Jetpack Compose and CameraX. It replaces traditional point-and-shoot camera behavior with hardware-enforced spatial and biometric puzzles. The shutter button remains locked until the user satisfies real-time physical constraints: digital zoom, gyroscope tilt, geomagnetic compass heading, subject distance, and facial eye state.

Every successful capture produces both the original photo and an official Certificate of Photographic Conformity documenting telemetry accuracy, score grade, and roast verdict directly to the device gallery.

---

## Core Modes

TiltShift* features four distinct camera puzzle modes available from the top-right menu:

### 1. Normal Mode
- **Constraint**: Target Zoom ratio (1.5x to 3.5x).
- **Evaluation**: The user must dial the zoom slider to match the assigned magnification within a tolerance of ±0.15x.

### 2. Pro Mode
- **Constraints**: Target Zoom ratio + Gyroscope Tilt angle.
- **Evaluation**: The phone must be tilted to a specific pitch angle (15°, 25°, 35°, 45°, or 60° ±5.0°) while holding the assigned zoom level.

### 3. Peak Mode
- **Constraints**: Target Zoom ratio + Gyroscope Tilt angle + Compass heading.
- **Evaluation**: The phone must simultaneously match the pitch angle, zoom ratio, and rotate to face the exact cardinal compass heading (e.g., North, East, South-West ±5.5°).

### 4. Peak+ Mode (Multi-Constraint Vision Challenge)
- **Constraints**: All 4 modes combined:
  - Target Zoom ratio (±0.15x)
  - Gyroscope Tilt angle (±5.0°)
  - Compass heading (±5.5°)
  - Arm Stretch / Subject Distance (randomized target distance)
    - Front Camera (Selfie): Randomized between 30 cm and 80 cm
    - Back Camera: Randomized between 40 cm and 150 cm
  - Eyes Closed: Google ML Kit Face Detection requires both eyes to be closed (eye-open probability <= 0.30).
- **Evaluation**: The shutter unlocks only when all five physical and biometric requirements are concurrently satisfied.

---

## Difficulties

- **Chad Mode (Default)**: Blind operation. All on-screen visual level guides and direction indicators are hidden. The user must orient the phone by feel and live telemetry feedback.
- **Baby Mode**: Assistive operation. Renders a translucent (50% opacity) overlay featuring Lucide directional chevrons, live degree delta counters, and spirit level alignment helpers.

---

## Key Features

- **Vector Interface**: Clean, consistent user interface utilizing Lucide vector icons and Material UI elements. Zero emojis.
- **Non-Overlapping HUD Layout**: Unified top status architecture cleanly stacking the multi-sensor puzzle HUD and Peak+ challenge banner across all screen densities.
- **Live Roast Engine**: Real-time evaluation banner that analyzes current sensor discrepancies and delivers contextual guidance and critiques based on physical offsets.
- **Dual-File Verification System**: On capture, the app writes two files to the standard MediaStore gallery:
  1. The raw captured photograph.
  2. An official 1080x1600 Certificate of Photographic Conformity containing full telemetry breakdown, accuracy percentage, performance grade (S+, S, A, B), and audit verdict.
- **In-App Peaktures Gallery**: Integrated gallery browser displaying certified captures with grade badges and direct share functionality.
- **System Camera Provider Integration**: Implements `android.media.action.STILL_IMAGE_CAMERA` with `showWhenLocked="true"` to intercept lockscreen shortcuts and hardware power-button double-press gestures.

---

## Technical Specifications

- **Language**: Kotlin 1.9
- **Minimum SDK**: Android 8.0 (API Level 26)
- **Target SDK**: Android 14 (API Level 34)
- **UI Framework**: Jetpack Compose with Material 3
- **Camera Subsystem**: Android CameraX (Core, Camera2, Lifecycle, View, ImageCapture, ImageAnalysis)
- **Computer Vision**: Google ML Kit Face Detection (`play-services-mlkit-face-detection`)
- **Sensor Fusion**: Android Sensor Framework (`Sensor.TYPE_ROTATION_VECTOR`, `SensorManager.getOrientation`)
- **Graphics Pipeline**: Hardware-accelerated Android Canvas rendering for dynamic certificates
- **Storage Subsystem**: Android MediaStore ContentResolver (`Pictures/TiltShift`)

---

## Installation and Build

### Pre-built APK Installation
Connect your Android device via USB with USB Debugging enabled, then run:
```bash
adb install -r apk/TiltShift.apk
adb shell am start -n com.example.simplecamera/.MainActivity
```

### Building from Source
Ensure Android SDK with platform-tools and build-tools (API 34) is installed.
```bash
cd SimpleCamera
.\gradlew.bat assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.simplecamera/.MainActivity
```

---

## Architecture Overview

```
SimpleCamera/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml              # Permissions, camera features, intent filters
│   │   ├── java/com/example/simplecamera/
│   │   │   ├── MainActivity.kt              # App lifecycle, screen orientation, lockscreen behavior
│   │   │   ├── data/
│   │   │   │   ├── PeakPlusChallenge.kt     # Distance estimation, eye-state evaluation, ML Kit models
│   │   │   │   └── Roasts.kt                # Sensor roast generator and telemetry text
│   │   │   └── ui/camera/
│   │   │       └── CameraScreen.kt          # CameraX pipeline, HUD, sensor listener, certificate generator
│   │   └── res/
│   │       ├── drawable/                    # Lucide vector drawables (ruler, eye-closed, compass, etc.)
│   │       └── mipmap-*/                    # Edge-to-edge camera doodle application icon
└── gradle/
    └── libs.versions.toml                   # Version catalog
```
