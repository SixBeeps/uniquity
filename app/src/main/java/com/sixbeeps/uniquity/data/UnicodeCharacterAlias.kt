package com.sixbeeps.uniquity.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UnicodeCharacterAlias {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String codepoint = "";

    @NonNull
    public String alias = "";
}

