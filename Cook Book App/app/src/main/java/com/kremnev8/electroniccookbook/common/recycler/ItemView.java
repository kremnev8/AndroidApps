package com.kremnev8.electroniccookbook.common.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kremnev8.electroniccookbook.common.ContextMenuKind;

public class ItemView extends ConstraintLayout {

    private final ItemExtraInfo extraInfo = new ItemExtraInfo();

    public ItemView(@NonNull Context context) {
        super(context);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setInfo(int index, ContextMenuKind menuKind) {
        extraInfo.index = index;
        extraInfo.menuKind = menuKind;
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return extraInfo;
    }

    public class ItemExtraInfo implements ContextMenu.ContextMenuInfo{
        public int index;
        public ContextMenuKind menuKind;
    }
}
