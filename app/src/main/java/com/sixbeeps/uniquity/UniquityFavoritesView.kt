package com.sixbeeps.uniquity

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Custom view for the favorites tab in the Uniquity keyboard
 */
class UniquityFavoritesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        
        val placeholderText = TextView(context)
        placeholderText.text = "VIEW GOES HERE"
        placeholderText.setTextColor(
            ContextCompat.getColor(context, R.color.uniquity_button_text_color)
        )
        placeholderText.textSize = 18f
        placeholderText.gravity = Gravity.CENTER
        
        addView(placeholderText)
    }
}