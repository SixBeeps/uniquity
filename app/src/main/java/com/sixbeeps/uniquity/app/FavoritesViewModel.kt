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
    private val _favorites = MutableStateFlow<List<NamedCharacter>>(emptyList())
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

    private fun queryFavorites(): List<NamedCharacter> {
        val contentUri = "content://${UniquityContentProvider.AUTHORITY}/favorites".toUri()
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        val favorites = mutableListOf<NamedCharacter>()

        cursor?.use {
            if (it.moveToFirst()) {
                val codepointColumnIndex = it.getColumnIndexOrThrow("codepoint")

                do {
                    val codepoint = it.getString(codepointColumnIndex)
                    val name = NamedCharacter.resolveCharacterName(codepoint, contentResolver)
                    favorites.add(NamedCharacter(codepoint, name))
                } while (it.moveToNext())
            }
        }
        return favorites
    }
}
