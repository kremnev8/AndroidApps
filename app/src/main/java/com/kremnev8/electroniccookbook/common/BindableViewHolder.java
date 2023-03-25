package com.kremnev8.electroniccookbook.common;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.kremnev8.electroniccookbook.BR;

class BindableViewHolder<T, VT extends ItemViewModel> extends RecyclerView.ViewHolder {

    public ViewDataBinding binding;

    public BindableViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(VT itemViewModel) {
        binding.setVariable(BR.itemViewModel, itemViewModel);
    }
}
