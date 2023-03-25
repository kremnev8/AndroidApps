package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;

import com.kremnev8.electroniccookbook.recipeview.model.ViewCache;

import java.util.List;

public interface ICacheCallback {

    void onResult(LiveData<List<ViewCache>> result);
}
