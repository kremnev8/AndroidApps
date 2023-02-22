package com.kremnev8.electroniccookbook.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.BindingAdapter;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.BindableRecyclerViewAdapter;
import com.kremnev8.electroniccookbook.model.Ingredient;

public class IngredientViewModel implements BindableRecyclerViewAdapter.IItemViewModel {

    private static RoundedBitmapDrawable image;

    public Integer id;
    public String name;
    public String iconUri;
    public Float amount;
    public String units;

    public IngredientViewModel(Ingredient ingredient) {
        id = ingredient.id;
        name = ingredient.name;
        iconUri = ingredient.iconUri;
        amount = ingredient.amount;
        units = ingredient.units;
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

    @Override
    public int getLayoutId() {
        return R.layout.item_ingredient;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
