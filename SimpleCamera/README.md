# tiltshift* 📸

A clean, modern native Android Camera application built with **Jetpack Compose** and **CameraX**, specifically configured to run, build, and debug directly inside **Antigravity IDE**.

---

## ✨ Features
- **Full-Screen Camera Preview**: Smooth, responsive CameraX preview with auto-rotation support.
- **Dedicated Shutter Button**: Minimalist circular shutter button with press animations and a flash screen effect on capture.
- **Direct Gallery / Photos Integration**: Pictures are saved directly to the device's standard Photos album (`Pictures/SimpleCamera`) using modern Android `MediaStore` APIs.
- **Lens Switcher**: Quick toggle button to switch between the back camera and front (selfie) camera.
- **Runtime Permissions**: Friendly permission handling that prompts the user gracefully if camera permission is needed.

---

## 🛠 Project Structure
```
SimpleCamera/
├── .vscode/
│   └── tasks.json               # One-click Antigravity IDE build & deploy tasks
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml   # Camera permissions & hardware feature flags
│   │   └── java/com/example/simplecamera/
│   │       ├── MainActivity.kt   # App entry point
│   │       └── ui/camera/
│   │           └── CameraScreen.kt # CameraX preview, shutter & MediaStore save logic
│   └── build.gradle.kts         # App dependencies (CameraX, Compose, Lifecycle)
├── gradle/
│   └── libs.versions.toml       # Modern Gradle version catalog
├── local.properties             # Configured Android SDK location
└── gradlew.bat                  # Gradle wrapper script
```

---

## 🚀 How to Build and Run in Antigravity IDE

### Option 1: Using Antigravity IDE Tasks (Recommended)
1. Open the Command Palette in Antigravity IDE (`Ctrl + Shift + P`).
2. Type **`Tasks: Run Task`** (or press `Ctrl + Shift + B` for default build).
3. Choose one of the pre-configured tasks:
   - **`Android: Build Debug APK`**: Compiles the APK using the local Gradle wrapper.
   - **`Android: Install on Connected Phone`**: Pushes and installs the APK to your USB-connected Android phone.
   - **`Android: Launch Camera App on Phone`**: Installs and launches the app directly on your screen.
   - **`Android: View Logcat`**: Streams app logs directly in the IDE terminal.

---

### Option 2: Using the Terminal

#### 1. Build the APK:
```powershell
.\gradlew.bat assembleDebug
```
The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### 2. Install on your Android Phone:
Connect your phone via USB (with **USB Debugging** enabled), then run:
```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### 3. Launch the App:
```powershell
adb shell am start -n com.example.simplecamera/.MainActivity
```
