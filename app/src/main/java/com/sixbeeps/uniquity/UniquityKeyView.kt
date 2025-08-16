package com.sixbeeps.uniquity

import android.content.Context
import android.text.Html
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

class UniquityKeyView @JvmOverloads constructor(context: Context, val key: UniquityKey? = null) : Button(context) {
    /**
     * Primary constructor
     * @param context Application context
     * @param key Key data
     */
    init {
        // Update the button's appearance
        setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color))
        setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background))
        layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )

        // Set the button text if there is key data
        if (key != null) {
            text = if (key.caption != null && !key.caption!!.isEmpty()) {
                Html.fromHtml(key.displayString + "<br /><small><small><font color=\"gray\">" + key.caption + "</font></small></small>")
            } else {
                key.displayString
            }
        }
    }
}
