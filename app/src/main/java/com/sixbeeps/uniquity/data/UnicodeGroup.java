package com.sixbeeps.uniquity.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UnicodeGroup {
    @PrimaryKey
    @NonNull
    public String name = "";
}

