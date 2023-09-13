package com.kremnev8.electroniccookbook.components.recipe.model;

import android.util.Pair;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RecipeViewStepCache {
    @Embedded
    public RecipeStepCache cache;

    @Relation(parentColumn = "stepId", entityColumn = "id")
    public RecipeStep step;

    public Pair<Integer, Integer> getFullId(){
        return Pair.create(step.recipe, step.id);
    }
}
