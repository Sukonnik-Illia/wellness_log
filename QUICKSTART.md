# Quick Start Guide

Get the Wellness Log app running on your device in 5 minutes!

## Prerequisites

Before you begin, ensure you have:
- [ ] Android Studio Hedgehog (2023.1.1) or later installed
- [ ] JDK 17 or higher
- [ ] Android SDK with API level 34
- [ ] An Android device or emulator (API 24+)

## Step 1: Clone the Repository

```bash
git clone https://github.com/Sukonnik-Illia/wellness_log.git
cd wellness_log
```

## Step 2: Open in Android Studio

1. Launch Android Studio
2. Click **File → Open**
3. Navigate to the cloned `wellness_log` directory
4. Click **OK**
5. Wait for Gradle sync to complete (this may take a few minutes on first run)

## Step 3: Set Up an Android Device

### Option A: Use a Physical Device

1. Enable Developer Options on your Android device:
   - Go to **Settings → About Phone**
   - Tap **Build Number** 7 times
   - Go back to **Settings → Developer Options**
   - Enable **USB Debugging**

2. Connect your device via USB

3. Accept the "Allow USB debugging" prompt on your device

### Option B: Use an Emulator

1. In Android Studio, click **Tools → Device Manager**
2. Click **Create Device**
3. Select a device (e.g., Pixel 6)
4. Select a system image (API 34 recommended)
5. Click **Finish**
6. Start the emulator

## Step 4: Run the App

1. Wait for Gradle sync to complete
2. Click the **Run** button (green triangle) in the toolbar
3. Select your device/emulator from the list
4. Click **OK**

The app will build and install on your device!

## Step 5: Try It Out

Once the app launches:

### Test Main Screen
1. ✅ Tap the date card to open the date picker
2. ✅ Select a past date
3. ✅ Check the "Gym Visit" checkbox
4. ✅ Check the "Calorie Deficit" checkbox
5. ✅ Use +/- buttons to adjust your weight
6. ✅ Tap **SAVE**

### Test Weight Graph
1. ✅ Tap "Weight Graph" in the top bar
2. ✅ Adjust the date range
3. ✅ View your weight progression

### Test Gym Contributions
1. ✅ Go back to main screen
2. ✅ Tap "Gym Stats" in the top bar
3. ✅ View your gym visit contributions grid

## Common Issues

### Gradle Sync Failed

**Problem**: Build fails with dependency resolution errors

**Solution**:
```bash
# Clear Gradle cache
./gradlew clean
./gradlew --stop

# In Android Studio: File → Invalidate Caches → Invalidate and Restart
```

### Device Not Detected

**Problem**: Android Studio doesn't see your device

**Solution**:
1. Check USB cable connection
2. Try a different USB port
3. Re-enable USB debugging on device
4. Install device drivers (Windows only)
5. Run `adb devices` in terminal to verify connection

### App Crashes on Launch

**Problem**: App crashes immediately after opening

**Solution**:
1. Check logcat for error messages
2. Ensure minimum API level is 24
3. Verify all dependencies downloaded correctly
4. Try cleaning and rebuilding:
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

### Emulator Won't Start

**Problem**: Emulator fails to boot or is very slow

**Solution**:
1. Enable hardware acceleration (Intel HAXM or AMD Hypervisor)
2. Allocate more RAM to emulator (Tools → Device Manager → Edit Device)
3. Try a different API level
4. Use a smaller screen size device

## Next Steps

### Add Sample Data

To fully test the app, add entries for multiple days:

1. Go to main screen
2. Select yesterday's date
3. Add gym visit and weight
4. Save
5. Repeat for several days in the past
6. View weight graph and contributions

### Explore the Code

Key files to review:
- `MainActivity.kt` - App entry point and navigation
- `MainScreen.kt` - Main logging interface
- `WeightGraphScreen.kt` - Weight visualization
- `GymContributionsScreen.kt` - Contribution grid
- `WellnessRepository.kt` - Data persistence

### Future Development

Check out these guides:
- [DEVELOPMENT.md](DEVELOPMENT.md) - Comprehensive development guide
- [GOOGLE_SHEETS_INTEGRATION.md](GOOGLE_SHEETS_INTEGRATION.md) - How to add cloud sync
- [SCREEN_MOCKUPS.md](SCREEN_MOCKUPS.md) - UI/UX specifications

## Build Variants

The project includes two build variants:

### Debug Build (Default)
- Includes debugging information
- Uses debug signing key
- Fast build times
- For development only

```bash
./gradlew assembleDebug
```

### Release Build
- Optimized and minified
- Requires release signing key
- For production distribution

```bash
./gradlew assembleRelease
```

## Useful Commands

```bash
# Build the app
./gradlew build

# Install debug APK on connected device
./gradlew installDebug

# Run all tests
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates

# Generate APK
./gradlew assembleDebug

# View all Gradle tasks
./gradlew tasks
```

## Project Structure at a Glance

```
wellness_log/
├── app/
│   ├── build.gradle.kts          # App module build config
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/wellnesslog/app/
│   │       │   ├── MainActivity.kt
│   │       │   ├── data/          # Data layer
│   │       │   └── ui/            # UI layer
│   │       └── res/               # Resources (layouts, strings, etc.)
│   │
├── build.gradle.kts               # Project-level build config
├── settings.gradle.kts            # Project settings
├── gradle.properties              # Gradle properties
└── README.md                      # Project README
```

## Getting Help

If you encounter issues:

1. **Check Documentation**
   - Review [DEVELOPMENT.md](DEVELOPMENT.md)
   - Check [Android Studio documentation](https://developer.android.com/studio)

2. **Search Issues**
   - Look through GitHub issues
   - Search Stack Overflow

3. **Enable Verbose Logging**
   ```kotlin
   // Add to MainActivity.kt
   android.util.Log.d("WellnessLog", "Debug message")
   ```

4. **Check Logcat**
   - View → Tool Windows → Logcat
   - Filter by "WellnessLog" or error level

## Success! 🎉

You should now have the Wellness Log app running. Start tracking your fitness journey!

### Tips for Best Experience

- 📅 Log your data daily for best results
- 📊 Check your weight graph weekly to track progress
- 💪 Use the gym contributions grid for motivation
- 🎯 Set goals and use the app to stay accountable

Happy tracking! 💚
