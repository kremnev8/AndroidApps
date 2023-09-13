package com.kremnev8.electroniccookbook.common.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback<VT extends ItemViewModel> extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract<VT> mAdapter;
    private final IReorderCallback reorderCallback;

    public ItemMoveCallback(ItemTouchHelperContract<VT> adapter, IReorderCallback reorderCallback) {
        mAdapter = adapter;
        this.reorderCallback = reorderCallback;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        BindableViewHolder<VT> myViewHolder = (BindableViewHolder<VT>) viewHolder;
        if (!(myViewHolder.viewModel instanceof ICanBeReordered)) {
            return makeMovementFlags(0, 0);
        }

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int i1 = viewHolder.getAdapterPosition();
        int i2 = target.getAdapterPosition();
        mAdapter.onRowMoved(i1, i2);
        reorderCallback.onDrag(i1, i2);
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            BindableViewHolder<VT> myViewHolder = (BindableViewHolder<VT>) viewHolder;
            mAdapter.onRowSelected(myViewHolder);
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        BindableViewHolder<VT> myViewHolder = (BindableViewHolder<VT>) viewHolder;
        mAdapter.onRowClear(myViewHolder);
    }

    public interface ItemTouchHelperContract<VT extends ItemViewModel> {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(BindableViewHolder<VT> myViewHolder);

        void onRowClear(BindableViewHolder<VT> myViewHolder);
    }
}
