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
