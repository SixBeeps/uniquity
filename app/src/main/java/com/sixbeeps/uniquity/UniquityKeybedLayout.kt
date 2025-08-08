package com.sixbeeps.uniquity

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

class UniquityKeybedLayout @JvmOverloads constructor(context: Context?, height: Int = 10) :
    ScrollView(context) {
    /**
     * Container for key rows
     */
    @JvmField
    var root: LinearLayout = LinearLayout(context)

    /**
     * Primary constructor
     * @param context Application context
     * @param height Height of the keybed, in pixels
     */
    /**
     * Backwards compatibility constructor, defaults height to 10px
     */
    init {
        // Set up key container
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        addView(root)

        // Set the height of the view
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        )
    }
}
