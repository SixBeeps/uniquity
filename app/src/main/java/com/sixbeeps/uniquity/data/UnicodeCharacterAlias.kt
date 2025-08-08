package com.sixbeeps.uniquity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UnicodeCharacterAlias {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @JvmField
    var codepoint: String = ""

    @JvmField
    var alias: String = ""
}

