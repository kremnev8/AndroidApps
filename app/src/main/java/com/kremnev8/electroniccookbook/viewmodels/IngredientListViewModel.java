package com.kremnev8.electroniccookbook.viewmodels;

import android.os.Bundle;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.fragments.IngredientEditFragment;
import com.kremnev8.electroniccookbook.itemViewModel.IngredientViewModel;
import com.kremnev8.electroniccookbook.model.Ingredient;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientListViewModel extends SimpleListViewModel<Ingredient, IngredientViewModel> implements IngredientClickHandler {

    @Inject
    IngredientListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        super(handle, databaseExecutor);
        rawData = databaseExecutor.getIngredients();
        init();
    }

    @Override
    public IngredientViewModel CreateNewItemViewModel(Ingredient item) {
        return new IngredientViewModel(item, this);
    }


    @Override
    public void onRemoveIngredient(int id) {
        databaseExecutor.deleteById(id);
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

