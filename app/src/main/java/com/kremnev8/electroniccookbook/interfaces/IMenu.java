package com.kremnev8.electroniccookbook.interfaces;

public interface IMenu {
    int getMenuName();
    int getActionText();
    int getActionImage();

    void onAction();
}
