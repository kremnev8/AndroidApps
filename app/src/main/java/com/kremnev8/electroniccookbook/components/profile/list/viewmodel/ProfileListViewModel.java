package com.kremnev8.electroniccookbook.components.profile.list.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.FooterItemViewModel;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.common.recycler.SimpleListViewModel;
import com.kremnev8.electroniccookbook.components.profile.edit.fragment.ProfileEditFragment;
import com.kremnev8.electroniccookbook.components.profile.list.itemviewmodel.ProfileItemViewModel;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IClickHandler;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.ILoginSuccessCallback;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileListViewModel extends SimpleListViewModel<Profile> implements IClickHandler<Profile>, ILoginSuccessCallback {

    private final IFragmentController fragmentController;
    private final IProfileProvider profileProvider;

    @Inject
    public ProfileListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IFragmentController fragmentController,IProfileProvider profileProvider) {
        super(handle, databaseExecutor);
        this.fragmentController = fragmentController;
        this.profileProvider = profileProvider;
        itemViewModelHolder.setFooter(new FooterItemViewModel(R.string.add_profile_label, this::addProfile));
        setData(databaseExecutor.getProfiles());
    }

    private void addProfile() {
        Bundle args = new Bundle();
        args.putParcelable(ProfileEditFragment.ProfileData, new Profile());

        MainActivity.Instance.setFragment(ProfileEditFragment.class, args);
    }

    @Override
    public ItemViewModel CreateInstance(Profile item) {
        return new ProfileItemViewModel(item, this);
    }

    @Override
    public void OnClicked(Profile item) {
        if (item.passwordRequired)
            fragmentController.showLoginDialog(this, item);
        else
            profileProvider.loginIntoProfile(item, "");
    }

    public void editItem(int position) {
        var list = getData();

        if (position >= 0 && position < list.size()) {
            Bundle args = new Bundle();
            args.putParcelable(ProfileEditFragment.ProfileData, list.get(position));

            MainActivity.Instance.setFragment(ProfileEditFragment.class, args);
        }
    }

    public void deleteItem(int position) {
        var list = getData();

        if (position >= 0 && position < list.size()) {
            databaseExecutor.deleteProfileById(list.get(position).id);
        }
    }

    @Override
    public void OnSuccessfulLogin() {

    }
}