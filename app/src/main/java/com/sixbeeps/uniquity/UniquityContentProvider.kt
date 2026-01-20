package com.sixbeeps.uniquity

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.sixbeeps.uniquity.data.AppDatabase
import androidx.core.net.toUri

class UniquityContentProvider : ContentProvider() {
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        val code = MATCHER.match(uri)
        val ctx = context ?: return 0
        return when (code) {
            CODE_FAVORITES_ID -> {
                val id = uri.lastPathSegment?.toInt() ?: return 0
                AppDatabase.getDatabase(ctx).unicodeDao().removeFromFavoritesSync(id)
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        val code = MATCHER.match(uri)
        val ctx = context ?: return null
        return when (code) {
            CODE_FAVORITES_ID -> {
                val codepoint = uri.lastPathSegment ?: return null
                val id = AppDatabase.getDatabase(ctx).unicodeDao().addToFavoritesSync(codepoint)
                if (id >= 0) {
                    "content://$AUTHORITY/favorites/$codepoint".toUri()
                } else {
                    null
                }
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        val code = MATCHER.match(uri)
        val ctx = context ?: return null
        return when (code) {
            CODE_FAVORITES -> {
                val cursor = AppDatabase.getDatabase(ctx).unicodeDao().getFavoritesSync()
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            CODE_CHARACTER_ID -> {
                val codepoint = uri.lastPathSegment ?: return null
                val cursor = AppDatabase.getDatabase(ctx).unicodeDao().getUnicodeCharacterSync(codepoint)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            CODE_CHARACTER_ALIAS_ID -> {
                val codepoint = uri.lastPathSegment ?: return null
                val cursor = AppDatabase.getDatabase(ctx).unicodeDao().getUnicodeCharacterAliasesSync(codepoint)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            CODE_CHARACTER_SEARCH -> {
                val query = uri.lastPathSegment ?: return null
                val cursor = AppDatabase.getDatabase(ctx).unicodeDao().searchUnicodeCharactersSync(query)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            else -> {
                throw IllegalArgumentException("Unknown code $code with URI: $uri")
            }
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }

    companion object {
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
        const val AUTHORITY = "com.sixbeeps.uniquity.provider"
        const val CODE_FAVORITES = 1
        const val CODE_FAVORITES_ID = 2
        const val CODE_CHARACTER_ID = 3
        const val CODE_CHARACTER_ALIAS_ID = 4
        const val CODE_CHARACTER_SEARCH = 5

        init {
            MATCHER.addURI(AUTHORITY, "favorites", CODE_FAVORITES)
            MATCHER.addURI(AUTHORITY, "favorites/*", CODE_FAVORITES_ID)
            MATCHER.addURI(AUTHORITY, "alias/*", CODE_CHARACTER_ALIAS_ID)
            MATCHER.addURI(AUTHORITY, "character/*", CODE_CHARACTER_ID)
            MATCHER.addURI(AUTHORITY, "search/character/", CODE_CHARACTER_SEARCH)
            MATCHER.addURI(AUTHORITY, "search/character/*", CODE_CHARACTER_SEARCH)
        }
    }
}
