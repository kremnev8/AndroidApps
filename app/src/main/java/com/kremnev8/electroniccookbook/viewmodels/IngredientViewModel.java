package com.kremnev8.electroniccookbook.viewmodels;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.BindableRecyclerViewAdapter;
import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.model.Ingredient;

public class IngredientViewModel extends ItemViewModel<Ingredient> {

    private static RoundedBitmapDrawable image;
    private final IngredientClickHandler clickHandler;
    public Ingredient ingredient;

    public IngredientViewModel(Ingredient ingredient, IngredientClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        setItem(ingredient);
    }

    @Override
    public void setItem(Ingredient item) {
        this.ingredient = item;
    }

    public RoundedBitmapDrawable GetRoundedBitmap(){
        if (image == null){
            Resources res = MainActivity.Instance.getResources();
            Bitmap src = BitmapFactory.decodeResource(res, R.drawable.carrot);
            image = RoundedBitmapDrawableFactory.create(res, src);
            image.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        }
        return image;
    }

    @BindingAdapter("ingredientImage")
    public static void loadImage(ImageView view, String imageUrl) {
        if (image == null){
            Resources res = MainActivity.Instance.getResources();
            Bitmap src = BitmapFactory.decodeResource(res, R.drawable.carrot);
            image = RoundedBitmapDrawableFactory.create(res, src);
            image.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        }

        view.setImageDrawable(image);
    }

    public void onRemoveClicked(View view){
        clickHandler.onRemoveIngredient(ingredient.id);
    }

    public void onEditClicked(View view){
        clickHandler.openIngredientDetails(ingredient);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_ingredient;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
