package com.kremnev8.electroniccookbook.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.viewmodels.IngredientViewModel;

import java.util.List;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("itemViewModels")
    public static <T, VT extends ItemViewModel<T>> void bindItemViewModels(RecyclerView recyclerView, List<VT> itemViewModels) {
        BindableRecyclerViewAdapter<T, VT> adapter = getOrCreateAdapter(recyclerView);
        adapter.updateItems(itemViewModels);
    }

    @SuppressWarnings("unchecked")
    public static <T, VT extends ItemViewModel<T>> BindableRecyclerViewAdapter<T, VT> getOrCreateAdapter(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof BindableRecyclerViewAdapter) {
           return (BindableRecyclerViewAdapter<T, VT>)recyclerView.getAdapter();
        } else {
            BindableRecyclerViewAdapter<T, VT> bindableRecyclerAdapter = new BindableRecyclerViewAdapter<>();
            recyclerView.setAdapter(bindableRecyclerAdapter);
            return bindableRecyclerAdapter;
        }
    }
}
