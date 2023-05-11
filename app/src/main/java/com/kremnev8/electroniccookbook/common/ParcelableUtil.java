package com.kremnev8.electroniccookbook.common;

import android.os.Bundle;
import android.os.Parcelable;

import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;

@SuppressWarnings("deprecation")
public class ParcelableUtil {

    public static <T extends Parcelable> T GetParcelable(Bundle bundle, String key, Class<T> clazz){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelable(key, clazz);
        }else{
            return bundle.getParcelable(key);
        }
    }
}
