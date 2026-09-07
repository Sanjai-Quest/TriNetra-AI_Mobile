@echo off
rem ------------------------------------------------------------
rem TriNetra Android CLI deployment script (USB device)
rem ------------------------------------------------------------

rem Ensure we are in the script's directory
cd /d "%~dp0"
cd ..

rem ------------------------------------------------------------
rem 1. Verify ADB device is connected
rem ------------------------------------------------------------
adb get-state >nul 2>&1
if errorlevel 1 (
    echo [WARN] ADB device not detected, but proceeding anyway.
) else (
    echo [INFO] Device detected.
)

rem ------------------------------------------------------------
rem 2. Build the debug APK using Gradle Wrapper or local Gradle
rem ------------------------------------------------------------
if exist "gradlew.bat" (
    echo [INFO] Using Gradle Wrapper to build APK
    call gradlew.bat clean assembleDebug
) else (
    echo [WARN] Gradle wrapper not found, falling back to system Gradle
    gradle clean assembleDebug
)
if errorlevel 1 (
    echo [ERROR] Gradle build failed.
    exit /b 1
)

rem ------------------------------------------------------------
rem 3. Install the APK on the connected device
rem ------------------------------------------------------------
set APK_PATH=android\app\build\outputs\apk\debug\app-debug.apk
if not exist "%APK_PATH%" (
    echo [ERROR] APK not found at %APK_PATH%.
    exit /b 1
)

echo [INFO] Installing APK ...
adb install -r "%APK_PATH%"
if errorlevel 1 (
    echo [ERROR] adb install failed.
    exit /b 1
)

rem ------------------------------------------------------------
rem 4. Launch the main activity
rem ------------------------------------------------------------
adb shell am start -n com.trinetra.ai/.MainActivity
if errorlevel 1 (
    echo [ERROR] Failed to launch the app.
    exit /b 1
)

rem ------------------------------------------------------------
rem 5. Stream Logcat filtered for TelemetryUploadWorker
rem ------------------------------------------------------------
echo [INFO] Streaming Logcat (press Ctrl+C to stop)...
adb logcat | findstr /I "TelemetryUploadWorker"
