package com.kremnev8.electroniccookbook.components.profile.dialog.viewmodel;

import android.graphics.Color;

import androidx.databinding.Bindable;
import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ObservableViewModel;
import com.kremnev8.electroniccookbook.components.profile.model.LoginFormState;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.interfaces.ILoginSuccessCallback;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ObservableViewModel {

    private final IProfileProvider profileProvider;
    private ILoginSuccessCallback loginSuccessCallback;

    private final LoginFormState loginFormState = new LoginFormState();
    private Profile profile;
    private String password;

    @Inject
    LoginViewModel(SavedStateHandle handle, IProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    @Bindable
    public LoginFormState getLoginFormState() {
        return loginFormState;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        boolean valid = isPasswordValid(password);
        if (valid)
            loginFormState.clear();
        else
            loginFormState.showError(R.string.invalid_password);
        notifyChange();
    }

    public void setProfile(Profile profile){
        this.profile = profile;
        password = "";
        loginFormState.clear();
        notifyChange();
    }

    public void setLoginSuccessCallback(ILoginSuccessCallback loginSuccessCallback) {
        this.loginSuccessCallback = loginSuccessCallback;
    }

    public void login() {
        boolean result = profileProvider.loginIntoProfile(profile, password);
        if (result){
            loginFormState.showMessage(R.string.login_successful_message, Color.WHITE);
            notifyChange();

            if (loginSuccessCallback != null)
                loginSuccessCallback.OnSuccessfulLogin();
        }else{
            loginFormState.showError(R.string.login_failed);
        }
        notifyChange();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}