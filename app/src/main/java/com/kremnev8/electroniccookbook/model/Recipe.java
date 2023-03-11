package com.kremnev8.electroniccookbook.model;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "recipe",indices = @Index(value = {"id"},unique = true))
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "imageUri")
    public String imageUri;

    @Ignore
    public LiveData<List<RecipeStep>> steps;

    @Ignore
    public LiveData<List<RecipeIngredient>> ingredients;
}
