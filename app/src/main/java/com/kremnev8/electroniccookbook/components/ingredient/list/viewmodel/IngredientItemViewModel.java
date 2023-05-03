package com.kremnev8.electroniccookbook.components.ingredient.list.viewmodel;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.IHasContextMenu;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;

public class IngredientItemViewModel
        extends ItemViewModel
        implements IHasContextMenu {

    public Ingredient ingredient;


    public IngredientItemViewModel(Ingredient ingredient) {
        setItem(ingredient);
    }

    @Override
    public void setItem(Object item) {
        this.ingredient = (Ingredient) item;
    }

    @Override
    public long getItemId() {
        return ingredient.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_ingredient;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    @Override
    public int getMenuResId() {
        return R.menu.edit_menu;
    }
}
