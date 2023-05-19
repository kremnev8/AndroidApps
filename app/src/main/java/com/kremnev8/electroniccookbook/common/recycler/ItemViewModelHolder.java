package com.kremnev8.electroniccookbook.common.recycler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemViewModelHolder<T> {

    private final IItemViewModelSource<T, ItemViewModel> itemViewModelActivator;
    private ItemViewModel footerItemViewModel;

    private ISearchFilter<T> searchFilter;
    private Comparator<T> comparator;

    protected LiveData<List<T>> rawData;
    protected MutableLiveData<ArrayList<ItemViewModel>> viewModels = new MutableLiveData<>();

    public LiveData<ArrayList<ItemViewModel>> getViewModels(){
        return viewModels;
    }

    public LiveData<List<T>> getLiveData(){
        return rawData;
    }

    @NonNull
    public List<T> getList(){
        return Objects.requireNonNull(rawData.getValue());
    }

    public ItemViewModelHolder(IItemViewModelSource<T, ItemViewModel> activator) {
        this.itemViewModelActivator = activator;
        this.viewModels.postValue(createViewData());
    }

    public void setFooter(ItemViewModel footer){
        footerItemViewModel = footer;
        if (rawData == null || rawData.getValue() == null)
            this.viewModels.postValue(createViewData());
        else
            updateViewData(rawData.getValue());
    }

    public void updateData(LiveData<List<T>> newData){
        if (rawData != null)
            rawData.removeObserver(this::updateViewData);

        this.rawData = newData;
        rawData.observeForever(this::updateViewData);
    }

    public void setFilterAndComparator(@Nullable ISearchFilter<T> searchFilter, @Nullable Comparator<T> comparator) {
        this.searchFilter = searchFilter;
        this.comparator = comparator;
        if (rawData != null && rawData.getValue() != null)
            updateViewData(rawData.getValue());
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
        Stream<T> stream = newData.stream();

        if (searchFilter != null)
            stream = stream.filter(t -> searchFilter.shouldShow(t));

        if (comparator != null)
            stream = stream.sorted(comparator);

        if (searchFilter != null || comparator != null)
            newData = stream.collect(Collectors.toList());

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
