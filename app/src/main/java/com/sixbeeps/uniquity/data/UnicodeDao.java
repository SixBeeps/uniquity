package com.sixbeeps.uniquity.data;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UnicodeDao {
    @Query("SELECT * FROM UnicodeGroup")
    List<UnicodeGroup> getInstalledUnicodeGroups();

    @Query("SELECT * FROM UnicodeGroup WHERE name = :name")
    UnicodeGroup getUnicodeGroup(String name);

    @Query("SELECT * FROM UnicodeCharacter WHERE groupName = :group")
    List<UnicodeCharacter> getUnicodeCharacters(String group);

    @Query("SELECT * FROM UnicodeCharacter WHERE codepoint = :codepoint")
    UnicodeCharacter getUnicodeCharacter(String codepoint);

    @Query("SELECT * FROM UnicodeCharacterAlias WHERE codepoint = :codepoint")
    List<UnicodeCharacterAlias> getUnicodeCharacterAliases(String codepoint);
}
