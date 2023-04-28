package com.kremnev8.electroniccookbook.components.profile.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile",indices = @Index(value = {"id"},unique = true))
public class Profile {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profileImage")
    public String profileImageUrl;

    @ColumnInfo(name = "passwordRequired")
    public boolean passwordRequired;

    @ColumnInfo(name = "passwordHash")
    public String passwordHash;

}
