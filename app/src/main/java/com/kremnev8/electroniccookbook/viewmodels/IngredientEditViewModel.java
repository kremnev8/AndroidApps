package com.kremnev8.electroniccookbook.viewmodels;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.database.AppRepository;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientEditViewModel extends ViewModel {

    private static RoundedBitmapDrawable image;
    private static Drawable plusImage;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private AppRepository repository;
    public MutableLiveData<Ingredient> model;

    public LiveData<Ingredient> getModel(){
        return model;
    }

    public IngredientEditViewModel(){
        this.repository = MainActivity.Instance.repository;
        model = new MutableLiveData<>();
    }

    public void setData(Ingredient ingredient){
        model.setValue(ingredient);
    }

    public void saveData(){
        executor.execute(() -> {
            repository.insert(model.getValue());
        });
    }


    @BindingAdapter("buttonImage")
    public static void loadImage(ImageButton view, String imageUrl) {
        Resources res = MainActivity.Instance.getResources();

        if (imageUrl == null || imageUrl.equals("")){
            if (plusImage == null) {
                Bitmap src = BitmapFactory.decodeResource(res, R.drawable.image_add);
                plusImage = new BitmapDrawable(res, src);
            }
            view.setImageDrawable(plusImage);
            return;
        }

        if (image == null){

            Bitmap src = BitmapFactory.decodeResource(res, R.drawable.carrot);
            image = RoundedBitmapDrawableFactory.create(res, src);
            image.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        }

        view.setImageDrawable(image);
    }

}
