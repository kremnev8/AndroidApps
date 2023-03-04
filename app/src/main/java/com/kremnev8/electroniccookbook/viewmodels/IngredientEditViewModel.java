package com.kremnev8.electroniccookbook.viewmodels;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kremnev8.electroniccookbook.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.database.AppRepository;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientEditViewModel extends ViewModel implements IPhotoRequestCallback {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final RequestOptions requestOptions;
    private Handler handler = new Handler();

    static {
        requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16));
    }

    private AppRepository repository;
    private final MutableLiveData<Ingredient> model;

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

    public void selectIconClicked(View view){
        handler.postDelayed(() -> {
            MainActivity.Instance.requestPhoto(this);
        }, 100);
    }

    public void takePhotoClicked(View view){
        handler.postDelayed(() -> {
            MainActivity.Instance.takePicture(this);
        }, 100);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        Objects.requireNonNull(model.getValue()).iconUrl = imageUri;
        saveData();
    }


    @BindingAdapter("roundedImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide
                .with(MainActivity.Instance)
                .load(imageUrl)
                .placeholder(R.drawable.empty_placeholder)
                .apply(requestOptions)
                .into(view);
    }
}
