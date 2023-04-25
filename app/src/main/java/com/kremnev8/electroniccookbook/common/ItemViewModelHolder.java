package com.kremnev8.electroniccookbook.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModelHolder<T> {

    private final IItemViewModelSource<T, ItemViewModel> itemViewModelActivator;
    private ItemViewModel footerItemViewModel;

    protected LiveData<List<T>> rawData;
    protected MutableLiveData<ArrayList<ItemViewModel>> viewModels = new MutableLiveData<>();

    public LiveData<ArrayList<ItemViewModel>> getViewModels(){
        return viewModels;
    }

    public LiveData<List<T>> getData(){
        return rawData;
    }

    public ItemViewModelHolder(IItemViewModelSource<T, ItemViewModel> activator) {
        this.itemViewModelActivator = activator;
    }

    public void setFooter(ItemViewModel footer){
        footerItemViewModel = footer;
    }

    public void init(LiveData<List<T>> rawData){
        this.rawData = rawData;
        rawData.observeForever(this::updateViewData);
        var viewModels = createViewData();
        this.viewModels.postValue(viewModels);
    }

    public ArrayList<ItemViewModel> createViewData(){
        if (footerItemViewModel != null){
            var list = new ArrayList<ItemViewModel>(1);
            list.add(footerItemViewModel);
            return list;
        }

        return new ArrayList<>();
    }

    public void updateViewData(List<T> newData){
        var viewModelsList = viewModels.getValue();
        assert viewModelsList != null;
        int size = newData.size();

        if (footerItemViewModel != null){
            viewModelsList.remove(viewModelsList.size() - 1);
            size += 1;
        }

        viewModelsList.ensureCapacity(size);
        for (int i = 0; i < newData.size(); i++) {
            if (i < viewModelsList.size()){
                viewModelsList.get(i).setItem(newData.get(i));
            }else{
                viewModelsList.add(itemViewModelActivator.CreateInstance(newData.get(i)));
            }
        }

        if (viewModelsList.size() > newData.size()) {
            viewModelsList.subList(newData.size(), viewModelsList.size()).clear();
        }

        if (footerItemViewModel != null) {
            viewModelsList.add(footerItemViewModel);
        }

        viewModels.setValue(viewModelsList);
    }

    public void onCleared() {
        if (rawData != null)
            rawData.removeObserver(this::updateViewData);
    }
}
