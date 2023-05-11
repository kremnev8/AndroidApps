package com.kremnev8.electroniccookbook.components.profile.dialog.fragment;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.components.profile.dialog.viewmodel.LoginViewModel;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.databinding.FragmentLoginBinding;
import com.kremnev8.electroniccookbook.interfaces.ILoginSuccessCallback;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends DialogFragment {

    public static final String ProfileData = "ProfileData";

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;

    private ILoginSuccessCallback loginSuccessCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        loginViewModel = new ViewModelProvider(MainActivity.Instance).get(LoginViewModel.class);

        if (getArguments() != null) {
            Profile profile = getArguments().getParcelable(ProfileData);
            loginViewModel.setProfile(profile);
        }

        loginViewModel.setLoginSuccessCallback(loginSuccessCallback);

        binding.passwordField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login();
            }
            return false;
        });

        binding.loginButton.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            loginViewModel.login();
        });

        binding.setViewModel(loginViewModel);

        return binding.getRoot();

    }

    public void setLoginSuccessCallback(ILoginSuccessCallback loginSuccessCallback) {
        this.loginSuccessCallback = loginSuccessCallback;
        if (loginViewModel != null)
            loginViewModel.setLoginSuccessCallback(loginSuccessCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}