# Wellness Log - Setup and Development Guide

## Project Overview

This Android application tracks gym visits and weight with three main screens:

1. **Main Screen**: Daily logging of gym visits, calorie deficit, and weight
2. **Weight Graph Screen**: Visual representation of weight over time
3. **Gym Contributions Screen**: GitHub-style contribution grid for gym visits

## Architecture

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation**: Navigation Compose
- **Charts**: MPAndroidChart
- **Data Storage**: SharedPreferences (local storage)
- **Async**: Kotlin Coroutines

### Project Structure
```
app/
├── src/main/
│   ├── java/com/wellnesslog/app/
│   │   ├── MainActivity.kt           # Main entry point
│   │   ├── data/
│   │   │   ├── WellnessEntry.kt      # Data model
│   │   │   └── WellnessRepository.kt # Data access layer
│   │   └── ui/
│   │       ├── screens/
│   │       │   ├── MainScreen.kt              # Main logging screen
│   │       │   ├── WeightGraphScreen.kt       # Weight visualization
│   │       │   └── GymContributionsScreen.kt  # Gym stats
│   │       └── theme/
│   │           └── Theme.kt           # App theme
│   └── res/
│       ├── values/
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── ...
```

## Requirements

### Development Environment
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK 34
- Minimum Android API level: 24 (Android 7.0)
- Target Android API level: 34

### Dependencies
All dependencies are managed in `app/build.gradle.kts`:
- AndroidX Core, Lifecycle, Activity
- Jetpack Compose (UI, Material 3, Navigation)
- MPAndroidChart for graphs
- Google API Client and Sheets API (for future Google Sheets integration)
- Kotlin Coroutines

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/Sukonnik-Illia/wellness_log.git
cd wellness_log
```

### 2. Open in Android Studio
- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned repository
- Wait for Gradle sync to complete

### 3. Run the App
- Connect an Android device or start an emulator
- Click the "Run" button (green triangle) or press Shift+F10
- Select your device/emulator
- The app will build and install automatically

## Features in Detail

### Main Screen
- **Date Picker**: 
  - Default: Today's date
  - Can select past dates only (future dates disabled)
  - Material 3 DatePicker dialog
  
- **Gym Visit Checkbox**: 
  - Mark if you visited the gym on selected date
  
- **Calorie Deficit Checkbox**: 
  - Track if you maintained a calorie deficit
  
- **Weight Input**: 
  - Uses +/- buttons for fine control (0.1 kg increments)
  - Automatically pre-fills with previous day's weight
  - Displays current value in kg
  
- **Save Button**: 
  - Saves all data to local storage
  - Uses coroutines for non-blocking I/O

### Weight Graph Screen
- **Date Range Selector**: 
  - Choose start and end dates
  - Default: Last 30 days
  
- **Line Chart**: 
  - Shows weight progression over time
  - Interactive (zoom, pan)
  - Only shows dates with weight data
  - Green color scheme matching app theme

### Gym Contributions Screen
- **Contribution Grid**: 
  - GitHub-style visualization
  - Each square represents one day
  - Green = gym visit, Gray = no visit
  - Organized by week (7 rows, weeks as columns)
  - Shows current year data
  
- **Statistics Card**: 
  - Total gym visits count
  - Attendance rate percentage
  - Based on days elapsed in current year

## Data Model

### WellnessEntry
```kotlin
data class WellnessEntry(
    val date: LocalDate,
    val gymVisit: Boolean = false,
    val calorieDeficit: Boolean = false,
    val weight: Float? = null
)
```

### Storage
Data is stored in SharedPreferences with the following key format:
- `{date}_gym`: Boolean for gym visit
- `{date}_calorie`: Boolean for calorie deficit
- `{date}_weight`: Float for weight (optional)

Example: `2024-01-15_gym`, `2024-01-15_calorie`, `2024-01-15_weight`

## Future Enhancements

### Google Sheets Integration (Planned)
The app is designed to integrate with Google Sheets for cloud storage:

1. **Authentication**: 
   - Use Google Sign-In
   - Request Sheets API permissions
   
2. **Spreadsheet Structure**:
   - Column A: Date
   - Column B: Gym Visit (TRUE/FALSE)
   - Column C: Calorie Deficit (TRUE/FALSE)
   - Column D: Weight (kg)
   
3. **Sync Strategy**:
   - Local-first: All operations work offline
   - Background sync when online
   - Conflict resolution: Last write wins

### Implementation Steps:
1. Add Google Sign-In dependency
2. Configure OAuth 2.0 credentials in Google Cloud Console
3. Implement authentication flow
4. Create/connect to spreadsheet
5. Implement bi-directional sync
6. Add sync indicator in UI
7. Handle offline mode gracefully

## Testing

### Manual Testing Checklist
- [ ] Main Screen
  - [ ] Date picker shows today by default
  - [ ] Date picker restricts future dates
  - [ ] Gym visit checkbox toggles
  - [ ] Calorie deficit checkbox toggles
  - [ ] Weight +/- buttons work (0.1 kg increments)
  - [ ] Weight pre-fills from previous day
  - [ ] Save button persists data
  
- [ ] Weight Graph Screen
  - [ ] Navigation from main screen works
  - [ ] Back button returns to main screen
  - [ ] Date range pickers work
  - [ ] Chart displays weight data correctly
  - [ ] Empty state shows when no data
  
- [ ] Gym Contributions Screen
  - [ ] Navigation from main screen works
  - [ ] Back button returns to main screen
  - [ ] Grid shows correct year
  - [ ] Green squares for gym visits
  - [ ] Gray squares for non-visits
  - [ ] Statistics calculate correctly

### Unit Testing (To be added)
Create tests for:
- `WellnessRepository`: CRUD operations
- Date validation logic
- Statistics calculations

### UI Testing (To be added)
Use Compose Testing:
- Navigation flow
- User interactions
- State changes

## Troubleshooting

### Gradle Build Issues
- Ensure Android SDK is installed and up to date
- Check that ANDROID_HOME environment variable is set
- Sync project with Gradle files (File → Sync Project with Gradle Files)

### Runtime Issues
- Check minimum SDK version (API 24+)
- Verify device/emulator is running Android 7.0 or higher
- Check logcat for error messages

### Date Picker Issues
- Ensure device locale is set correctly
- Check that date picker is using Material 3 components

## Contributing

When contributing to this project:
1. Follow Kotlin coding conventions
2. Use Jetpack Compose best practices
3. Ensure all screens work offline
4. Test on multiple screen sizes
5. Update documentation for new features

## License

See LICENSE file in the repository root.
