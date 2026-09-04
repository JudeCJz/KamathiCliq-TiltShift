# TiltShift* 🎯 - Native Android Implementation

> **"A camera app that aggressively refuses to take photos until you hold your phone at an absurdly specific mathematical angle, compass heading, and zoom multiplier — while savagely roasting your unsteady posture."**

---

## 📸 Screenshots Showcase

### 1. App Icon & Seal
| App Icon | Certification Stamp |
| :---: | :---: |
| <img src="../docs/screenshots/app_icon.png" width="200" alt="App Icon" /> | <img src="../docs/screenshots/app_seal.png" width="200" alt="App Seal" /> |

### 2. Launch Screen & Live Viewfinder
| Loading Calibration | Live Camera & Roast Engine |
| :---: | :---: |
| <img src="../docs/screenshots/loading_screen.jpeg" width="260" alt="Launch Screen" /> | <img src="../docs/screenshots/interface.jpeg" width="260" alt="Viewfinder" /> |

### 3. Official Sensor Audit Certificate & Gallery Showcase
| Certificate of Conformity | Useless Peaktures Gallery |
| :---: | :---: |
| <img src="../docs/screenshots/example_certificate.jpeg" width="280" alt="Sensor Audit Certificate" /> | <img src="../docs/screenshots/gallery_showcase.jpeg" width="280" alt="Gallery Showcase" /> |

### 4. 👑 Royal LinkedIn Thought Leadership Parody Engine
<p align="center">
  <img src="../docs/screenshots/linkedin_integration.png" width="320" alt="LinkedIn Parody Engine" />
  <br />
  <em>Automatic satirical thought leadership post generation upon successful geometric alignment.</em>
</p>

---

## 🏗️ Architecture & Module Structure

```
SimpleCamera/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml              # Permissions, hardware features, secure camera intents
│   │   ├── java/com/example/simplecamera/
│   │   │   ├── MainActivity.kt              # App lifecycle, screen orientation, lockscreen behavior
│   │   │   ├── Navigation.kt                # Jetpack Compose screen navigation
│   │   │   ├── data/
│   │   │   │   ├── DataRepository.kt        # MediaStore persistence & Peaktures query
│   │   │   │   └── Roasts.kt                # Sensor roast generator & telemetry text
│   │   │   ├── theme/                       # Obsidian dark theme, typography, color palettes
│   │   │   └── ui/
│   │   │       ├── camera/
│   │   │       │   └── CameraScreen.kt      # CameraX pipeline, sensor fusion listener, certificate canvas
│   │   │       └── main/
│   │   │           └── MainScreen.kt        # Loading screen animation with crayon sprites
│   │   └── res/
│   │       ├── drawable/                    # Lucide vector drawables & app logos
│   │       └── mipmap-*/                    # High-res adaptive launcher icons
└── gradle/
    └── libs.versions.toml                   # Version catalog
```

---

## 📦 Project Dependencies & Version Catalog

| Component | Library Coordinate | Version / Source |
| :--- | :--- | :--- |
| **Android Gradle Plugin** | `com.android.application` | `9.0.1` |
| **Kotlin Compiler** | `org.jetbrains.kotlin.android` | `2.3.20` |
| **Compose Compiler** | `org.jetbrains.kotlin.plugin.compose` | `2.3.20` |
| **Compose BOM** | `androidx.compose:compose-bom` | `2026.03.01` |
| **Material 3** | `androidx.compose.material3:material3` | BOM Managed |
| **Extended Icons** | `androidx.compose.material:material-icons-extended` | BOM Managed |
| **CameraX Core** | `androidx.camera:camera-core` | `1.4.1` |
| **CameraX Camera2** | `androidx.camera:camera-camera2` | `1.4.1` |
| **CameraX Lifecycle** | `androidx.camera:camera-lifecycle` | `1.4.1` |
| **CameraX View** | `androidx.camera:camera-view` | `1.4.1` |
| **CameraX Extensions** | `androidx.camera:camera-extensions` | `1.4.1` |
| **Navigation 3** | `androidx.navigation3:navigation3-ui` | `1.0.1` |
| **Lifecycle Viewmodel** | `androidx.lifecycle:lifecycle-viewmodel-compose` | `2.10.0` |
| **Core KTX** | `androidx.core:core-ktx` | `1.18.0` |
| **Activity Compose** | `androidx.activity:activity-compose` | `1.13.0` |

---

## 🚀 Building & Running

### Requirements
- Android SDK 34 (Android 14)
- JDK 17+
- Connected Android Device with USB Debugging enabled

### Build Commands
```bash
# Build Debug APK
.\gradlew.bat assembleDebug

# Install and Launch on Connected Device
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.simplecamera/.MainActivity
```
