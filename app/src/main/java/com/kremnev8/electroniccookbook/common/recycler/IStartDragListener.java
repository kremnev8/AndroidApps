package com.kremnev8.electroniccookbook.common.recycler;

import androidx.recyclerview.widget.RecyclerView;

public interface IStartDragListener<VT extends ItemViewModel> {
    void requestDrag(BindableViewHolder<VT> viewHolder);
}
