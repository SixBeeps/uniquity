package com.sixbeeps.uniquity

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.sixbeeps.uniquity.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Custom view for the favorites tab in the Uniquity keyboard
 */
class UniquityFavoritesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val viewScope = CoroutineScope(Dispatchers.Main + Job())
    private val favoritesKeybed: UniquityKeybedLayout
    private var listener: UniquityKeyboardView.UniquityKeyboardListener? = null

    init {
        orientation = VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background))
        
        val keybedHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            200f,
            resources.displayMetrics
        ).toInt()
        
        // Add keybed for displaying favorite characters
        favoritesKeybed = UniquityKeybedLayout(context, keybedHeight)
        favoritesKeybed.status = "Loading favorites..."
        favoritesKeybed.showStatus()
        addView(favoritesKeybed)
        
        // Load favorites when view is created
        loadFavorites()
    }

    fun setKeyboardListener(listener: UniquityKeyboardView.UniquityKeyboardListener?) {
        this.listener = listener
    }

    private fun loadFavorites() {
        viewScope.launch {
            try {
                val favorites = AppDatabase.INSTANCE?.unicodeDao()?.getFavorites()
                val favoriteKeys = mutableListOf<UniquityKey>()
                
                if (favorites != null && favorites.isNotEmpty()) {
                    for (favorite in favorites) {
                        // Get the character details for each favorite
                        val character = AppDatabase.INSTANCE?.unicodeDao()?.getUnicodeCharacter(favorite.codepoint)
                        if (character != null) {
                            val scalar = character.codepoint.toInt(16)
                            
                            // Handle surrogate pairs if necessary
                            val text: String = if (scalar > 0xFFFF) {
                                val high = (scalar - 0x10000) / 0x400 + 0xD800
                                val low = (scalar - 0x10000) % 0x400 + 0xDC00
                                String(Character.toChars(high)) + String(Character.toChars(low))
                            } else {
                                String(Character.toChars(scalar))
                            }
                            
                            val key = UniquityKey(text, text, character.codepoint)
                            favoriteKeys.add(key)
                        }
                    }
                    
                    // Display the favorite characters
                    favoritesKeybed.showKeys(favoriteKeys, listener)
                } else {
                    // No favorites found
                    favoritesKeybed.status = "No favorites yet"
                    favoritesKeybed.showStatus()
                }
            } catch (e: Exception) {
                // Handle error
                favoritesKeybed.status = "Error loading favorites"
                favoritesKeybed.showStatus()
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}