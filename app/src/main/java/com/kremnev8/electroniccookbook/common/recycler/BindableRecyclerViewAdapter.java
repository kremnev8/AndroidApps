package com.kremnev8.electroniccookbook.common.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BindableRecyclerViewAdapter<VT extends ItemViewModel>
        extends RecyclerView.Adapter<BindableViewHolder<VT>>
        implements ItemMoveCallback.ItemTouchHelperContract<VT>, IStartDragListener<VT>, IHasOnTwos {

    private List<VT> itemViewModels = new ArrayList<>();
    private final HashMap<Integer, Integer> viewTypeToLayoutId = new HashMap<>();
    private ItemTouchHelper itemTouchHelper;
    private boolean onTwos;

    private final int firstColor;
    private final int secondColor;
    private final int selectedColor;

    public BindableRecyclerViewAdapter() {
        setHasStableIds(true);

        MainActivity activity = MainActivity.Instance;
        firstColor = activity.getResources().getColor(R.color.item_first, activity.getTheme());
        secondColor = activity.getResources().getColor(R.color.item_second, activity.getTheme());
        selectedColor = activity.getResources().getColor(R.color.gray, activity.getTheme());
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public BindableViewHolder<VT> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        var binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                viewTypeToLayoutId.getOrDefault(viewType, 0),
                parent,
                false);

        return new BindableViewHolder<>(binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BindableViewHolder<VT> holder, int position) {
        holder.bind(itemViewModels.get(position), position);
        setItemBackgroundColor(holder);
        setLastItemPadding(holder);
    }

    private void setItemBackgroundColor(@NonNull BindableViewHolder<VT> holder) {
        int val = holder.position % (onTwos ? 4 : 2);

        if (val == 0 || val == 3){
            holder.itemView.setBackgroundColor(firstColor);
        }else{
            holder.itemView.setBackgroundColor(secondColor);
        }
    }

    private void setLastItemPadding(@NonNull BindableViewHolder<VT> holder) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.binding.getRoot().getLayoutParams();
        int bottom = holder.position == itemViewModels.size() - 1 ? 200 : 0;
        layoutParams.setMargins(layoutParams.leftMargin,layoutParams.topMargin,layoutParams.rightMargin, bottom);
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
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
    public void onViewRecycled(@NonNull BindableViewHolder<VT> holder) {
        holder.onRecycled();
        super.onViewRecycled(holder);
    }

    public void updateItems(List<VT> items) {
        if (items != null)
            itemViewModels = items;
        else
            itemViewModels = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                if (swapAndValidate(i, i + 1)) break;
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                if (swapAndValidate(i, i - 1)) break;
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    private boolean swapAndValidate(int i1, int i2){
        VT item1 = itemViewModels.get(i1);
        VT item2 = itemViewModels.get(i2);
        if (item1 instanceof ICanBeReordered &&
            item2 instanceof ICanBeReordered){
            itemViewModels.set(i1, item2);
            itemViewModels.set(i2, item1);
            return false;
        }
        return true;
    }

    @Override
    public void onRowSelected(BindableViewHolder<VT> holder) {
        holder.itemView.setBackgroundColor(selectedColor);
    }

    @Override
    public void onRowClear(BindableViewHolder<VT> holder) {
        setItemBackgroundColor(holder);
    }

    @Override
    public void requestDrag(BindableViewHolder<VT> viewHolder) {
        if (itemTouchHelper != null){
            itemTouchHelper.startDrag(viewHolder);
        }
    }

    @Override
    public void setOnTwos(boolean onTwos) {
        this.onTwos = onTwos;
    }
}
