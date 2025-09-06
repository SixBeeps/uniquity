package com.sixbeeps.uniquity

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("NotifyDataSetChanged")
class UniquityKeyAdapter(
    private val context: Context,
    private val keysPerRow: Int = 8,
    private var keys: List<UniquityKey> = emptyList(),
    private var listener: UniquityKeyboardView.UniquityKeyboardListener? = null
) : RecyclerView.Adapter<UniquityKeyAdapter.KeyRowViewHolder>() {

    class KeyRowViewHolder(val rowLayout: LinearLayout) : RecyclerView.ViewHolder(rowLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyRowViewHolder {
        val rowLayout = LinearLayout(context)
        rowLayout.orientation = LinearLayout.HORIZONTAL
        rowLayout.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return KeyRowViewHolder(rowLayout)
    }

    override fun onBindViewHolder(holder: KeyRowViewHolder, position: Int) {
        holder.rowLayout.removeAllViews()
        
        val startIndex = position * keysPerRow
        val endIndex = minOf(startIndex + keysPerRow, keys.size)
        
        for (i in startIndex until endIndex) {
            val key = keys[i]
            val keyButton = UniquityKeyView(context, key)
            
            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            keyButton.layoutParams = layoutParams
            
            if (listener != null) {
                UniquityListeners.bindAllListeners(keyButton, listener, key)
            }
            
            holder.rowLayout.addView(keyButton)
        }
    }

    override fun getItemCount(): Int {
        return (keys.size + keysPerRow - 1) / keysPerRow
    }

    fun updateKeys(newKeys: List<UniquityKey>) {
        keys = newKeys
        notifyDataSetChanged()
    }

    fun setListener(newListener: UniquityKeyboardView.UniquityKeyboardListener?) {
        listener = newListener
        notifyDataSetChanged()
    }
}