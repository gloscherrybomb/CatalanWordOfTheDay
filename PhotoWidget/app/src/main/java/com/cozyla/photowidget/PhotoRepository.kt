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
