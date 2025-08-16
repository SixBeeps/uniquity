package com.sixbeeps.uniquity

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ScrollView
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.children

class UniquityQwertyKeybedLayout @JvmOverloads constructor(private var context: Context, height: Int = 10) :
    ScrollView(context) {
    /**
     * Container for key rows
     */
    @JvmField
    var root: LinearLayout = LinearLayout(context)

    private var isShiftPressed = false
    private var keyViews: MutableList<UniquityKeyView> = ArrayList()
    private var listener: UniquityKeyboardView.UniquityKeyboardListener? = null

    /**
     * Primary constructor
     * @param context Application context
     * @param height Height of the keybed, in pixels
     */
    init {
        // Set the appearance of the view
        setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background))
        layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            height
        )

        // Set up key container
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(root)

        refreshKeys()
    }

    private fun refreshKeys() {
        // Reset root container
        root.removeAllViews()
        keyViews.clear()

        // Add all keys to the key container
        val keyLayout = if (isShiftPressed) shiftedKeyLayout else unshiftedKeyLayout
        for (row in keyLayout) {
            // Create a row of keys
            val rowLayout = LinearLayout(context)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                1f
            )
            root.addView(rowLayout)

            // On the fourth row, add a shift key
            if (root.children.count() == 4) {
                val shiftKey = Button(context)
                shiftKey.setText(R.string.key_shift)
                shiftKey.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT
                )
                shiftKey.setBackgroundColor(ContextCompat.getColor(context, if (isShiftPressed) R.color.uniquity_button_special_background else R.color.uniquity_command_strip_background))
                shiftKey.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color))
                shiftKey.setOnClickListener {
                    isShiftPressed = !isShiftPressed
                    refreshKeys()
                }
                rowLayout.addView(shiftKey)
            }

            // Add all row keys to row
            for (letter in row) {
                val key = UniquityKey(letter)
                val keyButton = UniquityKeyView(context, key)
                keyButton.layoutParams = LayoutParams(
                    0,
                    LayoutParams.WRAP_CONTENT,
                    1f
                )
                rowLayout.addView(keyButton)
                keyViews.add(keyButton)
            }
        }

        // Bind all key listeners if there is a listener
        bindListeners(listener)
    }

    fun bindListeners(listener: UniquityKeyboardView.UniquityKeyboardListener?) {
        this.listener = listener
        if (listener == null) return

        for (view in keyViews) {
            if (view.key == null) return
            UniquityListeners.bindAllListeners(view, listener, view.key)
        }
    }

    companion object {
        private val shiftedKeyLayout = arrayOf(
            arrayOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")"),
            arrayOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            arrayOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            arrayOf("Z", "X", "C", "V", "B", "N", "M")
        )

        private val unshiftedKeyLayout = arrayOf(
            arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            arrayOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            arrayOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
            arrayOf("z", "x", "c", "v", "b", "n", "m")
        )
    }
}
