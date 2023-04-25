package com.kremnev8.electroniccookbook.recipeview.model;

import android.util.Pair;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;

public class RecipeViewIngredientCache {
    @Embedded
    public RecipeIngredientCache cache;

    @Relation(parentColumn = "ingredientId", entityColumn = "id")
    public RecipeIngredient ingredient;

    public Pair<Integer, Integer> getFullId() {
        return Pair.create(ingredient.recipe, ingredient.id);
    }
}
