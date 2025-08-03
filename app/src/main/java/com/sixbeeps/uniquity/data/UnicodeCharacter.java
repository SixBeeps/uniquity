package com.sixbeeps.uniquity.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class UnicodeCharacter {
    @PrimaryKey
    @NonNull
    public String character = "";
    public String byteRepresentation;
    public String group;
}

