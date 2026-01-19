package com.cozyla.catalanword

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.app.Activity

class MainActivity : Activity() {

    companion object {
        const val PREFS_NAME = "CatalanWordWidgetSettings"
        const val PREF_WORD_SIZE = "word_size"
        const val PREF_EXAMPLE_SIZE = "example_size"
        const val PREF_SHOW_EXAMPLES = "show_examples"
        const val PREF_DENSITY = "density"

        const val DENSITY_COMPACT = 0
        const val DENSITY_NORMAL = 1
        const val DENSITY_SPACIOUS = 2
    }

    private lateinit var wordSizeSeekbar: SeekBar
    private lateinit var wordSizeLabel: TextView
    private lateinit var exampleSizeSeekbar: SeekBar
    private lateinit var exampleSizeLabel: TextView
    private lateinit var showExamplesSwitch: Switch
    private lateinit var densityGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wordSizeSeekbar = findViewById(R.id.word_size_seekbar)
        wordSizeLabel = findViewById(R.id.word_size_label)
        exampleSizeSeekbar = findViewById(R.id.example_size_seekbar)
        exampleSizeLabel = findViewById(R.id.example_size_label)
        showExamplesSwitch = findViewById(R.id.show_examples_switch)
        densityGroup = findViewById(R.id.density_group)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val wordSize = prefs.getInt(PREF_WORD_SIZE, 18)
        wordSizeSeekbar.progress = wordSize
        wordSizeLabel.text = "${wordSize}sp"

        val exampleSize = prefs.getInt(PREF_EXAMPLE_SIZE, 10)
        exampleSizeSeekbar.progress = exampleSize
        exampleSizeLabel.text = "${exampleSize}sp"

        val showExamples = prefs.getBoolean(PREF_SHOW_EXAMPLES, true)
        showExamplesSwitch.isChecked = showExamples

        val density = prefs.getInt(PREF_DENSITY, DENSITY_COMPACT)
        when (density) {
            DENSITY_COMPACT -> densityGroup.check(R.id.density_compact)
            DENSITY_NORMAL -> densityGroup.check(R.id.density_normal)
            DENSITY_SPACIOUS -> densityGroup.check(R.id.density_spacious)
        }
    }

    private fun setupListeners() {
        wordSizeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                wordSizeLabel.text = "${progress}sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        exampleSizeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                exampleSizeLabel.text = "${progress}sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        densityGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.density_compact -> {
                    wordSizeSeekbar.progress = 16
                    exampleSizeSeekbar.progress = 9
                }
                R.id.density_normal -> {
                    wordSizeSeekbar.progress = 22
                    exampleSizeSeekbar.progress = 12
                }
                R.id.density_spacious -> {
                    wordSizeSeekbar.progress = 32
                    exampleSizeSeekbar.progress = 14
                }
            }
        }

        findViewById<Button>(R.id.apply_button).setOnClickListener {
            saveSettings()
            updateWidgets()
        }
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(PREF_WORD_SIZE, wordSizeSeekbar.progress)
            putInt(PREF_EXAMPLE_SIZE, exampleSizeSeekbar.progress)
            putBoolean(PREF_SHOW_EXAMPLES, showExamplesSwitch.isChecked)

            val density = when (densityGroup.checkedRadioButtonId) {
                R.id.density_compact -> DENSITY_COMPACT
                R.id.density_normal -> DENSITY_NORMAL
                R.id.density_spacious -> DENSITY_SPACIOUS
                else -> DENSITY_COMPACT
            }
            putInt(PREF_DENSITY, density)

            apply()
        }
    }

    private fun updateWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, WordWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        val intent = Intent(this, WordWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        sendBroadcast(intent)
    }
}
