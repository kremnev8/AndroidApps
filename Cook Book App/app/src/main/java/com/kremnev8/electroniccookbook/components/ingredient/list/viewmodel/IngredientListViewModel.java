package com.kremnev8.electroniccookbook.components.ingredient.list.viewmodel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.common.recycler.SimpleListViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.edit.fragment.IngredientEditFragment;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.tabs.model.SearchData;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class IngredientListViewModel extends SimpleListViewModel<Ingredient> {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final IFragmentController fragmentController;
    private int profileId;

    @Inject
    IngredientListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor,
                            IProfileProvider profileProvider, IFragmentController fragmentController) {
        super(handle, databaseExecutor);
        this.fragmentController = fragmentController;

        profileProvider
                .getCurrentProfile()
                .subscribeOn(Schedulers.computation())
                .subscribe(this::onProfileChanged, throwable -> Log.e("App", "Error while getting profile", throwable));
    }

    public IngredientItemViewModel CreateInstance(Ingredient item) {
        return new IngredientItemViewModel(item);
    }

    private void onProfileChanged(Profile profile) {
        mainHandler.post(() -> {
            profileId = profile.id;
            setData(databaseExecutor.getIngredients(profile.id));
        });
    }

    public void onSearchChanged(SearchData searchData){
        itemViewModelHolder.setFilterAndComparator(
                searchData.getIngredientSearchFilter(),
                searchData.geIngredientComparator()
        );
    }

    public void addIngredient(){
        if (profileId == 0) return;

        Bundle args = new Bundle();
        Ingredient ingredient = new Ingredient();
        ingredient.profileId = profileId;
        ingredient.lastModified = new Date();
        args.putParcelable(IngredientEditFragment.InspectIngredient, ingredient);
        fragmentController.setFragment(IngredientEditFragment.class, args);
    }

    public void editItem(int position) {
        var list = getData();

        if (position >= 0 && position < list.size()) {
            Bundle args = new Bundle();
            args.putParcelable(IngredientEditFragment.InspectIngredient, list.get(position));

            MainActivity.Instance.setFragment(IngredientEditFragment.class, args);
        }
    }

    public void deleteItem(int position) {
        var list = getData();

        if (position >= 0 && position < list.size()) {
            databaseExecutor.delete(list.get(position));
        }
    }
}

