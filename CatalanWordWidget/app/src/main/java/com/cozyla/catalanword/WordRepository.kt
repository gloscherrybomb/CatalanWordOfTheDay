package com.cozyla.catalanword

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class WordRepository(private val context: Context) {

    companion object {
        private const val TAG = "WordRepository"
        private val FALLBACK_WORD = Word(
            word = "Hola",
            translation = "Hello",
            example = "Hola, com estàs?",
            exampleTranslation = "Hello, how are you?"
        )
    }

    private var words: List<Word>? = null

    fun getTodaysWord(): Word {
        return try {
            val wordList = getWords()
            if (wordList.isEmpty()) {
                Log.e(TAG, "Word list is empty")
                return FALLBACK_WORD
            }
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val index = (dayOfYear - 1) % wordList.size
            wordList[index]
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today's word", e)
            FALLBACK_WORD
        }
    }

    private fun getWords(): List<Word> {
        if (words == null) {
            try {
                val json = context.assets.open("words.json")
                    .bufferedReader()
                    .use { it.readText() }
                val type = object : TypeToken<List<Word>>() {}.type
                words = Gson().fromJson(json, type) ?: emptyList()
                Log.d(TAG, "Loaded ${words?.size ?: 0} words")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading words.json", e)
                words = emptyList()
            }
        }
        return words ?: emptyList()
    }
}
