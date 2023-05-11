package com.kremnev8.electroniccookbook.common.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BindableRecyclerViewAdapter<T, VT extends ItemViewModel>
        extends RecyclerView.Adapter<BindableViewHolder<T, VT>> {

    private List<VT> itemViewModels = new ArrayList<>();
    private final HashMap<Integer, Integer> viewTypeToLayoutId = new HashMap<>();

    private final int firstColor;
    private final int secondColor;

    public BindableRecyclerViewAdapter() {
        setHasStableIds(true);

        MainActivity activity = MainActivity.Instance;
        firstColor = activity.getResources().getColor(R.color.item_first, activity.getTheme());
        secondColor = activity.getResources().getColor(R.color.item_second, activity.getTheme());
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
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
        holder.bind(itemViewModels.get(position), position);
        SetItemBackgroundColor(holder, position);
        SetLastItemPadding(holder, position);
    }

    private void SetItemBackgroundColor(@NonNull BindableViewHolder<T, VT> holder, int position) {
        if (position % 2 == 0){
            holder.itemView.setBackgroundColor(firstColor);
        }else{
            holder.itemView.setBackgroundColor(secondColor);
        }
    }

    private void SetLastItemPadding(@NonNull BindableViewHolder<T, VT> holder, int position) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.binding.getRoot().getLayoutParams();
        int bottom = position == itemViewModels.size() - 1 ? 200 : 0;
        layoutParams.setMargins(layoutParams.leftMargin,layoutParams.topMargin,layoutParams.rightMargin, bottom);
    }

    @Override
    public long getItemId(int position) {
        return itemViewModels.get(position).getItemId();
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

    @Override
    public void onViewRecycled(@NonNull BindableViewHolder<T, VT> holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public void updateItems(List<VT> items) {
        if (items != null)
            itemViewModels = items;
        else
            itemViewModels = new ArrayList<>();
        notifyDataSetChanged();
    }

}
