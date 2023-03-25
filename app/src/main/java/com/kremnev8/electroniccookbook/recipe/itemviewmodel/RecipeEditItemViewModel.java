package com.kremnev8.electroniccookbook.recipe.itemviewmodel;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class RecipeEditItemViewModel extends ItemViewModel {
    public RecipeStep step;

    public RecipeEditItemViewModel(RecipeStep item){
        setItem(item);
    }

    @Override
    public void setItem(Object item) {
        step = (RecipeStep) item;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_step_edit;
    }

    @Override
    public int getViewType() {
        return 2;
    }

}
