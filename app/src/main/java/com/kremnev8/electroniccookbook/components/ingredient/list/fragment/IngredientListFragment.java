package com.kremnev8.electroniccookbook.components.ingredient.list.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.ItemView;
import com.kremnev8.electroniccookbook.components.ingredient.list.viewmodel.IngredientListViewModel;
import com.kremnev8.electroniccookbook.components.tabs.model.SearchData;
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientListBinding;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IMenu;
import com.kremnev8.electroniccookbook.interfaces.ISearchStateProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class IngredientListFragment extends Fragment implements IMenu {

    private FragmentIngredientListBinding binding;
    private IngredientListViewModel ingredientListViewModel;
    @Inject
    IDrawerController drawerController;
    @Inject
    ISearchStateProvider searchStateProvider;

    public IngredientListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIngredientListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        ingredientListViewModel = new ViewModelProvider(MainActivity.Instance).get(IngredientListViewModel.class);

        binding.setViewModel(ingredientListViewModel);
        binding.addButton.setOnClickListener(v -> ingredientListViewModel.addIngredient());

        registerForContextMenu(binding.ingredientsList);

        searchStateProvider.getSearchData().observe(getViewLifecycleOwner(), this::onSearchChanged);

        return binding.getRoot();
    }

    public void onSearchChanged(SearchData searchData){
        ingredientListViewModel.onSearchChanged(searchData);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ItemView.ItemExtraInfo extra = (ItemView.ItemExtraInfo)item.getMenuInfo();
        int position = extra.index;
        
        if (item.getItemId() == R.id.ctx_edit){
            ingredientListViewModel.editItem(position);
        }else if (item.getItemId() == R.id.ctx_delete){
            ingredientListViewModel.deleteItem(position);
        }
        return true;
    }

    @Override
    public int getMenuName() {
        return R.string.ingredients_label;
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