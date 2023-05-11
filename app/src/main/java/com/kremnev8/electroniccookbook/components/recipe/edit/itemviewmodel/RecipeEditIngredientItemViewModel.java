package com.kremnev8.electroniccookbook.components.recipe.edit.itemviewmodel;

import androidx.databinding.Bindable;

import com.google.common.primitives.Floats;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ContextMenuKind;
import com.kremnev8.electroniccookbook.common.recycler.IHasContextMenu;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RecipeEditIngredientItemViewModel
        extends ItemViewModel
        implements IHasContextMenu {

    public RecipeIngredient ingredient;
    private String amountString;

    public RecipeEditIngredientItemViewModel(RecipeIngredient ingredient) {
        setItem(ingredient);
    }

    @Bindable
    public String getAmountString(){
        return amountString;
    }

    public void setAmountString(String value){
        amountString = value;
        ingredient.setAmount(Ingredient.ParseAmountString(value));

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

    @Override
    public int getMenuResId() {
        return R.menu.delete_menu;
    }

    @Override
    public ContextMenuKind getMenuKind() {
        return ContextMenuKind.RECIPE_INGREDIENT;
    }
}
