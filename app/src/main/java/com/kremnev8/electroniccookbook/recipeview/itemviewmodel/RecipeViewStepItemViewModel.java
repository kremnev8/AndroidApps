package com.kremnev8.electroniccookbook.recipeview.itemviewmodel;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.Bindable;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.recipeview.model.ViewStepCache;

public class RecipeViewStepItemViewModel extends ItemViewModel {

    public ViewStepCache step;
    private final DatabaseExecutor executor;

    public RecipeViewStepItemViewModel(ViewStepCache step, DatabaseExecutor executor) {
        this.step = step;
        this.executor = executor;
    }

    @Bindable
    public boolean getComplete(){
        return step.cache.stepComplete;
    }

    public void setComplete(boolean value){
        step.cache.stepComplete = value;
        notifyChange();
        executor.update(step.cache);
    }

    @Override
    public void setItem(Object item) {
        step = (ViewStepCache)item;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_view_step;
    }

    @Override
    public int getViewType() {
        return 2;
    }
}
