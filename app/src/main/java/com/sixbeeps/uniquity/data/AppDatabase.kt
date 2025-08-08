package com.sixbeeps.uniquity.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {
        UnicodeGroup.class,
        UnicodeCharacter.class,
        UnicodeCharacterAlias.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase INSTANCE = null;
    public abstract UnicodeDao unicodeDao();

    public static void init(Context context) {
        if (INSTANCE != null) {
            Log.w("AppDatabase", "Attempted to re-initialize database, ignoring");
            return;
        }

        INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "unicode")
                .allowMainThreadQueries()
                .createFromAsset("ucd.db")
                .build();
    }
}
