package com.kremnev8.electroniccookbook.recipeview.model;

import android.util.Pair;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class ViewStepCache {
    @Embedded
    public ViewCache cache;

    @Relation(parentColumn = "stepId", entityColumn = "id")
    public RecipeStep step;

    public Pair<Integer, Integer> getFullId(){
        return Pair.create(step.recipe, step.id);
    }
}
