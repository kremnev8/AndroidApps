package com.kremnev8.electroniccookbook.components.ingredient.edit.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.common.SimpleViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;
import com.kremnev8.electroniccookbook.interfaces.IMediaRequestCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IngredientEditViewModel extends SimpleViewModel<Ingredient> implements IMediaRequestCallback {

    private final IMediaProvider photoProvider;

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
    IngredientEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IMediaProvider photoProvider) {
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
        handler.postDelayed(() -> photoProvider.requestMedia(this, false), 100);
    }

    @Override
    public void onMediaSelected(String imageUri) {
        model.iconUri = imageUri;
        notifyChange();
    }
}
