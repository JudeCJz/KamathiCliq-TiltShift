# TiltShift* (SimpleCamera Module)

Native Android camera implementation for TiltShift*, built with Jetpack Compose, CameraX, and Google ML Kit Face Detection.

---

## Capabilities

- **CameraX Pipeline**: Implements high-framerate camera preview (`PreviewView`), non-blocking hardware image capture (`ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY`), and real-time vision processing (`ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST`).
- **Sensor Fusion Engine**: Uses low-latency `Sensor.TYPE_ROTATION_VECTOR` matrix transformations to derive continuous physical pitch angle and geomagnetic compass azimuth.
- **Biometric & Distance Estimation**: Integrated with Google ML Kit Face Detection to estimate subject distance based on facial frame ratio and determine bilateral eye closure status.
- **Dynamic Lock Verification**: Shutter state evaluates mode-dependent constraints across Zoom, Gyroscope Pitch, Heading Azimuth, Distance Target, and Facial Biometrics.
- **Certificate Synthesis**: Generates 1080x1600 verification certificates using Android Canvas directly on a background thread and persists both the photo and certificate to `MediaStore.Images.Media`.
- **System Intent Integration**: Declares `android.media.action.STILL_IMAGE_CAMERA` with `showWhenLocked="true"` to serve as a hardware camera intent handler.

---

## Build Commands

### Compiling Debug APK
Run from this directory:
```powershell
.\gradlew.bat assembleDebug
```
Output APK path:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Direct Device Deployment
```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.simplecamera/.MainActivity
```

### Logcat Telemetry Monitoring
```powershell
adb logcat -s "CameraScreen" "PeakPlusEvaluator"
```
