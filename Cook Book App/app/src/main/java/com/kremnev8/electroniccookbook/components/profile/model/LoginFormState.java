package com.kremnev8.electroniccookbook.components.profile.model;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.kremnev8.electroniccookbook.R;

/**
 * Data validation state of the login form.
 */
public class LoginFormState {
    private int message = R.string.empty;
    private boolean showMessage = true;
    @ColorInt
    private int messageColor;

    public void clear(){
        message = R.string.empty;
        showMessage = false;
    }

    public void showError(int messageId){
        message = messageId;
        showMessage = true;
        messageColor = Color.RED;
    }

    public void showMessage(int messageId, @ColorInt int color){
        message = messageId;
        showMessage = true;
        messageColor = color;
    }

    @Nullable
    public Integer getMessage() {
        return message;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public int getMessageColor() {
        return messageColor;
    }
}