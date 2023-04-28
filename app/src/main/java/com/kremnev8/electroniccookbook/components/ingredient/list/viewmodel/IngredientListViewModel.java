package com.kremnev8.electroniccookbook.components.ingredient.list.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.common.recycler.SimpleListViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.edit.fragment.IngredientEditFragment;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IngredientClickHandler;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientListViewModel extends SimpleListViewModel<Ingredient> implements IngredientClickHandler {

    @Inject
    IngredientListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        super(handle, databaseExecutor);
        rawData = databaseExecutor.getIngredients();
        init();
    }

    public IngredientViewModel CreateInstance(Ingredient item) {
        return new IngredientViewModel(item, this);
    }


    @Override
    public void onRemoveIngredient(int id) {
        databaseExecutor.deleteIngredientById(id);
    }

    @Override
    public void openIngredientDetails(Ingredient ingredient) {
        Bundle args = new Bundle();
        args.putParcelable(IngredientEditFragment.InspectIngredient, ingredient);

        MainActivity.Instance.setFragment(IngredientEditFragment.class, args);
    }

    public String GetFragmentName(){
        return "Ingredients";
    }
}

