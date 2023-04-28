package com.kremnev8.electroniccookbook.components.recipe.model;

import android.util.Pair;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RecipeViewIngredientCache {
    @Embedded
    public RecipeIngredientCache cache;

    @Relation(parentColumn = "ingredientId", entityColumn = "id")
    public RecipeIngredient ingredient;

    public Pair<Integer, Integer> getFullId() {
        return Pair.create(ingredient.recipe, ingredient.id);
    }
}
