package com.kremnev8.electroniccookbook.recipeview.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class ViewStepCache {
    @Embedded
    public ViewCache cache;

    @Relation(parentColumn = "stepId", entityColumn = "id")
    public RecipeStep step;
}
