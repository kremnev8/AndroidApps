package com.kremnev8.electroniccookbook.components.profile.list.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.ItemView;
import com.kremnev8.electroniccookbook.components.profile.list.viewmodel.ProfileListViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentProfileListBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileListFragment extends Fragment implements IMenu {

    private FragmentProfileListBinding binding;
    private ProfileListViewModel ingredientListViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        ingredientListViewModel = new ViewModelProvider(MainActivity.Instance).get(ProfileListViewModel.class);

        binding.setViewModel(ingredientListViewModel);
        registerForContextMenu(binding.profileList);

        return binding.getRoot();
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
        return R.string.profiles_label;
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