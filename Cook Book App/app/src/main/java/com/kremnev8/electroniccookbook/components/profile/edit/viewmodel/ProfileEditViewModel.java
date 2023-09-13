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
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;
import com.kremnev8.electroniccookbook.interfaces.IMediaRequestCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileEditViewModel extends SimpleViewModel<Profile> implements IMediaRequestCallback {

    private final IMediaProvider photoProvider;
    private String editedPasswordText;

    @Inject
    ProfileEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IMediaProvider photoProvider) {
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
        databaseExecutor.upsert(model);
    }

    public void addPhotoClicked(View view){
        handler.postDelayed(() -> photoProvider.requestMedia(this, false), 100);
    }

    @Override
    public void onMediaSelected(String imageUri) {
        model.profileImageUrl = imageUri;
        notifyChange();
    }
}