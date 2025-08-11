package com.sixbeeps.uniquity.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UnicodeDao {
    @Query("SELECT * FROM UnicodeGroup")
    suspend fun getInstalledUnicodeGroups(): List<UnicodeGroup>?

    @Query("SELECT * FROM UnicodeGroup WHERE name = :name")
    fun getUnicodeGroup(name: String?): UnicodeGroup?

    @Query("SELECT * FROM UnicodeCharacter WHERE groupName = :group")
    fun getUnicodeCharacters(group: String?): List<UnicodeCharacter>?

    @Query("SELECT * FROM UnicodeCharacter WHERE codepoint = :codepoint")
    fun getUnicodeCharacter(codepoint: String?): UnicodeCharacter?

    @Query("SELECT * FROM UnicodeCharacterAlias WHERE codepoint = :codepoint")
    fun getUnicodeCharacterAliases(codepoint: String?): List<UnicodeCharacterAlias>?
}
