# Photo Widget Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an Android widget that displays a random daily photo from a user-selected folder.

**Architecture:** Separate Android app module in the Cozyla repository. Uses Storage Access Framework (SAF) for folder selection, SharedPreferences for settings, and RemoteViews for the widget. Photo selection is random but stable per day (day-of-year seed).

**Tech Stack:** Kotlin, Android App Widgets, Storage Access Framework, SharedPreferences

---

### Task 1: Project Setup

**Files:**
- Create: `PhotoWidget/settings.gradle.kts`
- Create: `PhotoWidget/build.gradle.kts`
- Create: `PhotoWidget/gradle.properties`
- Create: `PhotoWidget/app/build.gradle.kts`
- Create: `PhotoWidget/app/proguard-rules.pro`
- Copy: Gradle wrapper from CatalanWordWidget

**Step 1: Create project root files**

Create `PhotoWidget/settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PhotoWidget"
include(":app")
```

Create `PhotoWidget/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
```

Create `PhotoWidget/gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

**Step 2: Create app build file**

Create `PhotoWidget/app/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cozyla.photowidget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cozyla.photowidget"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.documentfile:documentfile:1.0.1")
}
```

Create `PhotoWidget/app/proguard-rules.pro`:
```
# Add project specific ProGuard rules here.
```

**Step 3: Copy Gradle wrapper**

```bash
cp -r CatalanWordWidget/gradle PhotoWidget/
cp CatalanWordWidget/gradlew PhotoWidget/
cp CatalanWordWidget/gradlew.bat PhotoWidget/
```

**Step 4: Commit**

```bash
git add PhotoWidget/
git commit -m "feat(photo-widget): add project setup and build configuration"
```

---

### Task 2: Resource Files

**Files:**
- Create: `PhotoWidget/app/src/main/res/values/strings.xml`
- Create: `PhotoWidget/app/src/main/res/values/colors.xml`
- Create: `PhotoWidget/app/src/main/res/values/themes.xml`
- Create: `PhotoWidget/app/src/main/res/drawable/placeholder_background.xml`
- Create: `PhotoWidget/app/src/main/res/drawable/ic_launcher_foreground.xml`
- Create: `PhotoWidget/app/src/main/res/drawable/ic_launcher_background.xml`
- Create: `PhotoWidget/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- Create: `PhotoWidget/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`

**Step 1: Create strings.xml**

Create `PhotoWidget/app/src/main/res/values/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Photo Widget</string>
    <string name="widget_description">Displays a random photo from your selected folder each day</string>
    <string name="tap_to_select_folder">Tap to select folder</string>
    <string name="no_photos_found">No photos found</string>
    <string name="folder_not_found">Folder not found - tap to reconfigure</string>
    <string name="select_folder">Select Photo Folder</string>
    <string name="current_folder">Current folder:</string>
    <string name="no_folder_selected">No folder selected</string>
    <string name="change_folder">Change Folder</string>
</resources>
```

**Step 2: Create colors.xml**

Create `PhotoWidget/app/src/main/res/values/colors.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="placeholder_background">#E5E7EB</color>
    <color name="placeholder_text">#6B7280</color>
    <color name="white">#FFFFFF</color>
    <color name="primary">#0D9488</color>
</resources>
```

**Step 3: Create themes.xml**

Create `PhotoWidget/app/src/main/res/values/themes.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.PhotoWidget" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:colorPrimary">@color/primary</item>
    </style>
</resources>
```

**Step 4: Create placeholder background drawable**

Create `PhotoWidget/app/src/main/res/drawable/placeholder_background.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/placeholder_background" />
    <corners android:radius="16dp" />
</shape>
```

**Step 5: Create launcher icon drawables**

Create `PhotoWidget/app/src/main/res/drawable/ic_launcher_foreground.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <!-- Photo icon -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M34,34h40v40h-40z"/>
    <path
        android:fillColor="#0D9488"
        android:pathData="M38,38h32v32h-32zM42,58l6,-8l4,5l8,-10l10,13z"/>
    <circle
        android:fillColor="#0D9488"
        android:cx="48"
        android:cy="48"
        android:r="4"/>
</vector>
```

Create `PhotoWidget/app/src/main/res/drawable/ic_launcher_background.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0D9488"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
```

Create `PhotoWidget/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
```

Create `PhotoWidget/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
```

**Step 6: Commit**

```bash
git add PhotoWidget/app/src/main/res/
git commit -m "feat(photo-widget): add resource files (strings, colors, themes, icons)"
```

---

### Task 3: Widget Layout

**Files:**
- Create: `PhotoWidget/app/src/main/res/layout/widget_layout.xml`
- Create: `PhotoWidget/app/src/main/res/xml/widget_info.xml`

**Step 1: Create widget layout**

The widget has two states: configured (shows photo) and unconfigured (shows placeholder).

Create `PhotoWidget/app/src/main/res/layout/widget_layout.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/widget_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Photo display (edge-to-edge) -->
    <ImageView
        android:id="@+id/photo_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:visibility="gone" />

    <!-- Placeholder when no folder selected or no photos -->
    <LinearLayout
        android:id="@+id/placeholder_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        android:background="@drawable/placeholder_background"
        android:padding="16dp">

        <TextView
            android:id="@+id/placeholder_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/tap_to_select_folder"
            android:textSize="16sp"
            android:textColor="@color/placeholder_text"
            android:gravity="center" />

    </LinearLayout>

</FrameLayout>
```

**Step 2: Create widget info**

Create `PhotoWidget/app/src/main/res/xml/widget_info.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="180dp"
    android:minHeight="180dp"
    android:minResizeWidth="110dp"
    android:minResizeHeight="110dp"
    android:resizeMode="horizontal|vertical"
    android:initialLayout="@layout/widget_layout"
    android:previewLayout="@layout/widget_layout"
    android:updatePeriodMillis="86400000"
    android:widgetCategory="home_screen"
    android:description="@string/widget_description" />
```

**Step 3: Commit**

```bash
git add PhotoWidget/app/src/main/res/layout/ PhotoWidget/app/src/main/res/xml/
git commit -m "feat(photo-widget): add widget layout and info"
```

---

### Task 4: Settings Activity Layout

**Files:**
- Create: `PhotoWidget/app/src/main/res/layout/activity_folder_picker.xml`

**Step 1: Create settings layout**

Create `PhotoWidget/app/src/main/res/layout/activity_folder_picker.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/select_folder"
        android:textSize="24sp"
        android:textStyle="bold"
        android:textColor="#1A1A2E"
        android:layout_marginBottom="32dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/current_folder"
        android:textSize="14sp"
        android:textColor="#6B7280" />

    <TextView
        android:id="@+id/current_folder_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/no_folder_selected"
        android:textSize="16sp"
        android:textColor="#1A1A2E"
        android:layout_marginTop="8dp"
        android:layout_marginBottom="32dp" />

    <Button
        android:id="@+id/select_folder_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/change_folder"
        android:backgroundTint="@color/primary"
        android:textColor="@color/white"
        android:paddingStart="32dp"
        android:paddingEnd="32dp" />

</LinearLayout>
```

**Step 2: Commit**

```bash
git add PhotoWidget/app/src/main/res/layout/activity_folder_picker.xml
git commit -m "feat(photo-widget): add folder picker activity layout"
```

---

### Task 5: PhotoRepository

**Files:**
- Create: `PhotoWidget/app/src/main/java/com/cozyla/photowidget/PhotoRepository.kt`

**Step 1: Create PhotoRepository**

This class handles folder access and random photo selection.

Create `PhotoWidget/app/src/main/java/com/cozyla/photowidget/PhotoRepository.kt`:
```kotlin
package com.cozyla.photowidget

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.util.Calendar
import kotlin.random.Random

class PhotoRepository(private val context: Context) {

    companion object {
        private const val TAG = "PhotoRepository"
        private const val PREFS_NAME = "PhotoWidget"
        private const val PREF_FOLDER_URI = "folder_uri"
        private val SUPPORTED_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp")
    }

    fun getFolderUri(): Uri? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uriString = prefs.getString(PREF_FOLDER_URI, null)
        return uriString?.let { Uri.parse(it) }
    }

    fun saveFolderUri(uri: Uri) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_FOLDER_URI, uri.toString()).apply()
    }

    fun getTodaysPhoto(): PhotoResult {
        val folderUri = getFolderUri() ?: return PhotoResult.NotConfigured

        return try {
            val folder = DocumentFile.fromTreeUri(context, folderUri)
            if (folder == null || !folder.exists()) {
                return PhotoResult.FolderNotFound
            }

            val photos = folder.listFiles()
                .filter { file ->
                    file.isFile && file.name?.let { name ->
                        SUPPORTED_EXTENSIONS.any { ext ->
                            name.lowercase().endsWith(".$ext")
                        }
                    } == true
                }
                .sortedBy { it.name }

            if (photos.isEmpty()) {
                return PhotoResult.NoPhotos
            }

            // Use day-of-year as seed for stable daily random selection
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val seed = (year * 1000 + dayOfYear).toLong()
            val random = Random(seed)
            val index = random.nextInt(photos.size)

            val selectedPhoto = photos[index]
            Log.d(TAG, "Selected photo ${index + 1}/${photos.size}: ${selectedPhoto.name}")

            PhotoResult.Success(selectedPhoto.uri)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for folder", e)
            PhotoResult.PermissionDenied
        } catch (e: Exception) {
            Log.e(TAG, "Error getting photo", e)
            PhotoResult.Error(e.message ?: "Unknown error")
        }
    }

    sealed class PhotoResult {
        data class Success(val uri: Uri) : PhotoResult()
        object NotConfigured : PhotoResult()
        object FolderNotFound : PhotoResult()
        object NoPhotos : PhotoResult()
        object PermissionDenied : PhotoResult()
        data class Error(val message: String) : PhotoResult()
    }
}
```

**Step 2: Commit**

```bash
git add PhotoWidget/app/src/main/java/com/cozyla/photowidget/PhotoRepository.kt
git commit -m "feat(photo-widget): add PhotoRepository for folder access and photo selection"
```

---

### Task 6: FolderPickerActivity

**Files:**
- Create: `PhotoWidget/app/src/main/java/com/cozyla/photowidget/FolderPickerActivity.kt`

**Step 1: Create FolderPickerActivity**

Create `PhotoWidget/app/src/main/java/com/cozyla/photowidget/FolderPickerActivity.kt`:
```kotlin
package com.cozyla.photowidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile

class FolderPickerActivity : AppCompatActivity() {

    private lateinit var currentFolderText: TextView
    private lateinit var repository: PhotoRepository

    private val folderPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let { handleFolderSelected(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_picker)

        repository = PhotoRepository(this)
        currentFolderText = findViewById(R.id.current_folder_text)

        val selectButton: Button = findViewById(R.id.select_folder_button)
        selectButton.setOnClickListener {
            folderPicker.launch(null)
        }

        updateCurrentFolderDisplay()
    }

    private fun handleFolderSelected(uri: Uri) {
        // Take persistent permission
        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)

        // Save the URI
        repository.saveFolderUri(uri)

        // Update display
        updateCurrentFolderDisplay()

        // Update all widgets
        updateAllWidgets()
    }

    private fun updateCurrentFolderDisplay() {
        val folderUri = repository.getFolderUri()
        if (folderUri != null) {
            val folder = DocumentFile.fromTreeUri(this, folderUri)
            currentFolderText.text = folder?.name ?: folderUri.lastPathSegment
        } else {
            currentFolderText.text = getString(R.string.no_folder_selected)
        }
    }

    private fun updateAllWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, PhotoWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        val intent = Intent(this, PhotoWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        sendBroadcast(intent)
    }
}
```

**Step 2: Commit**

```bash
git add PhotoWidget/app/src/main/java/com/cozyla/photowidget/FolderPickerActivity.kt
git commit -m "feat(photo-widget): add FolderPickerActivity for folder selection"
```

---

### Task 7: PhotoWidgetProvider

**Files:**
- Create: `PhotoWidget/app/src/main/java/com/cozyla/photowidget/PhotoWidgetProvider.kt`

**Step 1: Create PhotoWidgetProvider**

Create `PhotoWidget/app/src/main/java/com/cozyla/photowidget/PhotoWidgetProvider.kt`:
```kotlin
package com.cozyla.photowidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import java.io.InputStream

class PhotoWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "PhotoWidgetProvider"
        const val ACTION_OPEN_PHOTO = "com.cozyla.photowidget.OPEN_PHOTO"
        const val EXTRA_PHOTO_URI = "photo_uri"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate called with ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive: ${intent.action}")

        if (intent.action == ACTION_OPEN_PHOTO) {
            val photoUri = intent.getStringExtra(EXTRA_PHOTO_URI)
            if (photoUri != null) {
                openPhotoInGallery(context, Uri.parse(photoUri))
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            Log.d(TAG, "updateWidget for id: $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            val repository = PhotoRepository(context)

            when (val result = repository.getTodaysPhoto()) {
                is PhotoRepository.PhotoResult.Success -> {
                    showPhoto(context, views, result.uri)
                }
                is PhotoRepository.PhotoResult.NotConfigured -> {
                    showPlaceholder(context, views, context.getString(R.string.tap_to_select_folder))
                }
                is PhotoRepository.PhotoResult.FolderNotFound -> {
                    showPlaceholder(context, views, context.getString(R.string.folder_not_found))
                }
                is PhotoRepository.PhotoResult.NoPhotos -> {
                    showPlaceholder(context, views, context.getString(R.string.no_photos_found))
                }
                is PhotoRepository.PhotoResult.PermissionDenied -> {
                    showPlaceholder(context, views, context.getString(R.string.folder_not_found))
                }
                is PhotoRepository.PhotoResult.Error -> {
                    showPlaceholder(context, views, result.message)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d(TAG, "Widget updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget", e)
        }
    }

    private fun showPhoto(context: Context, views: RemoteViews, photoUri: Uri) {
        try {
            // Load and scale bitmap to reasonable size for widget
            val bitmap = loadScaledBitmap(context, photoUri, 800)
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.photo_view, bitmap)
                views.setViewVisibility(R.id.photo_view, View.VISIBLE)
                views.setViewVisibility(R.id.placeholder_view, View.GONE)

                // Set click to open photo in gallery
                val openIntent = Intent(context, PhotoWidgetProvider::class.java).apply {
                    action = ACTION_OPEN_PHOTO
                    putExtra(EXTRA_PHOTO_URI, photoUri.toString())
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, photoUri.hashCode(), openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.photo_view, pendingIntent)
            } else {
                showPlaceholder(context, views, context.getString(R.string.no_photos_found))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading photo", e)
            showPlaceholder(context, views, context.getString(R.string.no_photos_found))
        }
    }

    private fun showPlaceholder(context: Context, views: RemoteViews, message: String) {
        views.setViewVisibility(R.id.photo_view, View.GONE)
        views.setViewVisibility(R.id.placeholder_view, View.VISIBLE)
        views.setTextViewText(R.id.placeholder_text, message)

        // Set click to open folder picker
        val configIntent = Intent(context, FolderPickerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, configIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.placeholder_view, pendingIntent)
    }

    private fun loadScaledBitmap(context: Context, uri: Uri, maxSize: Int): Bitmap? {
        return try {
            // First, get dimensions without loading full bitmap
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }

            // Calculate sample size
            val width = options.outWidth
            val height = options.outHeight
            var sampleSize = 1
            while (width / sampleSize > maxSize || height / sampleSize > maxSize) {
                sampleSize *= 2
            }

            // Load scaled bitmap
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, scaledOptions)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap", e)
            null
        }
    }

    private fun openPhotoInGallery(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening photo in gallery", e)
        }
    }
}
```

**Step 2: Commit**

```bash
git add PhotoWidget/app/src/main/java/com/cozyla/photowidget/PhotoWidgetProvider.kt
git commit -m "feat(photo-widget): add PhotoWidgetProvider for displaying photos"
```

---

### Task 8: AndroidManifest

**Files:**
- Create: `PhotoWidget/app/src/main/AndroidManifest.xml`

**Step 1: Create AndroidManifest.xml**

Create `PhotoWidget/app/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.PhotoWidget">

        <activity
            android:name=".FolderPickerActivity"
            android:exported="true"
            android:label="@string/select_folder">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <receiver
            android:name=".PhotoWidgetProvider"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.cozyla.photowidget.OPEN_PHOTO" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_info" />
        </receiver>

    </application>

</manifest>
```

**Step 2: Commit**

```bash
git add PhotoWidget/app/src/main/AndroidManifest.xml
git commit -m "feat(photo-widget): add AndroidManifest with activity and widget receiver"
```

---

### Task 9: Update GitHub Actions

**Files:**
- Modify: `.github/workflows/build-apk.yml`

**Step 1: Update workflow to build both apps**

Update `.github/workflows/build-apk.yml` to build both CatalanWordWidget and PhotoWidget:
```yaml
name: Build APKs

on:
  push:
    branches: [master, main]
  pull_request:
    branches: [master, main]
  workflow_dispatch:

jobs:
  build-catalan-widget:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Grant execute permission for gradlew
        run: chmod +x CatalanWordWidget/gradlew

      - name: Build Debug APK
        working-directory: CatalanWordWidget
        run: ./gradlew assembleDebug

      - name: Build Release APK
        working-directory: CatalanWordWidget
        run: ./gradlew assembleRelease

      - name: Upload Debug APK
        uses: actions/upload-artifact@v4
        with:
          name: catalan-word-widget-debug
          path: CatalanWordWidget/app/build/outputs/apk/debug/app-debug.apk

      - name: Upload Release APK
        uses: actions/upload-artifact@v4
        with:
          name: catalan-word-widget-release
          path: CatalanWordWidget/app/build/outputs/apk/release/app-release-unsigned.apk

  build-photo-widget:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Grant execute permission for gradlew
        run: chmod +x PhotoWidget/gradlew

      - name: Build Debug APK
        working-directory: PhotoWidget
        run: ./gradlew assembleDebug

      - name: Build Release APK
        working-directory: PhotoWidget
        run: ./gradlew assembleRelease

      - name: Upload Debug APK
        uses: actions/upload-artifact@v4
        with:
          name: photo-widget-debug
          path: PhotoWidget/app/build/outputs/apk/debug/app-debug.apk

      - name: Upload Release APK
        uses: actions/upload-artifact@v4
        with:
          name: photo-widget-release
          path: PhotoWidget/app/build/outputs/apk/release/app-release-unsigned.apk
```

**Step 2: Commit**

```bash
git add .github/workflows/build-apk.yml
git commit -m "ci: update workflow to build both Catalan word and photo widgets"
```

---

### Task 10: Push and Verify Build

**Step 1: Push to GitHub**

```bash
git push origin master
```

**Step 2: Verify build succeeds**

Go to https://github.com/gloscherrybomb/CatalanWordOfTheDay/actions and verify:
- Both build jobs complete successfully
- `photo-widget-debug` artifact is available for download

**Step 3: Test on device**

1. Download `photo-widget-debug` artifact from GitHub Actions
2. Install APK on device
3. Add widget to home screen
4. Tap widget to open folder picker
5. Select a folder with photos
6. Verify photo displays in widget
7. Tap photo to open in gallery
