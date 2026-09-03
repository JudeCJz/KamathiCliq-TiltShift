@echo off
setlocal
echo ===================================================
echo        TILTSHIFT* ANDROID APP - DEPLOY & RUN
echo ===================================================
echo.

set "PROJECT_DIR=%~dp0SimpleCamera"
set "APK_PATH=%PROJECT_DIR%\app\build\outputs\apk\debug\app-debug.apk"

if not exist "%APK_PATH%" (
    echo [1/3] Building APK...
    cd /d "%PROJECT_DIR%"
    call gradlew.bat assembleDebug
    if errorlevel 1 (
        echo Error: Build failed.
        pause
        exit /b 1
    )
) else (
    echo [1/3] Found existing build: %APK_PATH%
)

echo.
echo [2/3] Checking connected Android devices...
adb devices
echo.

echo [3/3] Installing APK to device...
adb install -r "%APK_PATH%"
if errorlevel 1 (
    echo.
    echo Could not install to device.
    echo Please make sure your Android phone is connected via USB with "USB Debugging" enabled!
    echo.
    pause
    exit /b 1
)

echo.
echo Starting Camera App on device...
adb shell am start -n com.example.simplecamera/.MainActivity
echo.
echo Done! Enjoy taking photos!
pause
