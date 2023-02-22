package com.kremnev8.electroniccookbook.adapters;

import static java.util.Collections.emptyList;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.databinding.ItemIngredientBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BindableRecyclerViewAdapter extends RecyclerView.Adapter<BindableRecyclerViewAdapter.BindableViewHolder> {

    private List<IItemViewModel> itemViewModels = new ArrayList<>();
    private HashMap<Integer, Integer> viewTypeToLayoutId = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public BindableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        var binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                viewTypeToLayoutId.getOrDefault(viewType, 0),
                parent,
                false);

        return new BindableViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindableViewHolder holder, int position) {
        holder.bind(itemViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return itemViewModels.size();
    }

    @Override
    public int getItemViewType(int position){
        var item = itemViewModels.get(position);
        if (!viewTypeToLayoutId.containsKey(item.getViewType())) {
            viewTypeToLayoutId.put(item.getViewType(), item.getLayoutId());
        }
        return item.getViewType();
    }

    public void updateItems(List<IItemViewModel> items) {
        if (items != null)
            itemViewModels = items;
        else
            itemViewModels = new ArrayList<>();
        notifyDataSetChanged();
    }

    class BindableViewHolder extends RecyclerView.ViewHolder{

        public ViewDataBinding binding;

        public BindableViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(IItemViewModel itemViewModel){
            binding.setVariable(BR.itemViewModel, itemViewModel);
        }
    }

    public interface IItemViewModel {
        int getLayoutId();
        int getViewType();
    }
}
