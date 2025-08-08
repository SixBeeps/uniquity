package com.sixbeeps.uniquity.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UnicodeDao {
    @get:Query("SELECT * FROM UnicodeGroup")
    val installedUnicodeGroups: MutableList<UnicodeGroup>?

    @Query("SELECT * FROM UnicodeGroup WHERE name = :name")
    fun getUnicodeGroup(name: String?): UnicodeGroup?

    @Query("SELECT * FROM UnicodeCharacter WHERE groupName = :group")
    fun getUnicodeCharacters(group: String?): MutableList<UnicodeCharacter>?

    @Query("SELECT * FROM UnicodeCharacter WHERE codepoint = :codepoint")
    fun getUnicodeCharacter(codepoint: String?): UnicodeCharacter?

    @Query("SELECT * FROM UnicodeCharacterAlias WHERE codepoint = :codepoint")
    fun getUnicodeCharacterAliases(codepoint: String?): MutableList<UnicodeCharacterAlias>?
}
