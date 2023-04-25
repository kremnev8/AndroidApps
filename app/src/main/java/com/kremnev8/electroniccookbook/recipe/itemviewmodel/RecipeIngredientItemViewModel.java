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
    private String amountString;

    public RecipeIngredientItemViewModel(RecipeIngredient ingredient) {
        setItem(ingredient);
    }

    @Bindable
    public String getAmountString(){
        return amountString;
    }

    public void setAmountString(String value){
        amountString = value;
        String[] parts = value.split(" ");
        if (parts.length == 1){
            ingredient.amount = 0;
            ingredient.units = value;
        }else if (parts.length > 1){
            Float amount = Floats.tryParse(parts[0]);
            if (amount == null){
                ingredient.amount = 0;
                ingredient.units = value;
            }else {
                ingredient.amount = amount;
                ingredient.units = Arrays.stream(parts).skip(1).collect(Collectors.joining(""));
            }
        }
        notifyChange();
    }

    @Override
    public void setItem(Object item) {
        ingredient = (RecipeIngredient)item;
        amountString = ingredient.getAmountString();
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
