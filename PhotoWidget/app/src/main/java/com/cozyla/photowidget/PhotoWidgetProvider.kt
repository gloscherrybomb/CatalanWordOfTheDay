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
