package com.kremnev8.electroniccookbook.common;

import android.os.Handler;

import androidx.databinding.Bindable;

import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

public abstract class SimpleViewModel<T> extends ObservableViewModel {

    protected final Handler handler = new Handler();
    protected final DatabaseExecutor databaseExecutor;

    protected T model;

    @Bindable
    public T getModel(){
        return model;
    }

    protected SimpleViewModel(DatabaseExecutor ingredientDao) {
        this.databaseExecutor = ingredientDao;
    }

    public void setData(T data){
        model = data;
        notifyChange();
    }

    public abstract void saveData();
}
