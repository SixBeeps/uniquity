package com.sixbeeps.uniquity.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(
    entity = UnicodeCharacter::class,
    parentColumns = arrayOf("codepoint"),
    childColumns = arrayOf("codepoint"),
    onDelete = ForeignKey.CASCADE)]
)
class Favorite {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @JvmField
    var codepoint: String? = null
}

