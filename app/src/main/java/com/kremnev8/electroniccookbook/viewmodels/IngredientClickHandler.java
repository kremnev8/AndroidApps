package com.kremnev8.electroniccookbook.viewmodels;

import com.kremnev8.electroniccookbook.model.Ingredient;

public interface IngredientClickHandler {
    void onRemoveIngredient(int id);

    void openIngredientDetails(Ingredient ingredient);
}
