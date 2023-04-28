package com.kremnev8.electroniccookbook.common.recycler;

import com.kremnev8.electroniccookbook.common.ObservableViewModel;

import java.io.Closeable;

public abstract class ItemViewModel extends ObservableViewModel {

    public ItemViewModel() {
        super((Closeable) null);
    }

    public abstract void setItem(Object item);

    public abstract long getItemId();

    public abstract int getLayoutId();

    public abstract int getViewType();
}
