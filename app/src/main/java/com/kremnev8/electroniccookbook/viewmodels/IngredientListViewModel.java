package com.kremnev8.electroniccookbook.viewmodels;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.database.IngredientDao;
import com.kremnev8.electroniccookbook.fragments.IngredientEditFragment;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientListViewModel extends ViewModel implements IngredientClickHandler {

    private final IngredientDao ingredientDao;
    private LiveData<List<Ingredient>> rawData;
    private MutableLiveData<ArrayList<IngredientViewModel>> ingredients = new MutableLiveData<>();

    public LiveData<ArrayList<IngredientViewModel>> getIngredients(){
        return ingredients;
    }

    @Inject
    IngredientListViewModel(SavedStateHandle handle, IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;

        rawData = ingredientDao.getIngredients();
        rawData.observeForever(this::updateViewData);
        var viewModels = createViewData(rawData);
        ingredients.postValue(viewModels);
    }

    public ArrayList<IngredientViewModel> createViewData(LiveData<List<Ingredient>> data){
        var dataValue = data.getValue();
        if (dataValue == null){
            return new ArrayList<>();
        }

        var viewData = new ArrayList<IngredientViewModel>(dataValue.size());
        for (Ingredient item: dataValue) {
            viewData.add(new IngredientViewModel(item, this));
        }
        return viewData;
    }

    public void updateViewData(List<Ingredient> newData){
        var ingredientsData = ingredients.getValue();
        assert ingredientsData != null;

        ingredientsData.ensureCapacity(newData.size());
        for (int i = 0; i < newData.size(); i++) {
            if (i < ingredientsData.size()){
                ingredientsData.get(i).setItem(newData.get(i));
            }else{
                ingredientsData.add(new IngredientViewModel(newData.get(i), this));
            }
        }

        ingredients.setValue(ingredientsData);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        rawData.removeObserver(this::updateViewData);
    }

    @Override
    public void onRemoveIngredient(int id) {
        ingredientDao.deleteById(id);
    }

    @Override
    public void openIngredientDetails(Ingredient ingredient) {
        Bundle args = new Bundle();
        args.putParcelable(IngredientEditFragment.InspectIngredient, ingredient);

        MainActivity.Instance.setFragment(IngredientEditFragment.class, args);
    }
}

