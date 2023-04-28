package com.kremnev8.electroniccookbook.interfaces;

import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;

public interface IngredientClickHandler {
    void onRemoveIngredient(int id);

    void openIngredientDetails(Ingredient ingredient);
}
