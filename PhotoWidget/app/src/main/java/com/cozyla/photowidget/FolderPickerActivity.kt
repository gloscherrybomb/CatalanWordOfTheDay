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
