package com.kremnev8.electroniccookbook.recipe.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipeStep",
        indices = @Index(value = {"id"},unique = true),
        foreignKeys = {@ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipe",
                onDelete = ForeignKey.CASCADE)
        })
public class RecipeStep {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipe", index = true)
    public int recipe;

    @ColumnInfo(name = "name", defaultValue = "")
    public String name;

    @ColumnInfo(name = "description", defaultValue = "")
    public String description;

    @ColumnInfo(name = "mediaUri", defaultValue = "")
    public String mediaUri;

    @ColumnInfo(name = "isOptional", defaultValue = "false")
    public boolean isOptional;

    @ColumnInfo(name = "timerEnabled", defaultValue = "false")
    public boolean timerEnabled;

    @ColumnInfo(name = "timer")
    public long timer;

}
