package com.kremnev8.electroniccookbook.common.recycler;

import android.annotation.SuppressLint;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.MainActivity;

class BindableViewHolder<VT extends ItemViewModel>
        extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, View.OnTouchListener {

    private final IStartDragListener<VT> startDragListener;

    public int position;
    public ViewDataBinding binding;
    public ItemViewModel viewModel;

    public BindableViewHolder(ViewDataBinding binding, IStartDragListener<VT> startDragListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.startDragListener = startDragListener;
    }


    public void bind(VT itemViewModel, int position) {
        binding.setVariable(BR.itemViewModel, itemViewModel);
        this.position = position;
        viewModel = itemViewModel;
        if (viewModel instanceof IHasContextMenu) {
            IHasContextMenu hasContextMenu = (IHasContextMenu) viewModel;
            ItemView itemView = (ItemView) binding.getRoot();

            itemView.setInfo(position, hasContextMenu.getMenuKind());
            itemView.setOnCreateContextMenuListener(this);
        }
        if (viewModel instanceof ICanBeReordered){
            ICanBeReordered canBeReordered = (ICanBeReordered) viewModel;
            View dragHandle = binding.getRoot().findViewById(canBeReordered.getDragHandleId());
            dragHandle.setOnTouchListener(this);
        }
    }

    public void onRecycled(){
        itemView.setOnLongClickListener(null);
        if (viewModel instanceof ICanBeReordered){
            ICanBeReordered canBeReordered = (ICanBeReordered) viewModel;
            View dragHandle = binding.getRoot().findViewById(canBeReordered.getDragHandleId());
            dragHandle.setOnTouchListener(null);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startDragListener.requestDrag(this);
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (viewModel != null && viewModel instanceof IHasContextMenu){
            var menuModel = (IHasContextMenu)viewModel;
            MenuInflater inflater = MainActivity.Instance.getMenuInflater();
            inflater.inflate(menuModel.getMenuResId(), menu);
        }
    }
}
