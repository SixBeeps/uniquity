package com.sixbeeps.uniquity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UnicodeCharacter {
    @JvmField
    @PrimaryKey
    var codepoint: String = ""

    @JvmField
    var name: String? = null

    @JvmField
    var groupName: String = ""
}

