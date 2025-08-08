package com.sixbeeps.uniquity.data

import androidx.room.Embedded
import androidx.room.Relation

class UnicodeGroupWithCharacters {
    @Embedded
    var group: UnicodeGroup? = null

    @Relation(parentColumn = "name", entityColumn = "codepoint")
    var characters: MutableList<UnicodeCharacter?>? = null
}
