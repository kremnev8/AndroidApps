package com.kremnev8.electroniccookbook.recipe.itemviewmodel;

import androidx.databinding.Bindable;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class RecipeEditItemViewModel extends ItemViewModel {
    public RecipeStep step;

    public RecipeEditItemViewModel(RecipeStep item){
        setItem(item);
    }

    @Bindable
    public String getTimerDuration(){
        return Long.toString(step.timer);
    }

    public void setTimerDuration(String value){
        try {
            step.timer = Long.parseLong(value);
            notifyChange();
        }catch (NumberFormatException ignored){
        }
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
