package com.kremnev8.electroniccookbook.recipe.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;

@Entity(tableName = "recipeIngredient",
        indices = @Index(value = {"id"}, unique = true),
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "id",
                        childColumns = "recipe",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Ingredient.class,
                        parentColumns = "id",
                        childColumns = "ingredient",
                        onDelete = ForeignKey.CASCADE)
        })
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipe", index = true)
    public int recipe;

    @ColumnInfo(name = "ingredient", index = true)
    public int ingredient;

    @ColumnInfo(name = "neededAmount")
    public int neededAmount;


}
