package com.kremnev8.electroniccookbook.recipe.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleListViewModel <T, TVM extends ItemViewModel<T>> extends ViewModel {

    public final DatabaseExecutor databaseExecutor;
    protected LiveData<List<T>> rawData;
    protected MutableLiveData<ArrayList<TVM>> viewModels = new MutableLiveData<>();

    public LiveData<ArrayList<TVM>> getViewModels(){
        return viewModels;
    }

    public SimpleListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        this.databaseExecutor = databaseExecutor;
    }

    protected void init() {
        rawData.observeForever(this::updateViewData);
        var viewModels = createViewData(rawData);
        this.viewModels.postValue(viewModels);
    }

    public abstract TVM CreateNewItemViewModel(T item);

    public ArrayList<TVM> createViewData(LiveData<List<T>> data){
        var dataValue = data.getValue();
        if (dataValue == null){
            return new ArrayList<>();
        }

        var viewData = new ArrayList<TVM>(dataValue.size());
        for (T item: dataValue) {
            viewData.add(CreateNewItemViewModel(item));
        }
        return viewData;
    }

    public void updateViewData(List<T> newData){
        var viewModelsList = viewModels.getValue();
        assert viewModelsList != null;

        viewModelsList.ensureCapacity(newData.size());
        for (int i = 0; i < newData.size(); i++) {
            if (i < viewModelsList.size()){
                viewModelsList.get(i).setItem(newData.get(i));
            }else{
                viewModelsList.add(CreateNewItemViewModel(newData.get(i)));
            }
        }

        viewModels.setValue(viewModelsList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        rawData.removeObserver(this::updateViewData);
    }
}
