package com.kremnev8.electroniccookbook.common.recycler;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.MainActivity;

class BindableViewHolder<T, VT extends ItemViewModel>
        extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener {

    public ViewDataBinding binding;
    private ItemViewModel viewModel;

    public BindableViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(VT itemViewModel, int position) {
        binding.setVariable(BR.itemViewModel, itemViewModel);
        viewModel = itemViewModel;
        if (viewModel instanceof IHasContextMenu) {
            IHasContextMenu hasContextMenu = (IHasContextMenu) viewModel;
            ItemView itemView = (ItemView) binding.getRoot();

            itemView.setInfo(position, hasContextMenu.getMenuKind());
            itemView.setOnCreateContextMenuListener(this);
        }
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
