package com.kremnev8.electroniccookbook.common.recycler;

import com.kremnev8.electroniccookbook.common.ContextMenuKind;

public interface IHasContextMenu {
    int getMenuResId();
    ContextMenuKind getMenuKind();
}
