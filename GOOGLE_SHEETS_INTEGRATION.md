# Google Sheets Integration Guide

This document provides detailed steps for integrating Google Sheets storage into the Wellness Log app.

## Overview

The app is designed to store data in Google Sheets, allowing:
- Cloud backup of your wellness data
- Access from multiple devices
- Data portability and analysis in spreadsheets
- Easy sharing with trainers or health professionals

## Current Status

**Note**: The current implementation uses local SharedPreferences for data storage. Google Sheets integration is planned for a future release.

## Implementation Plan

### Phase 1: Google Cloud Console Setup

1. **Create a Google Cloud Project**
   - Go to https://console.cloud.google.com/
   - Click "Create Project"
   - Name: "Wellness Log"
   - Note the Project ID

2. **Enable Google Sheets API**
   - Navigate to "APIs & Services" → "Library"
   - Search for "Google Sheets API"
   - Click "Enable"

3. **Enable Google Drive API**
   - Search for "Google Drive API"
   - Click "Enable"

4. **Configure OAuth Consent Screen**
   - Go to "APIs & Services" → "OAuth consent screen"
   - Select "External" user type
   - Fill in app information:
     - App name: "Wellness Log"
     - User support email: your email
     - Developer contact: your email
   - Add scopes:
     - `https://www.googleapis.com/auth/spreadsheets`
     - `https://www.googleapis.com/auth/drive.file`

5. **Create OAuth 2.0 Credentials**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "OAuth client ID"
   - Application type: "Android"
   - Name: "Wellness Log Android"
   - Package name: `com.wellnesslog.app`
   - SHA-1 certificate fingerprint:
     ```bash
     # Debug keystore (for development)
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     
     # Release keystore (for production)
     keytool -list -v -keystore /path/to/release.keystore -alias your-alias
     ```
   - Note the Client ID

### Phase 2: Android App Integration

#### 1. Add Dependencies

In `app/build.gradle.kts`, the following dependencies are already included:
```kotlin
// Google Sheets API
implementation("com.google.android.gms:play-services-auth:20.7.0")
implementation("com.google.api-client:google-api-client-android:2.2.0")
implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")
```

#### 2. Update AndroidManifest.xml

Add the following permissions and meta-data:
```xml
<!-- Already included -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Add these -->
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.MANAGE_ACCOUNTS" />

<application>
    <meta-data
        android:name="com.google.android.gms.version"
        android:value="@integer/google_play_services_version" />
</application>
```

#### 3. Create GoogleSheetsService

Create `app/src/main/java/com/wellnesslog/app/data/GoogleSheetsService.kt`:

```kotlin
package com.wellnesslog.app.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoogleSheetsService(private val context: Context) {
    private val scopes = listOf(SheetsScopes.SPREADSHEETS)
    private var sheetsService: Sheets? = null
    private var spreadsheetId: String? = null
    
    companion object {
        const val SHEET_NAME = "Wellness Log"
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
    }

    fun getSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .build()
    }

    fun initializeService(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, scopes
        ).apply {
            selectedAccount = account.account
        }

        sheetsService = Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("Wellness Log")
            .build()
    }

    suspend fun createSpreadsheet(): String = withContext(Dispatchers.IO) {
        val spreadsheet = Spreadsheet().apply {
            properties = SpreadsheetProperties().apply {
                title = "Wellness Log Data"
            }
            sheets = listOf(
                Sheet().apply {
                    properties = SheetProperties().apply {
                        title = SHEET_NAME
                    }
                }
            )
        }

        val result = sheetsService?.spreadsheets()?.create(spreadsheet)?.execute()
        spreadsheetId = result?.spreadsheetId
        
        // Add headers
        initializeHeaders()
        
        spreadsheetId ?: throw Exception("Failed to create spreadsheet")
    }

    private suspend fun initializeHeaders() = withContext(Dispatchers.IO) {
        val headers = listOf(
            listOf("Date", "Gym Visit", "Calorie Deficit", "Weight (kg)")
        )
        
        val body = ValueRange().setValues(headers)
        sheetsService?.spreadsheets()?.values()
            ?.update(spreadsheetId, "$SHEET_NAME!A1:D1", body)
            ?.setValueInputOption("RAW")
            ?.execute()
    }

    suspend fun saveEntry(entry: WellnessEntry) = withContext(Dispatchers.IO) {
        val values = listOf(
            listOf(
                entry.date.format(DATE_FORMATTER),
                entry.gymVisit.toString(),
                entry.calorieDeficit.toString(),
                entry.weight?.toString() ?: ""
            )
        )

        val body = ValueRange().setValues(values)
        
        // Check if entry exists for this date
        val existingRow = findRowForDate(entry.date)
        val range = if (existingRow != null) {
            "$SHEET_NAME!A$existingRow:D$existingRow"
        } else {
            "$SHEET_NAME!A:D"
        }

        sheetsService?.spreadsheets()?.values()
            ?.append(spreadsheetId, range, body)
            ?.setValueInputOption("RAW")
            ?.setInsertDataOption("OVERWRITE")
            ?.execute()
    }

    suspend fun loadAllEntries(): List<WellnessEntry> = withContext(Dispatchers.IO) {
        val response = sheetsService?.spreadsheets()?.values()
            ?.get(spreadsheetId, "$SHEET_NAME!A2:D")
            ?.execute()

        val values = response?.getValues() ?: return@withContext emptyList()
        
        values.mapNotNull { row ->
            try {
                WellnessEntry(
                    date = LocalDate.parse(row[0].toString(), DATE_FORMATTER),
                    gymVisit = row.getOrNull(1)?.toString()?.toBoolean() ?: false,
                    calorieDeficit = row.getOrNull(2)?.toString()?.toBoolean() ?: false,
                    weight = row.getOrNull(3)?.toString()?.toFloatOrNull()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun findRowForDate(date: LocalDate): Int? = withContext(Dispatchers.IO) {
        val response = sheetsService?.spreadsheets()?.values()
            ?.get(spreadsheetId, "$SHEET_NAME!A:A")
            ?.execute()

        val values = response?.getValues() ?: return@withContext null
        val dateStr = date.format(DATE_FORMATTER)
        
        values.indexOfFirst { it.isNotEmpty() && it[0].toString() == dateStr }
            .takeIf { it >= 0 }
            ?.let { it + 1 } // Convert to 1-based row number
    }

    fun setSpreadsheetId(id: String) {
        spreadsheetId = id
    }

    fun getSpreadsheetId(): String? = spreadsheetId
}
```

#### 4. Update WellnessRepository

Add Google Sheets sync capabilities to `WellnessRepository.kt`:

```kotlin
class WellnessRepository(context: Context) {
    // ... existing code ...
    
    private val googleSheetsService = GoogleSheetsService(context)
    private var syncEnabled = false

    fun enableGoogleSheetsSync(account: GoogleSignInAccount, spreadsheetId: String? = null) {
        googleSheetsService.initializeService(account)
        spreadsheetId?.let { googleSheetsService.setSpreadsheetId(it) }
        syncEnabled = true
    }

    suspend fun createGoogleSpreadsheet(): String {
        return googleSheetsService.createSpreadsheet()
    }

    suspend fun syncToGoogleSheets() = withContext(Dispatchers.IO) {
        if (!syncEnabled) return@withContext
        
        val entries = getAllEntries()
        entries.forEach { entry ->
            googleSheetsService.saveEntry(entry)
        }
    }

    suspend fun syncFromGoogleSheets() = withContext(Dispatchers.IO) {
        if (!syncEnabled) return@withContext
        
        val entries = googleSheetsService.loadAllEntries()
        entries.forEach { entry ->
            saveEntry(entry)
        }
    }
}
```

#### 5. Add Authentication Screen

Create `app/src/main/java/com/wellnesslog/app/ui/screens/GoogleSheetsSetupScreen.kt`:

```kotlin
package com.wellnesslog.app.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.wellnesslog.app.data.WellnessRepository
import kotlinx.coroutines.launch

@Composable
fun GoogleSheetsSetupScreen(
    repository: WellnessRepository,
    onSetupComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                scope.launch {
                    isLoading = true
                    try {
                        val spreadsheetId = repository.createGoogleSpreadsheet()
                        repository.enableGoogleSheetsSync(account, spreadsheetId)
                        repository.syncToGoogleSheets()
                        onSetupComplete()
                    } catch (e: Exception) {
                        errorMessage = "Failed to create spreadsheet: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Sign-in failed: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Google Sheets Integration",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Connect your Google account to store data in Google Sheets",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val googleSignInClient = GoogleSignIn.getClient(
                        context,
                        repository.getGoogleSignInOptions()
                    )
                    signInLauncher.launch(googleSignInClient.signInIntent)
                }
            ) {
                Text("Connect Google Account")
            }
        }
        
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
```

### Phase 3: Testing

1. **Test Authentication Flow**
   - Sign in with Google account
   - Verify OAuth consent screen appears
   - Check that permissions are granted

2. **Test Spreadsheet Creation**
   - Create new spreadsheet
   - Verify headers are correct
   - Check spreadsheet permissions

3. **Test Data Sync**
   - Save entry locally
   - Sync to Google Sheets
   - Verify data appears in spreadsheet
   - Modify data in spreadsheet
   - Sync from Google Sheets
   - Verify local data updates

4. **Test Offline Mode**
   - Disable network
   - Make changes locally
   - Re-enable network
   - Sync changes

### Phase 4: Production Deployment

1. **Generate Release Signing Key**
   ```bash
   keytool -genkey -v -keystore release.keystore -alias wellness-log -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Update OAuth Credentials**
   - Add release SHA-1 fingerprint to Google Cloud Console
   - Generate new Client ID for production

3. **Configure App Signing**
   - Upload release keystore to app signing
   - Configure build variants

4. **Security Considerations**
   - Never commit keystore files
   - Use environment variables for sensitive data
   - Implement certificate pinning
   - Add ProGuard rules for API client

## Data Privacy

- All data is stored in user's own Google account
- App requests minimum necessary permissions
- Data is not shared with third parties
- User can revoke access at any time
- Spreadsheets can be deleted by user

## Troubleshooting

### Common Issues

1. **"Sign-in failed"**
   - Check OAuth client ID is correctly configured
   - Verify SHA-1 fingerprint matches
   - Ensure Google Sheets API is enabled

2. **"Insufficient permissions"**
   - Check requested scopes include spreadsheets
   - Verify OAuth consent screen is configured
   - Re-authenticate if needed

3. **"Network error"**
   - Check internet connectivity
   - Verify API quotas not exceeded
   - Check for Google API outages

## Resources

- [Google Sheets API Documentation](https://developers.google.com/sheets/api)
- [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android)
- [OAuth 2.0 for Mobile Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
