package com.kremnev8.electroniccookbook.adapters;

import androidx.lifecycle.ViewModel;

import java.io.Closeable;

public abstract class ItemViewModel<ITEM_T> extends ViewModel {

    public ItemViewModel() {
        super((Closeable) null);
    }

    public abstract void setItem(ITEM_T item);

    public abstract int getLayoutId();

    public abstract int getViewType();
}
