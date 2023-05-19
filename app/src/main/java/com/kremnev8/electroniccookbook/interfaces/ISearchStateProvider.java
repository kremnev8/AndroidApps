package com.kremnev8.electroniccookbook.interfaces;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kremnev8.electroniccookbook.components.tabs.model.SearchData;

public interface ISearchStateProvider {
    MutableLiveData<SearchData> getSearchData();
}
