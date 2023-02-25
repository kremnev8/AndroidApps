package com.kremnev8.electroniccookbook.viewmodels;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.kremnev8.electroniccookbook.database.AppRepository;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientListViewModel extends ViewModel {

    private AppRepository repository;
    private LiveData<List<Ingredient>> rawData;
    private MutableLiveData<ArrayList<IngredientViewModel>> ingredients = new MutableLiveData<>();

    public LiveData<ArrayList<IngredientViewModel>> getIngredients(){
        return ingredients;
    }

    public IngredientListViewModel(AppRepository repository){
        this.repository = repository;

        rawData = repository.getIngredients();
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
            viewData.add(new IngredientViewModel(item));
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
                ingredientsData.add(new IngredientViewModel(newData.get(i)));
            }
        }

        ingredients.setValue(ingredientsData);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        rawData.removeObserver(this::updateViewData);
    }

    public static final ViewModelInitializer<IngredientListViewModel> initializer = new ViewModelInitializer<>(
            IngredientListViewModel.class,
            creationExtras -> {
                Application app = (Application) creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
                assert app != null;

                return new IngredientListViewModel(new AppRepository(app));
            }
    );
}

