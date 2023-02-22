package com.kremnev8.electroniccookbook.adapters;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("itemViewModels")
    public static void bindItemViewModels(RecyclerView recyclerView, List<BindableRecyclerViewAdapter.IItemViewModel> itemViewModels) {
        var adapter = getOrCreateAdapter(recyclerView);
        adapter.updateItems(itemViewModels);
    }

    private static BindableRecyclerViewAdapter getOrCreateAdapter(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof BindableRecyclerViewAdapter) {
           return (BindableRecyclerViewAdapter)recyclerView.getAdapter();
        } else {
            var bindableRecyclerAdapter = new BindableRecyclerViewAdapter();
            recyclerView.setAdapter(bindableRecyclerAdapter);
            return bindableRecyclerAdapter;
        }
    }
}
