package com.kremnev8.electroniccookbook.recipe.itemviewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public class RecipeEditItemViewModel extends ItemViewModel implements IPhotoRequestCallback {

    public RecipeStep step;
    private int hours;
    private int minutes;

    private final IPhotoProvider photoProvider;

    public RecipeEditItemViewModel(RecipeStep item, IPhotoProvider photoProvider){
        setItem(item);
        this.photoProvider = photoProvider;
    }

    @Bindable
    public String getHours(){
        return Integer.toString(hours);
    }

    public void setHours(String value){
        try {
            hours = Integer.parseInt(value);
            step.timer = hours * 60L + minutes;
            notifyChange();
        }catch (NumberFormatException ignored){
        }
    }

    @Bindable
    public String getMinutes(){
        return Integer.toString(minutes);
    }

    public void setMinutes(String value){
        try {
            minutes = Integer.parseInt(value);
            step.timer = hours * 60L + minutes;
            notifyChange();
        }catch (NumberFormatException ignored){
        }
    }

    @Override
    public void setItem(Object item) {
        step = (RecipeStep) item;
        hours = Math.round(step.timer / 60f);
        minutes = (int)(step.timer % 60);
    }

    public void takePhotoButtonClicked(View view){
        photoProvider.requestPhoto(this);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        step.mediaUri = imageUri;
        notifyChange();
    }

    @Override
    public long getItemId() {
        return step.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_step_edit;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
