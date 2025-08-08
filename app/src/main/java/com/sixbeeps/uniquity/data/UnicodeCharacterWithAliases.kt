package com.sixbeeps.uniquity.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UnicodeCharacterWithAliases {
    @Embedded public UnicodeCharacter character;
    @Relation(
            parentColumn = "codepoint",
            entityColumn = "id"
    )
    public List<UnicodeCharacterAlias> aliases;
}
