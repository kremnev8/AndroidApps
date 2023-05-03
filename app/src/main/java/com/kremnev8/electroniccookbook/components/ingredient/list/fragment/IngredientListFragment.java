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
import com.kremnev8.electroniccookbook.common.recycler.IContextMenuPositionProvider;
import com.kremnev8.electroniccookbook.components.ingredient.edit.fragment.IngredientEditFragment;
import com.kremnev8.electroniccookbook.components.ingredient.list.viewmodel.IngredientListViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientListBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class IngredientListFragment extends Fragment implements IMenu {

    private FragmentIngredientListBinding binding;
    private IngredientListViewModel ingredientListViewModel;

    public IngredientListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIngredientListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        ingredientListViewModel = new ViewModelProvider(MainActivity.Instance).get(IngredientListViewModel.class);

        binding.setViewModel(ingredientListViewModel);
        binding.addButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putParcelable(IngredientEditFragment.InspectIngredient, new Ingredient());
            MainActivity.Instance.setFragment(IngredientEditFragment.class, args);
        });

        registerForContextMenu(binding.ingredientsList);

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    public int getPosition(){
        return ((IContextMenuPositionProvider) binding.ingredientsList.getAdapter()).getMenuPosition();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = getPosition();
        if (item.getItemId() == R.id.ctx_edit){
            ingredientListViewModel.editItem(position);
        }else if (item.getItemId() == R.id.ctx_delete){
            ingredientListViewModel.deleteItem(position);
        }
        return true;
    }

    @Override
    public int getMenuName() {
        return R.string.IngredientsListMenuName;
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