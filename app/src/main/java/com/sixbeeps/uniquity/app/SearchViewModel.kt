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
import kotlinx.coroutines.Job

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _results = MutableStateFlow<List<NamedCharacter>>(emptyList())

    val results = _results.asStateFlow()

    private val contentResolver: ContentResolver = getApplication<Application>().contentResolver
    private var searchJob: Job? = null
    val loading = MutableStateFlow(false)

    fun search(query: String) {
        if (searchJob?.isActive == true) {
            searchJob!!.cancel()
        }

        _results.value = emptyList()

        if (query.isEmpty()) return

        loading.value = true

        searchJob = viewModelScope.launch {
            val resultsList = withContext(Dispatchers.IO) {
                queryCharacterSearch(query)
            }
            println("got ${resultsList.size} results")
            _results.value = resultsList
        }

        searchJob?.invokeOnCompletion {
            searchJob = null
            loading.value = false
        }
    }

    private fun queryCharacterSearch(query: String): List<NamedCharacter> {
        val contentUri = "content://${UniquityContentProvider.AUTHORITY}/search/character/$query".toUri()
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        val results = mutableListOf<NamedCharacter>()

        cursor?.use {
            if (it.moveToFirst()) {
                val codepointColumnIndex = it.getColumnIndexOrThrow("codepoint")

                do {
                    val codepoint = it.getString(codepointColumnIndex)
                    val name = NamedCharacter.resolveCharacterName(codepoint, contentResolver)
                    results.add(NamedCharacter(codepoint, name))
                } while (it.moveToNext())
            }
        }
        return results
    }
}