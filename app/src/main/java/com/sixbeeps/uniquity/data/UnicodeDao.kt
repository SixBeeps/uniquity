package com.sixbeeps.uniquity.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UnicodeDao {
    @Query("SELECT * FROM UnicodeGroup")
    suspend fun getInstalledUnicodeGroups(): List<UnicodeGroup>?

    @Query("SELECT * FROM UnicodeGroup WHERE name = :name")
    suspend fun getUnicodeGroup(name: String?): UnicodeGroup?

    @Query("SELECT * FROM UnicodeCharacter WHERE groupName = :group")
    suspend fun getUnicodeCharacters(group: String?): List<UnicodeCharacter>?

    @Query("SELECT * FROM UnicodeCharacter WHERE codepoint = :codepoint")
    suspend fun getUnicodeCharacter(codepoint: String?): UnicodeCharacter?

    @Query("SELECT * FROM UnicodeCharacterAlias WHERE codepoint = :codepoint")
    suspend fun getUnicodeCharacterAliases(codepoint: String?): List<UnicodeCharacterAlias>?

    @Query("SELECT * FROM Favorite ORDER BY id ASC")
    suspend fun getFavorites(): List<Favorite>?

    @Query("INSERT OR IGNORE INTO Favorite (codepoint) VALUES (:codepoint)")
    suspend fun addToFavorites(codepoint: String)

    @Query("SELECT COUNT(*) FROM Favorite WHERE codepoint = :codepoint")
    suspend fun isFavorite(codepoint: String): Int
}
