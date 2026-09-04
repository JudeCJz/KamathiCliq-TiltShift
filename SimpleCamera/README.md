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
