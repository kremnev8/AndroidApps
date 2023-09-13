package com.kremnev8.electroniccookbook.components.profile.edit.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ParcelableUtil;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.profile.edit.viewmodel.ProfileEditViewModel;
import com.kremnev8.electroniccookbook.components.profile.list.fragment.ProfileListFragment;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.databinding.FragmentProfileEditBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileEditFragment
        extends Fragment
        implements IMenu, TextView.OnEditorActionListener {

    public static final String ProfileData = "ProfileData";

    private FragmentProfileEditBinding binding;
    private ProfileEditViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel = new ViewModelProvider(MainActivity.Instance).get(ProfileEditViewModel.class);

        if (getArguments() != null) {
            Profile profile = ParcelableUtil.GetParcelable(getArguments(), ProfileData, Profile.class);
            viewModel.setData(profile);
        }

        binding.setViewModel(viewModel);
        binding.passwordInput.setOnEditorActionListener(this);

        return binding.getRoot();
    }
    @Override
    public int getMenuName() {
        return R.string.edit_profile_label;
    }

    @Override
    public int getActionText() {
        return R.string.save_label;
    }

    @Override
    public int getActionImage() {
        return 0;
    }

    @Override
    public void onAction() {
        viewModel.saveData();
        MainActivity.Instance.setFragment(ProfileListFragment.class, null);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            viewModel.confirmPassword();
            return true;
        }
        return false;
    }
}