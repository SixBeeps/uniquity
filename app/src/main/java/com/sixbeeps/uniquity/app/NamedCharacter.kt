package com.sixbeeps.uniquity.app

import android.content.ContentResolver
import androidx.core.net.toUri
import com.sixbeeps.uniquity.UniquityContentProvider

class NamedCharacter(val codepoint: String, val name: String?, val id: Int? = null) {
    companion object {
        fun resolveCharacterName(codepoint: String, contentResolver: ContentResolver): String? {
            // First try canonical name
            val contentUri =
                "content://${UniquityContentProvider.AUTHORITY}/character/$codepoint".toUri()
            var cursor = contentResolver.query(contentUri, null, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    val nameColumnIndex = it.getColumnIndexOrThrow("name")
                    return it.getString(nameColumnIndex)
                }
            }

            // Then try alias
            val aliasContentUri =
                "content://${UniquityContentProvider.AUTHORITY}/alias/$codepoint".toUri()
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
}