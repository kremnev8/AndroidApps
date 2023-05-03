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
public class IngredientListViewModel extends SimpleListViewModel<Ingredient> {

    @Inject
    IngredientListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        super(handle, databaseExecutor);
        rawData = databaseExecutor.getIngredients();
        init();
    }

    public IngredientItemViewModel CreateInstance(Ingredient item) {
        return new IngredientItemViewModel(item);
    }

    public void editItem(int position) {
        var list = rawData.getValue();
        assert list != null;

        if (position >= 0 && position < list.size()) {
            Bundle args = new Bundle();
            args.putParcelable(IngredientEditFragment.InspectIngredient, list.get(position));

            MainActivity.Instance.setFragment(IngredientEditFragment.class, args);
        }
    }

    public void deleteItem(int position) {
        var list = rawData.getValue();
        assert list != null;

        if (position >= 0 && position < list.size()) {
            databaseExecutor.delete(list.get(position));
        }
    }
}

