package com.kremnev8.electroniccookbook.components.ingredient.edit.viewmodel;

import android.os.Handler;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.common.ObservableViewModel;
import com.kremnev8.electroniccookbook.common.SimpleViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientEditViewModel extends SimpleViewModel<Ingredient> implements IPhotoRequestCallback {

    private final IPhotoProvider photoProvider;

    private String amountText;

    @Bindable
    public String getAmount()
    {
        return amountText;
    }

    public void setAmount(String value){
        amountText = value;
        if (model != null)
            model.setAmount(Ingredient.ParseAmountString(value));
        notifyPropertyChanged(BR.amount);
    }

    @Inject
    IngredientEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IPhotoProvider photoProvider) {
        super(databaseExecutor);
        this.photoProvider = photoProvider;
    }

    @Override
    public void setData(Ingredient ingredient){
        amountText = ingredient.getAmountString();
        super.setData(ingredient);
    }

    public void saveData(){
        databaseExecutor.insert(model);
    }

    public void addPhotoClicked(View view){
        handler.postDelayed(() -> photoProvider.requestPhoto(this), 100);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        model.iconUri = imageUri;
        notifyChange();
    }
}
