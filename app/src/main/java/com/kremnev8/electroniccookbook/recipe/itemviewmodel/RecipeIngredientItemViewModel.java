package com.kremnev8.electroniccookbook.recipe.itemviewmodel;

import androidx.databinding.Bindable;

import com.google.common.primitives.Floats;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RecipeIngredientItemViewModel extends ItemViewModel {

    public RecipeIngredient ingredient;

    public RecipeIngredientItemViewModel(RecipeIngredient ingredient) {
        setItem(ingredient);
    }

    @Bindable
    public String getAmountString(){
        return ingredient.amount + " " + ingredient.units;
    }

    public void setAmountString(String value){
        String[] parts = value.split(" ");
        if (parts.length == 1){
            ingredient.amount = 1;
            ingredient.units = value;
            notifyChange();
        }else if (parts.length > 1){
            Float amount = Floats.tryParse(parts[0]);
            ingredient.amount = amount != null ? amount : 1;
            ingredient.units = Arrays.stream(parts).skip(1).collect(Collectors.joining(""));
            notifyChange();
        }
    }

    @Override
    public void setItem(Object item) {
        ingredient = (RecipeIngredient)item;
    }

    @Override
    public long getItemId() {
        return ingredient.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_ingredient_edit;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
