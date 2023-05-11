package com.kremnev8.electroniccookbook.components.recipe.list.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.IContextMenuPositionProvider;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditFragment;
import com.kremnev8.electroniccookbook.components.recipe.list.viewmodel.RecipesListViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipesListBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipesListFragment extends Fragment implements IMenu {

    private RecipesListViewModel recipesListViewModel;
    private FragmentRecipesListBinding binding;


    public static RecipesListFragment newInstance() {
        return new RecipesListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipesListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        recipesListViewModel = new ViewModelProvider(this).get(RecipesListViewModel.class);
        binding.setViewModel(recipesListViewModel);

        binding.addButton.setOnClickListener(v -> recipesListViewModel.addIngredient());

        registerForContextMenu(binding.recipeList);

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    public int getPosition(){
        return ((IContextMenuPositionProvider) binding.recipeList.getAdapter()).getMenuPosition();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = getPosition();
        if (item.getItemId() == R.id.ctx_edit){
            recipesListViewModel.editItem(position);
        }else if (item.getItemId() == R.id.ctx_delete){
            recipesListViewModel.deleteItem(position);
        }
        return true;
    }

    @Override
    public int getMenuName() {
        return R.string.recipe_label;
    }

    @Override
    public int getActionText() {
        return 0;
    }

    @Override
    public int getActionImage() {
        return 0;
    }

    @Override
    public void onAction() {

    }
}