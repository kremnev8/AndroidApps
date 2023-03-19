package com.kremnev8.electroniccookbook.recipe.viewmodels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class RecipeMainEditItemViewModel extends ItemViewModel  {

    public RecipeMainEditItemViewModel(MutableLiveData<Recipe> recipe, IRecipePhotoAccess photoAccess){
        this.recipe = recipe;
        this.photoAccess = photoAccess;
    }

    public MutableLiveData<Recipe> recipe;
    private IRecipePhotoAccess photoAccess;

    @Override
    public void setItem(Object item) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_main_edit;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    public void selectIconClicked(View view){
        photoAccess.selectIconClicked();
    }

    public void takePhotoClicked(View view){
        photoAccess.takePhotoClicked();
    }
}
