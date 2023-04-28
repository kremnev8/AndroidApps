package com.kremnev8.electroniccookbook.components.tabs.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.common.ObservableViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.list.fragment.IngredientListFragment;
import com.kremnev8.electroniccookbook.components.recipe.list.fragment.RecipesListFragment;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NavigationDrawerViewModel extends ObservableViewModel {
    private Profile profile;
    private final IDrawerController drawerController;
    private final IFragmentController fragmentController;

    @Inject
    public NavigationDrawerViewModel(SavedStateHandle handle, IDrawerController drawerController, IFragmentController fragmentController) {
        this.drawerController = drawerController;
        this.fragmentController = fragmentController;
    }

    @Bindable
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile){
        this.profile = profile;
        notifyChange();
    }

    public void ingredientsButtonClicked(View view){
        fragmentController.setFragment(IngredientListFragment.class, null);
        drawerController.closeDrawer(IDrawerController.DrawerKind.NAVIGATION);
    }

    public void recipesButtonClicked(View view){
        fragmentController.setFragment(RecipesListFragment.class, null);
        drawerController.closeDrawer(IDrawerController.DrawerKind.NAVIGATION);
    }

    public void settingsButtonClicked(View view){

    }

    public void changeProfileClicked(View view){

    }
}