package com.kremnev8.electroniccookbook.components.profile.list.itemviewmodel;

import android.view.View;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.IHasContextMenu;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.interfaces.IClickHandler;

public class ProfileItemViewModel extends ItemViewModel implements IHasContextMenu {
    public Profile profile;
    private final IClickHandler<Profile> clickHandler;

    public ProfileItemViewModel(Profile profile, IClickHandler<Profile> clickHandler) {
        this.profile = profile;
        this.clickHandler = clickHandler;
    }

    public void selectButtonPressed(View view){
        clickHandler.OnClicked(profile);
    }

    @Override
    public void setItem(Object item) {
        profile = (Profile) item;
    }

    @Override
    public long getItemId() {
        return profile.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_profile_view;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getMenuResId() {
        return R.menu.edit_menu;
    }
}
