# Catalan Word of the Day Widget

An Android home screen widget that displays a daily Catalan vocabulary word with tap-to-reveal English translations.

## Features

- **Daily vocabulary**: Shows a different Catalan word each day (365 words total)
- **Tap to reveal**: Shows Catalan first, tap to reveal English translation
- **Example sentences**: Each word includes a contextual example sentence
- **Clean design**: Matches the Cozyla family calendar aesthetic

## Building the APK

### Option 1: Android Studio (Recommended)

1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to this `CatalanWordWidget` folder
4. Wait for Gradle sync to complete
5. Build → Build Bundle(s) / APK(s) → Build APK(s)
6. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

### Option 2: Command Line

Requires Gradle 8.2+ installed:

```bash
# Generate gradle wrapper (first time only)
gradle wrapper --gradle-version 8.2

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

## Installation on Cozyla Tablet

1. Copy the APK to the tablet (USB, cloud, or ADB)
2. Enable "Install from unknown sources" in Settings
3. Open the APK file to install
4. Long-press on the home screen → Widgets
5. Find "Paraula del Dia" and drag to home screen
6. Resize as desired

## Widget Usage

- **Tap** the content area to toggle English translations
- **Tap ↻** to refresh (resets to Catalan-only view)
- Word changes automatically at midnight

## Technical Details

- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Language**: Kotlin
- **Dependencies**: AndroidX Core, Gson
