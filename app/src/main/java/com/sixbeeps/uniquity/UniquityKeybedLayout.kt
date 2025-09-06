package com.sixbeeps.uniquity

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UniquityKeybedLayout @JvmOverloads constructor(private var context: Context, height: Int = 10) :
    LinearLayout(context) {
    
    /**
     * RecyclerView for displaying keys
     */
    private val recyclerView: RecyclerView = RecyclerView(context)
    
    /**
     * Adapter for the RecyclerView
     */
    private val keyAdapter = UniquityKeyAdapter(context)
    
    /**
     * Container for status/loading views
     */
    @JvmField
    var root: LinearLayout = LinearLayout(context)

    /**
     * Text to be displayed if there are no keys
     */
    var status: String? = null

    /**
     * Primary constructor
     * @param context Application context
     * @param height Height of the keybed, in pixels
     */
    /**
     * Backwards compatibility constructor, defaults height to 10px
     */
    init {
        orientation = VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background))
        layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            height
        )
        
        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = keyAdapter
        recyclerView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(recyclerView)
        
        // Set up status container (initially hidden)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        root.visibility = GONE
        addView(root)
    }

    fun showStatus() {
        recyclerView.visibility = GONE
        root.visibility = VISIBLE
        root.removeAllViews()
        
        val paddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
        ).toInt()

        val layout = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        val statusTextView = TextView(context)
        statusTextView.text = status?: resources.getString(R.string.loading)
        statusTextView.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.uniquity_button_text_color
            )
        )
        statusTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        statusTextView.textAlignment = TEXT_ALIGNMENT_CENTER
        statusTextView.layoutParams = layout
        root.addView(statusTextView)
    }

    fun showLoading() {
        recyclerView.visibility = GONE
        root.visibility = VISIBLE
        root.removeAllViews()
        
        val paddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
        ).toInt()

        val layout = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        val throbber = ProgressBar(context)
        throbber.layoutParams = layout
        throbber.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        root.addView(throbber)
    }
    
    fun showKeys(keys: List<UniquityKey>, listener: UniquityKeyboardView.UniquityKeyboardListener?) {
        root.visibility = GONE
        recyclerView.visibility = VISIBLE
        keyAdapter.updateKeys(keys)
        keyAdapter.setListener(listener)
    }
}
