package com.kremnev8.electroniccookbook.viewmodels;

import android.os.Handler;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.database.IngredientDao;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientEditViewModel extends ViewModel implements IPhotoRequestCallback {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Handler handler = new Handler();
    private final IngredientDao ingredientDao;
    private final IPhotoProvider photoProvider;

    private final MutableLiveData<Ingredient> model;

    public LiveData<Ingredient> getModel(){
        return model;
    }

    @Inject
    IngredientEditViewModel(SavedStateHandle handle, IngredientDao ingredientDao, IPhotoProvider photoProvider) {
        this.ingredientDao = ingredientDao;
        this.photoProvider = photoProvider;
        model = new MutableLiveData<>();
    }

    public void setData(Ingredient ingredient){
        model.setValue(ingredient);
    }

    public void saveData(){
        executor.execute(() -> {
            ingredientDao.insert(model.getValue());
        });
    }

    public void selectIconClicked(View view){
        handler.postDelayed(() -> {
            photoProvider.requestPhoto(this);
        }, 100);
    }

    public void takePhotoClicked(View view){
        handler.postDelayed(() -> {
            photoProvider.takePicture(this);
        }, 100);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        Ingredient ingredient = model.getValue();
        assert ingredient != null;

        ingredient.iconUrl = imageUri;
        model.postValue(ingredient);
    }
}
