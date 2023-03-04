package com.kremnev8.electroniccookbook.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWithData {

    @Embedded
    public Recipe recipe;

    @Relation(
            parentColumn = "id",
            entityColumn = "recipe"
    )
    public List<RecipeStep> steps;

    @Relation(
            entity = RecipeIngredient.class,
            parentColumn = "id",
            entityColumn = "recipe"
    )
    public List<RecipeFullIngredient> ingredients;

}
