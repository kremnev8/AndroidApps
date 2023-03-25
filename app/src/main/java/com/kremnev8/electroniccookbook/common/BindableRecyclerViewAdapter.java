package com.kremnev8.electroniccookbook.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BindableRecyclerViewAdapter<T, VT extends ItemViewModel> extends RecyclerView.Adapter<BindableViewHolder<T, VT>> {

    private List<VT> itemViewModels = new ArrayList<>();
    private final HashMap<Integer, Integer> viewTypeToLayoutId = new HashMap<>();

    @Override
    public BindableViewHolder<T, VT> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        var binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                viewTypeToLayoutId.getOrDefault(viewType, 0),
                parent,
                false);

        return new BindableViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindableViewHolder<T, VT> holder, int position) {
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

    public void updateItems(List<VT> items) {
        if (items != null)
            itemViewModels = items;
        else
            itemViewModels = new ArrayList<>();
        notifyDataSetChanged();
    }

}
