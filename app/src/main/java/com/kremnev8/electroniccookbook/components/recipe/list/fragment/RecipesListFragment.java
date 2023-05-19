package com.kremnev8.electroniccookbook.components.recipe.list.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ContextMenuKind;
import com.kremnev8.electroniccookbook.common.recycler.ItemView;
import com.kremnev8.electroniccookbook.components.recipe.list.viewmodel.RecipesListViewModel;
import com.kremnev8.electroniccookbook.components.tabs.model.SearchData;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipesListBinding;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IMenu;
import com.kremnev8.electroniccookbook.interfaces.ISearchStateProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipesListFragment extends Fragment implements IMenu {

    private RecipesListViewModel recipesListViewModel;
    private FragmentRecipesListBinding binding;

    @Inject
    IDrawerController drawerController;
    @Inject
    ISearchStateProvider searchStateProvider;

    public static RecipesListFragment newInstance() {
        return new RecipesListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipesListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        recipesListViewModel = new ViewModelProvider(this).get(RecipesListViewModel.class);
        binding.setViewModel(recipesListViewModel);

        binding.addButton.setOnClickListener(v -> recipesListViewModel.addRecipe());

        registerForContextMenu(binding.recipeList);

        searchStateProvider.getSearchData().observe(getViewLifecycleOwner(), this::onSearchChanged);

        return binding.getRoot();
    }

    public void onSearchChanged(SearchData searchData){
        recipesListViewModel.onSearchChanged(searchData);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getParentFragmentManager().beginTransaction().detach(RecipesListFragment.this).commit();
        getParentFragmentManager().beginTransaction().attach(RecipesListFragment.this).commit();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ItemView.ItemExtraInfo extra = (ItemView.ItemExtraInfo)item.getMenuInfo();
        if (extra.menuKind != ContextMenuKind.RECIPE) return false;


        if (item.getItemId() == R.id.ctx_edit){
            recipesListViewModel.editItem(extra.index);
        }else if (item.getItemId() == R.id.ctx_delete){
            recipesListViewModel.deleteItem(extra.index);
        }
        return true;
    }

    @Override
    public int getMenuName() {
        return R.string.recipes_label;
    }

    @Override
    public int getActionText() {
        return 0;
    }

    @Override
    public int getActionImage() {
        return R.drawable.ic_filters;
    }

    @Override
    public void onAction() {
        drawerController.toggleDrawer(IDrawerController.DrawerKind.FILTERS);
    }
}