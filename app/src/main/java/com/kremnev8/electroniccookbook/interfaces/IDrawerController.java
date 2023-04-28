package com.kremnev8.electroniccookbook.interfaces;

public interface IDrawerController {
    void toggleDrawer(DrawerKind kind);
    void closeDrawer(DrawerKind kind);

    enum DrawerKind {
        NAVIGATION,
        FILTERS
    }
}
