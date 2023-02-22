package com.kremnev8.electroniccookbook.viewmodels;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.IngredientDataProvider;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.BindableRecyclerViewAdapter;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientListViewModel extends ViewModel {

    private MutableLiveData<List<BindableRecyclerViewAdapter.IItemViewModel>> ingredients = new MutableLiveData<>();

    public LiveData<List<BindableRecyclerViewAdapter.IItemViewModel>> getIngredients(){
        return ingredients;
    }



    public IngredientListViewModel(IngredientDataProvider dataProvider){
        var data = dataProvider.getIngredientData();
        var viewModels = createViewData(data);
        ingredients.postValue(viewModels);
    }

    public List<BindableRecyclerViewAdapter.IItemViewModel> createViewData(List<Ingredient> ingredients){
        var viewData = new ArrayList<BindableRecyclerViewAdapter.IItemViewModel>(ingredients.size());
        for (Ingredient item: ingredients) {
            viewData.add(new IngredientViewModel(item));
        }
        return viewData;
    }


    /*public void RemoveIngredient(Ingredient ingredient){
        int index = Iterables.indexOf(ingredients.getValue(), item -> Objects.equals(((IngredientViewModel)item).id, ingredient.id));
        if (index == -1) return;

        ingredients.remove(index);
    }

    public void SetAmount(Ingredient ingredient, float amount){
        int index = Iterables.indexOf(ingredients.getValue(), item -> Objects.equals(((IngredientViewModel)item).id, ingredient.id));
        if (index == -1) return;

        ingredient.amount = amount;
        ingredients.set(index, ingredient);
    }
*/
}
