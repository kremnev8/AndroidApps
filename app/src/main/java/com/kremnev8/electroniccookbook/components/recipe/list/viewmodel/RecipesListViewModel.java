package com.kremnev8.electroniccookbook.components.recipe.list.viewmodel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.common.recycler.SimpleListViewModel;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditFragment;
import com.kremnev8.electroniccookbook.components.recipe.list.itemviewmodel.RecipeItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class RecipesListViewModel extends SimpleListViewModel<Recipe> {

    private final IProfileProvider profileProvider;
    private final IFragmentController fragmentController;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private int profileId;

    @Inject
    RecipesListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IProfileProvider profileProvider, IFragmentController fragmentController) {
        super(handle, databaseExecutor);
        this.profileProvider = profileProvider;
        this.fragmentController = fragmentController;
        profileProvider
                .getCurrentProfile()
                .subscribeOn(Schedulers.computation())
                .subscribe(this::onDataReady, throwable -> Log.e("App", "Error while getting profile", throwable));
    }

    private void onDataReady(Profile profile) {
        mainHandler.post(() -> {
            setData(databaseExecutor.getRecipesWithData(profile.id));
            profileId = profile.id;
        });
    }

    public void addRecipe(){
        if (profileId == 0) return;

        Recipe recipe = new Recipe();
        recipe.profileId = profileId;
        databaseExecutor.upsertWithCallback(recipe, itemId -> {
            Bundle args = new Bundle();
            recipe.id = (int)itemId;
            args.putParcelable(RecipeEditFragment.TARGET_RECIPE, recipe);
            fragmentController.setFragment(RecipeEditFragment.class, args);
        });
    }

    public void editItem(int index) {
        var list = getData();

        if (index >= 0 && index < list.size()) {
            Bundle args = new Bundle();
            args.putParcelable(RecipeEditFragment.TARGET_RECIPE, list.get(index));
            MainActivity.Instance.setFragment(RecipeEditFragment.class, args);
        }
    }

    public void deleteItem(int index) {
        var list = getData();

        if (index >= 0 && index < list.size()) {
            databaseExecutor.delete(list.get(index));
        }
    }

    @Override
    public RecipeItemViewModel CreateInstance(Recipe item) {
        return new RecipeItemViewModel(item);
    }

    public String GetFragmentName() {
        return "Recipes";
    }
}