package com.kremnev8.electroniccookbook.components.ingredient.edit.viewmodel;

import android.os.Handler;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.common.ObservableViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientEditViewModel extends ObservableViewModel implements IPhotoRequestCallback {

    private final Handler handler = new Handler();
    private final DatabaseExecutor databaseExecutor;
    private final IPhotoProvider photoProvider;

    private final MutableLiveData<Ingredient> model;
    private String amountText;

    public LiveData<Ingredient> getModel(){
        return model;
    }

    @Bindable
    public String getAmount()
    {
        return amountText;
    }

    public void setAmount(String value){
        amountText = value;
        if (model.getValue() != null)
            model.getValue().setAmount(Ingredient.ParseAmountString(value));
        notifyChange();
    }


    @Inject
    IngredientEditViewModel(SavedStateHandle handle, DatabaseExecutor ingredientDao, IPhotoProvider photoProvider) {
        this.databaseExecutor = ingredientDao;
        this.photoProvider = photoProvider;
        model = new MutableLiveData<>();
    }

    public void setData(Ingredient ingredient){
        model.setValue(ingredient);
        amountText = ingredient.getAmountString();
    }

    public void saveData(){
        databaseExecutor.insert(model.getValue());
    }

    public void addPhotoClicked(View view){
        handler.postDelayed(() -> photoProvider.requestPhoto(this), 100);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        Ingredient ingredient = model.getValue();
        assert ingredient != null;

        ingredient.iconUri = imageUri;
        model.postValue(ingredient);
    }
}
