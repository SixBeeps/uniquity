package com.sixbeeps.uniquity.data

import androidx.room.Embedded
import androidx.room.Relation

class UnicodeCharacterWithAliases {
    @Embedded
    var character: UnicodeCharacter? = null

    @Relation(parentColumn = "codepoint", entityColumn = "id")
    var aliases: MutableList<UnicodeCharacterAlias?>? = null
}
