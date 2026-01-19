package com.cozyla.catalanword

import android.os.Bundle
import android.widget.TextView
import android.app.Activity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Add the Catalan Word Widget to your home screen!\n\nLong-press your home screen → Widgets → Catalan Word Widget"
            textSize = 18f
            setPadding(48, 48, 48, 48)
        }
        setContentView(textView)
    }
}
