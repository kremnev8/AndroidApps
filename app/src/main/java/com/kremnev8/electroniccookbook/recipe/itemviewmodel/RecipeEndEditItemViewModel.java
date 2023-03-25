package com.kremnev8.electroniccookbook.recipe.itemviewmodel;

import android.view.View;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.viewmodels.IAddStepCallback;

public class RecipeEndEditItemViewModel extends ItemViewModel {

    private IAddStepCallback callback;

    public RecipeEndEditItemViewModel(IAddStepCallback callback) {
        this.callback = callback;
    }

    public void addStep(View view){
        callback.addStep();
    }

    @Override
    public void setItem(Object item) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_end_edit;
    }

    @Override
    public int getViewType() {
        return 3;
    }
}
