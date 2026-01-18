# Catalan Word Widget Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an Android widget that displays a daily Catalan word with tap-to-reveal English translations.

**Architecture:** Native Android widget using Kotlin, embedded JSON word database, SharedPreferences for state persistence, AlarmManager for daily updates.

**Tech Stack:** Kotlin, Android SDK (API 26+), Gradle, JSON

---

## Task 1: Project Setup

**Files:**
- Create: `CatalanWordWidget/settings.gradle.kts`
- Create: `CatalanWordWidget/build.gradle.kts`
- Create: `CatalanWordWidget/app/build.gradle.kts`
- Create: `CatalanWordWidget/gradle.properties`
- Create: `CatalanWordWidget/app/src/main/AndroidManifest.xml`

**Step 1: Create project root files**

Create `CatalanWordWidget/settings.gradle.kts`:
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
rootProject.name = "CatalanWordWidget"
include(":app")
```

Create `CatalanWordWidget/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
```

Create `CatalanWordWidget/gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

**Step 2: Create app module build file**

Create `CatalanWordWidget/app/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cozyla.catalanword"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cozyla.catalanword"
        minSdk = 26
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
    implementation("com.google.code.gson:gson:2.10.1")
}
```

**Step 3: Create AndroidManifest.xml**

Create `CatalanWordWidget/app/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.CatalanWordWidget">

        <receiver
            android:name=".WordWidgetProvider"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.cozyla.catalanword.TOGGLE_TRANSLATION" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.cozyla.catalanword.REFRESH" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_info" />
        </receiver>

    </application>

</manifest>
```

**Step 4: Commit**

```bash
git add CatalanWordWidget/
git commit -m "feat: initialize Android project structure"
```

---

## Task 2: Resource Files (Colors, Strings, Themes)

**Files:**
- Create: `CatalanWordWidget/app/src/main/res/values/colors.xml`
- Create: `CatalanWordWidget/app/src/main/res/values/strings.xml`
- Create: `CatalanWordWidget/app/src/main/res/values/themes.xml`
- Create: `CatalanWordWidget/app/src/main/res/mipmap-hdpi/ic_launcher.png` (placeholder)

**Step 1: Create colors.xml**

Create `CatalanWordWidget/app/src/main/res/values/colors.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="teal_accent">#2DB3A2</color>
    <color name="text_dark">#333333</color>
    <color name="text_medium">#666666</color>
    <color name="text_light">#888888</color>
    <color name="text_hint">#AAAAAA</color>
    <color name="card_background">#FFFFFF</color>
    <color name="header_background">#F8F8F8</color>
</resources>
```

**Step 2: Create strings.xml**

Create `CatalanWordWidget/app/src/main/res/values/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Catalan Word</string>
    <string name="widget_name">Paraula del Dia</string>
    <string name="widget_description">Daily Catalan vocabulary word</string>
    <string name="tap_to_reveal">Tap to reveal ›</string>
    <string name="tap_to_hide">Tap to hide ‹</string>
</resources>
```

**Step 3: Create themes.xml**

Create `CatalanWordWidget/app/src/main/res/values/themes.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.CatalanWordWidget" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:colorPrimary">@color/teal_accent</item>
    </style>
</resources>
```

**Step 4: Create placeholder launcher icon**

Create directory and note that a proper icon should be added later. For now, Android Studio default will work.

**Step 5: Commit**

```bash
git add CatalanWordWidget/app/src/main/res/
git commit -m "feat: add color scheme and string resources"
```

---

## Task 3: Widget Layout XML

**Files:**
- Create: `CatalanWordWidget/app/src/main/res/xml/widget_info.xml`
- Create: `CatalanWordWidget/app/src/main/res/drawable/widget_background.xml`
- Create: `CatalanWordWidget/app/src/main/res/layout/widget_layout.xml`

**Step 1: Create widget_info.xml**

Create `CatalanWordWidget/app/src/main/res/xml/widget_info.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="180dp"
    android:minHeight="110dp"
    android:minResizeWidth="110dp"
    android:minResizeHeight="40dp"
    android:resizeMode="horizontal|vertical"
    android:initialLayout="@layout/widget_layout"
    android:previewLayout="@layout/widget_layout"
    android:updatePeriodMillis="86400000"
    android:widgetCategory="home_screen"
    android:description="@string/widget_description" />
```

**Step 2: Create widget_background.xml drawable**

Create `CatalanWordWidget/app/src/main/res/drawable/widget_background.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/card_background" />
    <corners android:radius="16dp" />
</shape>
```

**Step 3: Create widget_layout.xml**

Create `CatalanWordWidget/app/src/main/res/layout/widget_layout.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/widget_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@drawable/widget_background"
    android:padding="12dp">

    <!-- Header -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:paddingBottom="8dp">

        <TextView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="📖  Paraula del Dia"
            android:textSize="14sp"
            android:textColor="@color/text_dark"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/refresh_button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="↻"
            android:textSize="18sp"
            android:textColor="@color/teal_accent"
            android:paddingStart="8dp"
            android:paddingEnd="4dp" />

    </LinearLayout>

    <!-- Divider -->
    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="#EEEEEE" />

    <!-- Content area (clickable) -->
    <LinearLayout
        android:id="@+id/content_area"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="vertical"
        android:paddingTop="12dp"
        android:gravity="center_vertical">

        <!-- Catalan Word -->
        <TextView
            android:id="@+id/catalan_word"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Bon dia"
            android:textSize="24sp"
            android:textColor="@color/text_dark"
            android:textStyle="bold" />

        <!-- English Translation (hidden by default) -->
        <TextView
            android:id="@+id/english_word"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Good morning"
            android:textSize="16sp"
            android:textColor="@color/text_medium"
            android:visibility="gone"
            android:paddingTop="2dp" />

        <!-- Catalan Example -->
        <TextView
            android:id="@+id/catalan_example"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="\"Bon dia! Com estàs?\""
            android:textSize="14sp"
            android:textColor="@color/teal_accent"
            android:textStyle="italic"
            android:paddingTop="12dp" />

        <!-- English Example (hidden by default) -->
        <TextView
            android:id="@+id/english_example"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="\"Good morning! How are you?\""
            android:textSize="13sp"
            android:textColor="@color/text_light"
            android:textStyle="italic"
            android:visibility="gone"
            android:paddingTop="2dp" />

        <!-- Hint text -->
        <TextView
            android:id="@+id/hint_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/tap_to_reveal"
            android:textSize="12sp"
            android:textColor="@color/text_hint"
            android:gravity="end"
            android:paddingTop="8dp" />

    </LinearLayout>

</LinearLayout>
```

**Step 4: Commit**

```bash
git add CatalanWordWidget/app/src/main/res/
git commit -m "feat: add widget layout and styling"
```

---

## Task 4: Data Model and Repository

**Files:**
- Create: `CatalanWordWidget/app/src/main/java/com/cozyla/catalanword/Word.kt`
- Create: `CatalanWordWidget/app/src/main/java/com/cozyla/catalanword/WordRepository.kt`

**Step 1: Create Word data class**

Create directories first, then create `CatalanWordWidget/app/src/main/java/com/cozyla/catalanword/Word.kt`:
```kotlin
package com.cozyla.catalanword

data class Word(
    val word: String,
    val translation: String,
    val example: String,
    val exampleTranslation: String
)
```

**Step 2: Create WordRepository**

Create `CatalanWordWidget/app/src/main/java/com/cozyla/catalanword/WordRepository.kt`:
```kotlin
package com.cozyla.catalanword

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class WordRepository(private val context: Context) {

    private var words: List<Word>? = null

    fun getTodaysWord(): Word {
        val wordList = getWords()
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % wordList.size
        return wordList[index]
    }

    private fun getWords(): List<Word> {
        if (words == null) {
            val json = context.assets.open("words.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<Word>>() {}.type
            words = Gson().fromJson(json, type)
        }
        return words!!
    }
}
```

**Step 3: Commit**

```bash
git add CatalanWordWidget/app/src/main/java/
git commit -m "feat: add Word data model and repository"
```

---

## Task 5: Widget Provider (Core Logic)

**Files:**
- Create: `CatalanWordWidget/app/src/main/java/com/cozyla/catalanword/WordWidgetProvider.kt`

**Step 1: Create WordWidgetProvider**

Create `CatalanWordWidget/app/src/main/java/com/cozyla/catalanword/WordWidgetProvider.kt`:
```kotlin
package com.cozyla.catalanword

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

class WordWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "com.cozyla.catalanword.TOGGLE_TRANSLATION"
        const val ACTION_REFRESH = "com.cozyla.catalanword.REFRESH"
        private const val PREFS_NAME = "CatalanWordWidget"
        private const val PREF_SHOW_TRANSLATION = "show_translation"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE -> {
                toggleTranslation(context)
                updateAllWidgets(context)
            }
            ACTION_REFRESH -> {
                resetTranslationState(context)
                updateAllWidgets(context)
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        val repository = WordRepository(context)
        val word = repository.getTodaysWord()
        val showTranslation = getTranslationState(context)

        // Set word content
        views.setTextViewText(R.id.catalan_word, word.word)
        views.setTextViewText(R.id.english_word, word.translation)
        views.setTextViewText(R.id.catalan_example, "\"${word.example}\"")
        views.setTextViewText(R.id.english_example, "\"${word.exampleTranslation}\"")

        // Toggle visibility based on state
        val translationVisibility = if (showTranslation) View.VISIBLE else View.GONE
        views.setViewVisibility(R.id.english_word, translationVisibility)
        views.setViewVisibility(R.id.english_example, translationVisibility)

        // Update hint text
        val hintText = if (showTranslation) {
            context.getString(R.string.tap_to_hide)
        } else {
            context.getString(R.string.tap_to_reveal)
        }
        views.setTextViewText(R.id.hint_text, hintText)

        // Set click listener for content area (toggle translation)
        val toggleIntent = Intent(context, WordWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.content_area, togglePendingIntent)

        // Set click listener for refresh button
        val refreshIntent = Intent(context, WordWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context, 1, refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, WordWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun getTranslationState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_SHOW_TRANSLATION, false)
    }

    private fun toggleTranslation(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getBoolean(PREF_SHOW_TRANSLATION, false)
        prefs.edit().putBoolean(PREF_SHOW_TRANSLATION, !current).apply()
    }

    private fun resetTranslationState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_SHOW_TRANSLATION, false).apply()
    }
}
```

**Step 2: Commit**

```bash
git add CatalanWordWidget/app/src/main/java/
git commit -m "feat: implement widget provider with tap-to-reveal"
```

---

## Task 6: Word Database (365 words)

**Files:**
- Create: `CatalanWordWidget/app/src/main/assets/words.json`

**Step 1: Create words.json with 365 Catalan words**

Create `CatalanWordWidget/app/src/main/assets/words.json` containing a JSON array with 365 word objects covering:
- Greetings & Basics (30)
- Numbers & Time (25)
- Food & Drink (40)
- Family & People (25)
- Places & Directions (30)
- Weather & Nature (25)
- Common Verbs (50)
- Adjectives (35)
- Shopping & Money (20)
- Travel & Transport (25)
- Home & Objects (30)
- Useful Phrases (30)

Each entry follows this format:
```json
{
  "word": "Gràcies",
  "translation": "Thank you",
  "example": "Gràcies per la teva ajuda!",
  "exampleTranslation": "Thank you for your help!"
}
```

**Step 2: Commit**

```bash
git add CatalanWordWidget/app/src/main/assets/
git commit -m "feat: add 365 Catalan vocabulary words"
```

---

## Task 7: Gradle Wrapper and Build Test

**Files:**
- Create: `CatalanWordWidget/gradlew` (via gradle wrapper)
- Create: `CatalanWordWidget/gradlew.bat`
- Create: `CatalanWordWidget/gradle/wrapper/gradle-wrapper.properties`
- Create: `CatalanWordWidget/gradle/wrapper/gradle-wrapper.jar`

**Step 1: Initialize Gradle wrapper**

```bash
cd CatalanWordWidget
gradle wrapper --gradle-version 8.2
```

If gradle is not installed, create wrapper files manually:

Create `CatalanWordWidget/gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

**Step 2: Verify build compiles**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add .
git commit -m "feat: add gradle wrapper and verify build"
```

---

## Task 8: Build Release APK

**Step 1: Build release APK**

```bash
cd CatalanWordWidget
./gradlew assembleRelease
```

**Step 2: Locate APK**

APK will be at: `CatalanWordWidget/app/build/outputs/apk/release/app-release-unsigned.apk`

**Step 3: Final commit**

```bash
git add .
git commit -m "chore: build release APK"
```

---

## Completion Checklist

- [ ] Project structure created
- [ ] Resource files (colors, strings, themes)
- [ ] Widget layout XML
- [ ] Word data model and repository
- [ ] Widget provider with toggle logic
- [ ] 365-word database
- [ ] Gradle wrapper configured
- [ ] Release APK built
