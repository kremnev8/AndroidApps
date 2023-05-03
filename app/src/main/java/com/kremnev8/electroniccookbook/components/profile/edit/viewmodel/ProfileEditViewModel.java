package com.kremnev8.electroniccookbook.components.profile.edit.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.SavedStateHandle;

import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.common.Passwords;
import com.kremnev8.electroniccookbook.common.SimpleViewModel;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileEditViewModel extends SimpleViewModel<Profile> implements IPhotoRequestCallback {

    private final IPhotoProvider photoProvider;
    private String editedPasswordText;

    @Inject
    ProfileEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IPhotoProvider photoProvider) {
        super(databaseExecutor);
        this.photoProvider = photoProvider;
    }

    @Bindable
    public String getPassword(){
        return editedPasswordText;
    }

    public void setPassword(String value){
        editedPasswordText = value;
        notifyPropertyChanged(BR.password);
    }

    public void confirmPassword(){
        if (!Strings.isNullOrEmpty(editedPasswordText)) {
            if (Strings.isNullOrEmpty(model.passwordSalt))
                model.passwordSalt = Passwords.getNextSalt();

            model.passwordHash = Passwords.hash(editedPasswordText, model.passwordSalt);
            model.passwordRequired = true;
        }else{
            model.passwordHash = "";
            model.passwordSalt = "";
            model.passwordRequired = false;
        }
    }

    public void saveData(){
        confirmPassword();
        databaseExecutor.insertOrUpdate(model);
    }

    public void addPhotoClicked(View view){
        handler.postDelayed(() -> photoProvider.requestPhoto(this), 100);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        model.profileImageUrl = imageUri;
        notifyChange();
    }
}