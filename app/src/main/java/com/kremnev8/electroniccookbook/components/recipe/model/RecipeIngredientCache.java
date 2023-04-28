package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipeIngredientCache",
        indices = @Index(value = {"id"},unique = true),
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "id",
                        childColumns = "recipeId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = RecipeIngredient.class,
                        parentColumns = "id",
                        childColumns = "ingredientId")
        })
public class RecipeIngredientCache {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipeId", index = true)
    public int recipeId;

    @ColumnInfo(name = "ingredientId", index = true)
    public int ingredientId;

    @ColumnInfo(name = "ingredientUsed")
    public boolean ingredientUsed;

    public RecipeIngredientCache() {
    }

    @Ignore
    public RecipeIngredientCache(int recipeId, int ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }
}
