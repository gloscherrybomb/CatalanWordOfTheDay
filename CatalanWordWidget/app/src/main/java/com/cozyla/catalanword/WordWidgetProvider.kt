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
