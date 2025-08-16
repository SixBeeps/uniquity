package com.sixbeeps.uniquity

import android.content.Context
import android.os.Vibrator
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sixbeeps.uniquity.data.AppDatabase
import com.sixbeeps.uniquity.data.UnicodeGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The main view for the Uniquity Keyboard
 */
class UniquityKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    /**
     * An interface with methods to be implemented for keyboard events
     */
    interface UniquityKeyboardListener {
        /**
         * Called when a key is pressed
         * @param contents The characters to be typed in the input field
         */
        fun onKey(contents: String?)

        /**
         * Called when the backspace/delete key is pressed
         */
        fun onDelete()

        /**
         * Called when the enter key is pressed
         */
        fun onEnter()
    }

    var listener: UniquityKeyboardListener? = null
    private val viewScope = CoroutineScope(Dispatchers.Main + Job())

    private var tabStripContentLayout: LinearLayout
    private var keybed: UniquityKeybedLayout
    private var commandStripLayout: LinearLayout

    private var allUnicodeGroups: MutableList<UnicodeGroup>? = null
    private var keys: MutableList<UniquityKey>? = null
    private var currentSelectedGroup: UnicodeGroup? = null

    init {
        AppDatabase.init(context)

        orientation = VERTICAL

        val fixedHeightInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            KEYBOARD_HEIGHT_DP.toFloat(),
            resources.displayMetrics
        ).toInt()

        // Tab Strip
        val tabStripScrollView = HorizontalScrollView(context)
        tabStripScrollView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        tabStripContentLayout = LinearLayout(context)
        tabStripContentLayout.orientation = HORIZONTAL
        tabStripContentLayout.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        tabStripScrollView.addView(tabStripContentLayout)
        addView(tabStripScrollView)

        // Keybed
        keybed = UniquityKeybedLayout(context, fixedHeightInPx)
        addView(keybed)

        // Separator
        val separator = View(context)
        val separatorHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics
        ).toInt()
        val separatorParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            separatorHeightPx
        )
        separator.layoutParams = separatorParams
        separator.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_separator_color
            )
        )
        addView(separator)

        // Command Strip
        commandStripLayout = LinearLayout(context)
        commandStripLayout.orientation = HORIZONTAL
        commandStripLayout.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        commandStripLayout.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_command_strip_background
            )
        )
        addView(commandStripLayout)

        // Load Unicode groups from the database and store them for later
        viewScope.launch {
            fetchUnicodeGroups()
            fetchUnicodeCharsInSelectedGroup()
        }

        // Initialize vibrator
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Draw all the UI elements
        refreshTabStrip()
    }

    /**
     * Loads Unicode groups from the database and stores them in `allUnicodeGroups`
     */
    suspend fun fetchUnicodeGroups() {
        // Clear existing group data
        allUnicodeGroups?.clear()
        allUnicodeGroups = null
        currentSelectedGroup = null
        refreshTabStrip()

        // Get Unicode groups from the database and store them
        val groups = AppDatabase.INSTANCE?.unicodeDao()?.getInstalledUnicodeGroups()
        if (groups != null) {
            allUnicodeGroups = ArrayList()
            allUnicodeGroups!!.addAll(groups)
            currentSelectedGroup = allUnicodeGroups!!.firstOrNull()
            refreshTabStrip()
        }
    }

    /**
     * Updates the main key grid with characters from the currently selected Unicode group.
     */
    suspend fun fetchUnicodeCharsInSelectedGroup() {
        // Clear existing key data
        keys?.clear()
        if (currentSelectedGroup == null) {
            keys = ArrayList()
            return
        } else {
            keys = null
        }

        refreshKeysLayout()
        refreshCommandStrip()

        // Load characters from the database
        val characters = AppDatabase.INSTANCE?.unicodeDao()?.getUnicodeCharacters(currentSelectedGroup!!.name)
        keys = ArrayList()
        if (characters != null) {
            for (character in characters) {
                val scalar = character.codepoint.toInt(16)

                // Handle surrogate pairs if necessary
                val text: String?
                if (scalar > 0xFFFF) {
                    val high = (scalar - 0x10000) / 0x400 + 0xD800
                    val low = (scalar - 0x10000) % 0x400 + 0xDC00
                    text = String(Character.toChars(high)) + String(Character.toChars(low))
                } else {
                    text = String(Character.toChars(scalar))
                }

                val key = UniquityKey(text, text, character.codepoint)
                keys!!.add(key)
            }
        }
        refreshKeysLayout()
        refreshCommandStrip()
    }

    /**
     * Refreshes the tab strip with Unicode group names.
     */
    private fun refreshTabStrip() {
        tabStripContentLayout.removeAllViews()
        val context = getContext()

        // If the groups are still loading, display some loading text
        if (allUnicodeGroups == null) {
            val loadingTextView = TextView(context)
            loadingTextView.setText(R.string.loading)
            loadingTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.uniquity_button_text_color
                )
            )
            val tvParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            val paddingPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
            ).toInt()
            loadingTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            loadingTextView.layoutParams = tvParams
            tabStripContentLayout.addView(loadingTextView)
            return
        }

        // If there are no groups, display a message
        if (allUnicodeGroups!!.isEmpty()) {
            val noGroupsTextView = TextView(context)
            noGroupsTextView.setText(R.string.warning_no_group)
            noGroupsTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.uniquity_button_text_color
                )
            )
            val tvParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            val paddingPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
            ).toInt()
            noGroupsTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            noGroupsTextView.layoutParams = tvParams
            tabStripContentLayout.addView(noGroupsTextView)
            return
        }

        // Otherwise, create tabs for each group
        for (group in allUnicodeGroups) {
            val tabButton = Button(context)
            tabButton.text = group.name.replace("_".toRegex(), " ")
            tabButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.uniquity_button_text_color
                )
            )
            if (group == currentSelectedGroup) {
                tabButton.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.uniquity_button_background
                    )
                )
            } else {
                tabButton.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.uniquity_command_strip_background
                    )
                )
            }

            val tabParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )

            val marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics
            ).toInt()
            tabParams.setMargins(marginPx, 0, marginPx, 0)
            tabButton.layoutParams = tabParams

            val paddingPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
            ).toInt()
            tabButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

            tabButton.setOnClickListener { v: View? ->
                currentSelectedGroup = group

                // Reset the background color of each tab and highlight the selected one
                for (button in tabStripContentLayout.touchables) {
                    button.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.uniquity_command_strip_background
                        )
                    )
                }
                v!!.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.uniquity_button_background
                    )
                )

                // Update the key grid
                viewScope.launch {
                    fetchUnicodeCharsInSelectedGroup()
                }
            }
            tabStripContentLayout.addView(tabButton)
        }
    }

    /**
     * Refreshes the command strip
     */
    private fun refreshCommandStrip() {
        commandStripLayout.removeAllViews()
        val context = getContext()

        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics
        ).toInt()

        // Add space bar
        val spaceKey = UniquityKey(" ")
        val spaceButton = Button(context)
        spaceButton.text = "␣"
        spaceButton.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_text_color
            )
        )
        spaceButton.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_background
            )
        )

        val spaceParams = LayoutParams(
            0,
            LayoutParams.WRAP_CONTENT,
            2.0f
        )
        spaceParams.setMargins(marginPx, marginPx, marginPx, marginPx)
        spaceButton.layoutParams = spaceParams

        UniquityListeners.bindAllListeners(spaceButton, this.listener, spaceKey);
        commandStripLayout.addView(spaceButton)

        // Add delete key
        val deleteKey = UniquityKey(UniquityKey.KeyType.DELETE)
        val deleteButton = Button(context)
        deleteButton.text = deleteKey.displayString
        deleteButton.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_text_color
            )
        )
        deleteButton.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_background
            )
        )

        val deleteParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        deleteParams.setMargins(marginPx, marginPx, marginPx, marginPx)
        deleteButton.layoutParams = deleteParams

        UniquityListeners.bindAllListeners(deleteButton, this.listener, deleteKey);
        commandStripLayout.addView(deleteButton)

        // Add enter key
        val enterKey = UniquityKey(UniquityKey.KeyType.ENTER)
        val enterButton = Button(context)
        enterButton.text = enterKey.displayString
        enterButton.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_text_color
            )
        )

        enterButton.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_special_background
            )
        )

        val enterParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        enterParams.setMargins(marginPx, marginPx, marginPx, marginPx)
        enterButton.layoutParams = enterParams

        UniquityListeners.bindAllListeners(enterButton, this.listener, enterKey);
        commandStripLayout.addView(enterButton)
    }

    /**
     * Refreshes the layout of the main character keys.
     */
    fun refreshKeysLayout() {
        keybed.root.removeAllViews()
        val context = getContext()
        var currentRow: LinearLayout? = null
        val KEYS_PER_ROW = 8

        // If the characters are still loading, display some loading text
        if (keys == null) {
            keybed.status = resources.getString(R.string.loading)
            keybed.showStatus()
            keybed.root.requestLayout()
            return
        }

        // If selected group has no characters, display a message
        else if (keys!!.isEmpty() && currentSelectedGroup != null) {
            keybed.status = resources.getString(R.string.warning_no_char_in_group)
            keybed.showStatus()
            keybed.root.requestLayout()
            return
        }

        // Otherwise, create buttons for each character
        for (i in keys!!.indices) {
            if (i % KEYS_PER_ROW == 0) {
                currentRow = LinearLayout(context)
                currentRow.orientation = HORIZONTAL
                currentRow.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                keybed.root.addView(currentRow)
            }

            val key = keys!![i]
            val button = UniquityKeyView(context, key)

            if (this.listener != null) {
                button.setOnClickListener(UniquityListeners.ClickListener(this.listener, key))
            }
            currentRow!!.addView(button)
        }
        keybed.root.requestLayout()
    }

    /**
     * Sets the listener for keyboard events.
     * @param listener The listener to be notified of keyboard events.
     */
    fun setUniquityKeyboardListener(listener: UniquityKeyboardListener?) {
        this.listener = listener
        refreshTabStrip()
        viewScope.launch {
            fetchUnicodeCharsInSelectedGroup()
        }
    }

    companion object {
        private const val KEYBOARD_HEIGHT_DP = 200
        var vibrator: Vibrator? = null;
    }
}
