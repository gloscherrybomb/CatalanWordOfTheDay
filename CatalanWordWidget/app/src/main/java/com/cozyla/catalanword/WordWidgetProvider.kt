package com.cozyla.catalanword

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews

class WordWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "WordWidgetProvider"
        const val ACTION_TOGGLE = "com.cozyla.catalanword.TOGGLE_TRANSLATION"
        const val ACTION_REFRESH = "com.cozyla.catalanword.REFRESH"
        private const val PREFS_NAME = "CatalanWordWidget"
        private const val PREF_SHOW_TRANSLATION = "show_translation"

        // Settings prefs (shared with MainActivity)
        private const val SETTINGS_PREFS_NAME = "CatalanWordWidgetSettings"
        private const val PREF_WORD_SIZE = "word_size"
        private const val PREF_EXAMPLE_SIZE = "example_size"
        private const val PREF_SHOW_EXAMPLES = "show_examples"
        private const val PREF_HEADER_STYLE = "header_style"

        // Header style constants
        private const val HEADER_FULL = 0
        private const val HEADER_MINIMAL = 1
        private const val HEADER_HIDDEN = 2
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
        try {
            Log.d(TAG, "updateWidget for id: $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            val repository = WordRepository(context)
            val word = repository.getTodaysWord()
            val showTranslation = getTranslationState(context)

            // Load settings
            val settingsPrefs = context.getSharedPreferences(SETTINGS_PREFS_NAME, Context.MODE_PRIVATE)
            val wordSize = settingsPrefs.getInt(PREF_WORD_SIZE, 18)
            val exampleSize = settingsPrefs.getInt(PREF_EXAMPLE_SIZE, 10)
            val showExamples = settingsPrefs.getBoolean(PREF_SHOW_EXAMPLES, true)
            val headerStyle = settingsPrefs.getInt(PREF_HEADER_STYLE, HEADER_FULL)

            Log.d(TAG, "Settings: wordSize=$wordSize, exampleSize=$exampleSize, showExamples=$showExamples, headerStyle=$headerStyle")
            Log.d(TAG, "Displaying word: ${word.word}")

            // Set word content
            views.setTextViewText(R.id.catalan_word, word.word)
            views.setTextViewText(R.id.english_word, word.translation)
            views.setTextViewText(R.id.catalan_example, word.example)
            views.setTextViewText(R.id.english_example, word.exampleTranslation)

            // Apply text sizes from settings
            views.setTextViewTextSize(R.id.catalan_word, TypedValue.COMPLEX_UNIT_SP, wordSize.toFloat())
            views.setTextViewTextSize(R.id.english_word, TypedValue.COMPLEX_UNIT_SP, (wordSize * 0.65f))
            views.setTextViewTextSize(R.id.catalan_example, TypedValue.COMPLEX_UNIT_SP, exampleSize.toFloat())
            views.setTextViewTextSize(R.id.english_example, TypedValue.COMPLEX_UNIT_SP, (exampleSize * 0.9f))
            views.setTextViewTextSize(R.id.hint_text, TypedValue.COMPLEX_UNIT_SP, (exampleSize * 0.8f))

            // Toggle visibility based on state
            val translationVisibility = if (showTranslation) View.VISIBLE else View.GONE
            views.setViewVisibility(R.id.english_word, translationVisibility)
            views.setViewVisibility(R.id.english_example, if (showTranslation && showExamples) View.VISIBLE else View.GONE)

            // Show/hide examples based on settings
            views.setViewVisibility(R.id.catalan_example, if (showExamples) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.hint_text, if (showExamples) View.VISIBLE else View.GONE)

            // Apply header style
            when (headerStyle) {
                HEADER_FULL -> {
                    views.setViewVisibility(R.id.header_full, View.VISIBLE)
                    views.setViewVisibility(R.id.header_minimal, View.GONE)
                }
                HEADER_MINIMAL -> {
                    views.setViewVisibility(R.id.header_full, View.GONE)
                    views.setViewVisibility(R.id.header_minimal, View.VISIBLE)
                }
                HEADER_HIDDEN -> {
                    views.setViewVisibility(R.id.header_full, View.GONE)
                    views.setViewVisibility(R.id.header_minimal, View.GONE)
                }
            }

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

            // Set click listener for refresh buttons (both header variants)
            val refreshIntent = Intent(context, WordWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context, 1, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.refresh_button_full, refreshPendingIntent)
            views.setOnClickPendingIntent(R.id.refresh_button_minimal, refreshPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d(TAG, "Widget updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget", e)
        }
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
