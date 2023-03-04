package com.kremnev8.electroniccookbook.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RecipeFullIngredient {

    @Embedded
    public RecipeIngredient recipeIngredient;
    @Relation(
            parentColumn = "ingredient",
            entityColumn = "id"
    )
    public Ingredient ingredient;
}
