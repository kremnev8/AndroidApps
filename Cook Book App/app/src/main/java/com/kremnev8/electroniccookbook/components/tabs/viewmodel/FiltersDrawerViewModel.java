package com.kremnev8.electroniccookbook.components.tabs.viewmodel;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ObservableViewModel;
import com.kremnev8.electroniccookbook.components.tabs.model.SearchData;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FiltersDrawerViewModel extends ObservableViewModel {

    private MutableLiveData<SearchData> searchData;
    private final IFragmentController  fragmentController;

    @Inject
    public FiltersDrawerViewModel(SavedStateHandle handle, IFragmentController fragmentController) {
        this.fragmentController = fragmentController;
    }

    public void setSearchData(MutableLiveData<SearchData> searchData) {
        this.searchData = searchData;
    }

    @Bindable
    public boolean getIsRecipeList(){
        IMenu menu = (IMenu)fragmentController.getCurrentFragment();
        return menu.getMenuName() == R.string.recipes_label;
    }

    public LiveData<SearchData> getSearchData() {
        return searchData;
    }

    private SearchData getSearchDataObject(){
        return searchData.getValue();
    }

    public void confirmSearch(){
        searchData.setValue(searchData.getValue());
    }

}