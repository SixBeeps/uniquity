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

    @Query("""
        SELECT DISTINCT c.* FROM UnicodeCharacter c 
        LEFT JOIN UnicodeCharacterAlias a ON c.codepoint = a.codepoint 
        WHERE c.name LIKE '%' || :searchQuery || '%' 
        OR a.alias LIKE '%' || :searchQuery || '%'
        ORDER BY c.codepoint
        LIMIT :limit
    """)
    suspend fun searchUnicodeCharacters(searchQuery: String, limit: Int = 100): List<UnicodeCharacter>?
}
