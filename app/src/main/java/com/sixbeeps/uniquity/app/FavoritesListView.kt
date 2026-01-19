package com.sixbeeps.uniquity.app

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.sixbeeps.uniquity.data.Favorite;

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val contentResolver: ContentResolver = getApplication<Application>().contentResolver

    companion object {
        // Re-define the provider constants here for use in the app module
        private const val AUTHORITY = "com.sixbeeps.uniquity.provider"
        private const val FAVORITES_TABLE_NAME = "favorites"
        val CONTENT_URI: Uri = "content://$AUTHORITY/$FAVORITES_TABLE_NAME".toUri()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            // Perform the query on a background thread
            val favoriteList = withContext(Dispatchers.IO) {
                queryFavorites()
            }
            _favorites.value = favoriteList
        }
    }

    private fun queryFavorites(): List<Favorite> {
        val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
        val favorites = mutableListOf<Favorite>()

        cursor?.use {
            // Make sure the cursor is not null and can be moved to the first entry. [1, 9]
            if (it.moveToFirst()) {
                // Get column indices once to improve performance. [1, 6]
                val idColumnIndex = it.getColumnIndexOrThrow("id")
                val codepointColumnIndex = it.getColumnIndexOrThrow("codepoint")

                do {
                    val id = it.getInt(idColumnIndex)
                    val codepoint = it.getString(codepointColumnIndex)
                    favorites.add(Favorite(id, codepoint))
                } while (it.moveToNext()) // Continue until all rows are processed. [1, 9]
            }
        }
        return favorites
    }
}
