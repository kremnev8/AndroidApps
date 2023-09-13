package com.kremnev8.electroniccookbook.interfaces;

public interface IDrawerController {
    void toggleDrawer(DrawerKind kind);
    void closeDrawer(DrawerKind kind);
    void addOnDrawerStateChangedListener(IAction action);
    void removeListener(IAction action);

    enum DrawerKind {
        NAVIGATION,
        FILTERS
    }
}
