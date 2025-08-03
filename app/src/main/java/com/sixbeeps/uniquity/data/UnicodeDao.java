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

    @Query("SELECT * FROM UnicodeCharacter WHERE \"group\" = :group")
    List<UnicodeCharacter> getUnicodeCharacters(String group);
}
