package com.kremnev8.electroniccookbook.viewmodels;

import android.os.Handler;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.model.Ingredient;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientEditViewModel extends ViewModel implements IPhotoRequestCallback {

    private final Handler handler = new Handler();
    private final DatabaseExecutor databaseExecutor;
    private final IPhotoProvider photoProvider;

    private final MutableLiveData<Ingredient> model;

    public LiveData<Ingredient> getModel(){
        return model;
    }

    @Inject
    IngredientEditViewModel(SavedStateHandle handle, DatabaseExecutor ingredientDao, IPhotoProvider photoProvider) {
        this.databaseExecutor = ingredientDao;
        this.photoProvider = photoProvider;
        model = new MutableLiveData<>();
    }

    public void setData(Ingredient ingredient){
        model.setValue(ingredient);
    }

    public void saveData(){
        databaseExecutor.insert(model.getValue());
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

        ingredient.iconUri = imageUri;
        model.postValue(ingredient);
    }
}
