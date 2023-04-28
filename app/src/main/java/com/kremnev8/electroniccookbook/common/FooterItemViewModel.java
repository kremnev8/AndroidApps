package com.kremnev8.electroniccookbook.common;

import android.view.View;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;

public class FooterItemViewModel extends ItemViewModel {

    public final int footerText;
    public final IFooterCallback callback;

    public FooterItemViewModel(int footerText, IFooterCallback callback){
        this.footerText = footerText;
        this.callback = callback;

    }

    public void footerClicked(View view){
        callback.onAddItem();
    }

    @Override
    public void setItem(Object item) {
    }

    @Override
    public long getItemId() {
        return -1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_footer;
    }

    @Override
    public int getViewType() {
        return 2;
    }

    public interface IFooterCallback{
        void onAddItem();
    }
}
