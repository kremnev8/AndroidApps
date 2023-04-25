package com.kremnev8.electroniccookbook.recipeview.model;

import android.util.Pair;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class RecipeFullIngredient {
    @Embedded
    public RecipeIngredient recipeIngredient;

    @Relation(parentColumn = "ingredient", entityColumn = "id")
    public Ingredient ingredient;
}
