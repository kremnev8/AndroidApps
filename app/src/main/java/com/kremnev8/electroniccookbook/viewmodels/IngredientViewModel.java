package com.kremnev8.electroniccookbook.viewmodels;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.model.Ingredient;

public class IngredientViewModel extends ItemViewModel<Ingredient> {

    private final IngredientClickHandler clickHandler;
    public Ingredient ingredient;
    private Handler handler = new Handler();


    public IngredientViewModel(Ingredient ingredient, IngredientClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        setItem(ingredient);
    }

    @Override
    public void setItem(Ingredient item) {
        this.ingredient = item;
    }

    @BindingAdapter("circleImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide
                .with(MainActivity.Instance)
                .load(imageUrl)
                .placeholder(R.drawable.empty_round_placeholder)
                .circleCrop()
                .into(view);

    }

    public void onRemoveClicked(View view) {
        clickHandler.onRemoveIngredient(ingredient.id);
    }

    public void onEditClicked(View view) {
        handler.postDelayed(() -> {
            clickHandler.openIngredientDetails(ingredient);
        }, 100);
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
