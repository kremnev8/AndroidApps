package com.kremnev8.electroniccookbook.common;

import androidx.lifecycle.ViewModel;

import java.io.Closeable;

public abstract class ItemViewModel extends ObservableViewModel {

    public ItemViewModel() {
        super((Closeable) null);
    }

    public abstract void setItem(Object item);

    public abstract int getLayoutId();

    public abstract int getViewType();
}
