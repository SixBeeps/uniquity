package com.sixbeeps.uniquity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UnicodeGroup {
    @JvmField
    @PrimaryKey
    var name: String = ""
}

