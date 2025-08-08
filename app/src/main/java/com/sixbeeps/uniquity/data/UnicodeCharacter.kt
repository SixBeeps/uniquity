package com.sixbeeps.uniquity.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UnicodeCharacter {
    @PrimaryKey
    @NonNull
    public String codepoint = "";

    public String name = "";

    @NonNull
    public String groupName = "";
}

