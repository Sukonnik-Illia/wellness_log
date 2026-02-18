# Wellness Log

A simple Android app for tracking gym visits and weight.

## Features

### Main Screen
- Date picker (today and past dates only)
- Gym visit checkbox
- Calorie deficit checkbox
- Weight field with +/- controls (pre-filled with yesterday's weight)

### Weight Graph Screen
- Line chart showing weight over time
- Date range selector

### Gym Contributions Screen
- GitHub-style contribution grid showing gym visits
- Statistics (total visits, attendance rate)

## Building

This is an Android project built with Gradle. To build:

```bash
./gradlew build
```

To run on an emulator or device:

```bash
./gradlew installDebug
```

## Data Storage

Currently, data is stored locally using SharedPreferences. Future versions will integrate with Google Sheets for cloud storage.

## Requirements

- Android SDK 24 (Android 7.0) or higher
- Java 8 or higher
- Gradle 8.2
