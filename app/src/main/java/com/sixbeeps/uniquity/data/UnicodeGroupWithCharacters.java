package com.sixbeeps.uniquity.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UnicodeGroupWithCharacters {
    @Embedded public UnicodeGroup group;
    @Relation(
            parentColumn = "name",
            entityColumn = "codepoint"
    )
    public List<UnicodeCharacter> characters;
}
