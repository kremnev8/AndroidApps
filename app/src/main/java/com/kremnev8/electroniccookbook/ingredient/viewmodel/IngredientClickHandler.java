package com.kremnev8.electroniccookbook.ingredient.viewmodel;

import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;

public interface IngredientClickHandler {
    void onRemoveIngredient(int id);

    void openIngredientDetails(Ingredient ingredient);
}
