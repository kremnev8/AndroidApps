package com.kremnev8.electroniccookbook.adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.databinding.ItemIngredientBinding;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients;
    private static RoundedBitmapDrawable image;

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var binding = ItemIngredientBinding.inflate(inflater, parent, false);

        return new IngredientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        var ingredient = ingredients.get(position); // Получение человека из списка данных по позиции
        var context = holder.itemView.getContext();

        holder.binding.name.setText(ingredient.name);
        holder.binding.amount.setText(Float.toString(ingredient.amount));

        if (image == null){
            Resources res = context.getResources();
            Bitmap src = BitmapFactory.decodeResource(res, R.drawable.carrot);
            image = RoundedBitmapDrawableFactory.create(res, src);
            image.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        }
        holder.binding.icon.setImageDrawable(image);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder{

        public ItemIngredientBinding binding;

        public IngredientViewHolder(ItemIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
