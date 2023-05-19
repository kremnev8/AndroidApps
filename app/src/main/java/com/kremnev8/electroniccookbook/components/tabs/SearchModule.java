package com.kremnev8.electroniccookbook.components.tabs;

import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kremnev8.electroniccookbook.components.tabs.model.SearchData;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;
import com.kremnev8.electroniccookbook.interfaces.ISearchStateProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class SearchModule implements ISearchStateProvider {
    private final MutableLiveData<SearchData> searchData = new MutableLiveData<>();

    public SearchModule() {
        searchData.setValue(new SearchData());
    }

    @Override
    public MutableLiveData<SearchData> getSearchData() {
        return searchData;
    }

    @Provides
    public ISearchStateProvider provideSearchStateProvider(){
        return this;
    }
}
