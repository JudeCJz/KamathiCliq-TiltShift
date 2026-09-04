<img width="1280" height="640" alt="TiltShift Banner" src="https://github.com/user-attachments/assets/8920b256-2ba8-4988-b824-5351134eb4bd" />

# TiltShift* 🎯

> **"A camera app that aggressively refuses to take photos until you hold your phone at an absurdly specific mathematical angle, compass heading, and zoom multiplier — while savagely roasting your unsteady posture."**

---

## 📌 Basic Details
### Team Name: TinkerLess

### Team Members
- **Team Lead**: JudeCJz - TinkerHub

---

## 📸 Project Showcase & Screenshots

### 1. Official App Icon
<p align="center">
  <img src="docs/screenshots/app_icon.png" width="260" alt="TiltShift App Icon" />
  <br />
  <em>Hand-drawn camera doodle application icon featuring the upside-down stickman photographer</em>
</p>

---

### 2. Launch Calibration Screen
<p align="center">
  <img src="docs/screenshots/loading_screen.jpeg" width="300" alt="TiltShift Launch Screen" />
  <br />
  <em>Custom animated stickman loading sequence with dynamic crayon loading bar sprites and comedic calibration status</em>
</p>

---

### 3. Live Camera Viewfinder & Savage Roast Engine
<p align="center">
  <img src="docs/screenshots/interface.jpeg" width="300" alt="TiltShift Live Interface" />
  <br />
  <em>Real-time sensor HUD tracking Zoom, Tilt, and Compass alignment. Shutter button remains locked with active high-contrast roast critiques</em>
</p>

---

### 4. Official Sensor Audit Certificate of Conformity
<p align="center">
  <img src="docs/screenshots/example_certificate.jpeg" width="360" alt="Certificate of Conformity" />
  <br />
  <em>Official stamped certificate minted directly to gallery on capture, complete with accuracy score, grade, telemetry breakdown, and roast verdict</em>
</p>

---

### 5. Official TiltShift* Certification Stamp
<p align="center">
  <img src="docs/screenshots/app_seal.png" width="240" alt="TiltShift Certified Stamp Seal" />
  <br />
  <em>Official cryptographic verification seal embedded onto certified capture documents</em>
</p>

---

## ❓ The Problem (that doesn't exist)
Modern smartphone cameras have made photography far too effortless. Point, tap, shoot. Anyone can do it. Society has completely lost respect for the sacred geometry of gyroscope pitch, geomagnetic azimuth, and optical zoom ratios. Casual photographers capture memories with crooked horizons, trembling hands, and zero regard for spatial trigonometry.

---

## 💡 The Solution (that nobody asked for)
**TiltShift\*** brings back the discipline. The shutter button stays padlocked until you satisfy the alignment constraints:
1. **Tilt your phone** to the exact mathematical degree requested (e.g. `45° ±5°`).
2. **Face the required compass heading** (e.g. `270° West ±5.5°`).
3. **Dial in the exact zoom multiplier** (e.g. `3.0x ±0.15x`).

While you struggle to balance your phone, a live high-contrast roast engine actively critiques your posture (*"Off by 53°! Are you trying to photograph the ceiling or floor?!"*).

---

## 🎮 Game Modes & Difficulties

### 🕹️ Camera Modes (Top Right Dropdown Menu)
- **Normal Mode** (Emerald): Dial into the exact random Zoom magnification (e.g. `2.0x ±0.15x`).
- **Pro Mode** (Cyber Cyan): Requires both Gyroscope Tilt/Pitch Angle alignment (±5°) **AND** Zoom matching.
- **Peak Mode** (Golden Amber): The ultimate challenge — requires **Tilt Angle** (±5°) + **Compass Heading** (±5.5°) + **Zoom Level** simultaneously!

### 😈 Difficulties (Top Left Dropdown Menu)
- **Chad Mode (Default)**: Visual spirit level is hidden. You must balance the phone blindly by feel and live telemetry messages.
- **Baby Mode**: Translucent (50% opacity) on-screen guidance HUD with Lucide directional chevrons, live degree delta counters, and an interactive 2D gimbal reticle.

### 🔘 Control Layout
- **Left (58dp)**: In-App Useless Peaktures Gallery browser.
- **Center**: Tactile Puzzle Shutter button with dynamic lock/unlock state feedback.
- **Right (58dp)**: Front/Back Camera Switch button with smooth animated rotation.

---

## 🛠️ Technical Details

### Technologies / Components Used
- **Language**: Kotlin 1.9 (100% Native Android)
- **UI Framework**: Jetpack Compose + Material 3 (Dark glassmorphism, tactile animations, dynamic badges)
- **Camera Core**: Android CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)
- **Hardware Sensors**: Android Sensor Framework with fused `Sensor.TYPE_ROTATION_VECTOR` and `SensorManager.getOrientation` for low-latency live pitch and azimuth
- **Graphics & Certificate Engine**: Hardware-accelerated Android `Canvas`, `Bitmap`, and `Paint` generating high-res 1080x1600 verification certificates with embedded photo previews and audit breakdowns
- **Media Pipeline**: Android `MediaStore` ContentResolver integration saving directly to `Pictures/TiltShift`
- **System Integration**: Implements `android.media.action.STILL_IMAGE_CAMERA` and `STILL_IMAGE_CAMERA_SECURE` with `showWhenLocked="true"` to intercept lockscreen shortcuts and hardware power-button double-press gestures.

---

## 📥 Installation & Build

### Option 1: Direct APK Install (Plug & Play)
Download and install the pre-compiled APK directly:
```bash
adb install -r apk/TiltShift.apk
adb shell am start -n com.example.simplecamera/.MainActivity
```

### Option 2: Build from Source with Gradle Wrapper
```bash
# 1. Clone the repository
git clone https://github.com/JudeCJz/TinkerLess.git
cd TinkerLess/SimpleCamera

# 2. Build debug APK
.\gradlew.bat assembleDebug

# 3. Install to connected Android device
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.simplecamera/.MainActivity
```

---

## 👥 Team Contributions
- **JudeCJz**: Ideation, architecture, CameraX integration, sensor fusion mathematics, Jetpack Compose UI design, certificate generator canvas, artwork design, and savage roast copywriting.

---

Made with ❤️ at TinkerHub Useless Projects 

![Static Badge](https://img.shields.io/badge/TinkerHub-24?color=%23000000&link=https%3A%2F%2Fwww.tinkerhub.org%2F)
![Static Badge](https://img.shields.io/badge/UselessProjects--26-26?link=https%3A%2F%2Ftinkerhub.org%2Fevents%2F1M8ORET9A1%2Fuseless-projects-3.0)
