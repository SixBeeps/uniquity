package com.sixbeeps.uniquity.data

import android.database.Cursor
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

    @Query("SELECT * FROM UnicodeCharacter WHERE codepoint = :codepoint")
    fun getUnicodeCharacterSync(codepoint: String?): Cursor

    @Query("SELECT * FROM UnicodeCharacterAlias WHERE codepoint = :codepoint")
    suspend fun getUnicodeCharacterAliases(codepoint: String?): List<UnicodeCharacterAlias>?

    @Query("SELECT * FROM UnicodeCharacterAlias WHERE codepoint = :codepoint")
    fun getUnicodeCharacterAliasesSync(codepoint: String?): Cursor

    // TODO - Use Fts4 to search for characters instead
    @Query("SELECT * FROM UnicodeCharacter WHERE name LIKE :name ORDER BY name LIMIT 250")
    suspend fun searchUnicodeCharacters(name: String?): List<UnicodeCharacter>?

    @Query("SELECT * FROM UnicodeCharacter WHERE LOWER(name) LIKE LOWER(\'%\' || :name || \'%\') ORDER BY name LIMIT 250")
    fun searchUnicodeCharactersSync(name: String?): Cursor

    @Query("SELECT * FROM Favorite ORDER BY id ASC")
    suspend fun getFavorites(): List<Favorite>?

    @Query("SELECT * FROM Favorite ORDER BY id ASC")
    fun getFavoritesSync(): Cursor

    @Query("INSERT INTO Favorite (codepoint) VALUES (:codepoint)")
    suspend fun addToFavorites(codepoint: String)

    @Query("INSERT INTO Favorite (codepoint) VALUES (:codepoint)")
    fun addToFavoritesSync(codepoint: String): Long

    @Query("DELETE FROM Favorite WHERE codepoint = :codepoint")
    suspend fun removeFromFavorites(codepoint: String)

    @Query("DELETE FROM Favorite WHERE id = :id")
    fun removeFromFavoritesSync(id: Int): Int

    @Query("SELECT COUNT(*) FROM Favorite WHERE codepoint = :codepoint")
    suspend fun isFavorite(codepoint: String): Int
}
