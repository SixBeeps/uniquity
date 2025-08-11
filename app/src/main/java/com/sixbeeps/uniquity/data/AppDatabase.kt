package com.sixbeeps.uniquity.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(
    entities = [UnicodeGroup::class, UnicodeCharacter::class, UnicodeCharacterAlias::class
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unicodeDao(): UnicodeDao

    companion object {
        var INSTANCE: AppDatabase? = null
        fun init(context: Context) {
            if (INSTANCE != null) {
                Log.w("AppDatabase", "Attempted to re-initialize database, ignoring")
                return
            }

            INSTANCE = databaseBuilder<AppDatabase>(context, AppDatabase::class.java, "unicode")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration(true)
                .createFromAsset("ucd.db")
                .build()
        }
    }
}
