package com.sixbeeps.uniquity.app

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.sixbeeps.uniquity.UniquityContentProvider

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    class FavoriteData(val id: Int, val codepoint: String, val name: String?)

    private val _favorites = MutableStateFlow<List<FavoriteData>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val contentResolver: ContentResolver = getApplication<Application>().contentResolver

    fun loadFavorites() {
        viewModelScope.launch {
            val favoriteList = withContext(Dispatchers.IO) {
                queryFavorites()
            }
            _favorites.value = favoriteList
        }
    }

    fun removeFromFavorites(codepoint: String) {
        viewModelScope.launch {
            val contentUri = "content://${UniquityContentProvider.AUTHORITY}/favorites/$codepoint".toUri()
            val deleted = withContext(Dispatchers.IO) {
                contentResolver.delete(contentUri, null, null)
            }
            if (deleted > 0) loadFavorites()
        }
    }

    private fun queryFavorites(): List<FavoriteData> {
        val contentUri = "content://${UniquityContentProvider.AUTHORITY}/favorites".toUri()
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        val favorites = mutableListOf<FavoriteData>()

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndexOrThrow("id")
                val codepointColumnIndex = it.getColumnIndexOrThrow("codepoint")

                do {
                    val id = it.getInt(idColumnIndex)
                    val codepoint = it.getString(codepointColumnIndex)
                    favorites.add(FavoriteData(id, codepoint, queryDisplayName(codepoint)))
                } while (it.moveToNext())
            }
        }
        return favorites
    }

    private fun queryDisplayName(codepoint: String): String? {
        // First try canonical name
        val contentUri = "content://${UniquityContentProvider.AUTHORITY}/character/$codepoint".toUri()
        var cursor = contentResolver.query(contentUri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val nameColumnIndex = it.getColumnIndexOrThrow("name")
                return it.getString(nameColumnIndex)
            }
        }

        // Then try alias
        val aliasContentUri = "content://${UniquityContentProvider.AUTHORITY}/character/alias/$codepoint".toUri()
        cursor = contentResolver.query(aliasContentUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameColumnIndex = it.getColumnIndexOrThrow("alias")
                return it.getString(nameColumnIndex)
            }
        }

        return null
    }
}
