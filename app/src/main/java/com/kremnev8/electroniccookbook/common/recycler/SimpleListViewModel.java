package com.kremnev8.electroniccookbook.common.recycler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleListViewModel <T>
        extends ViewModel
        implements IItemViewModelSource<T, ItemViewModel> {

    public final DatabaseExecutor databaseExecutor;
    protected ItemViewModelHolder<T> itemViewModelHolder;
    protected LiveData<List<T>> rawData;

    public LiveData<ArrayList<ItemViewModel>> getViewModels(){
        return itemViewModelHolder.viewModels;
    }

    protected List<T> getData(){
        return rawData.getValue();
    }

    public SimpleListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        this.databaseExecutor = databaseExecutor;
        itemViewModelHolder = new ItemViewModelHolder<>(this);
    }

    protected void init() {
        itemViewModelHolder.init(rawData);
    }

    public abstract ItemViewModel CreateInstance(T item);

    @Override
    protected void onCleared() {
        super.onCleared();
        itemViewModelHolder.onCleared();
    }
}
