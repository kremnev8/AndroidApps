package com.kremnev8.electroniccookbook.components.recipe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ShowRecipeData implements Parcelable {
    public int recipeId;
    public int stepId;

    public ShowRecipeData(int recipeId, int stepId) {
        this.recipeId = recipeId;
        this.stepId = stepId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.recipeId);
        dest.writeInt(this.stepId);
    }

    protected ShowRecipeData(Parcel in) {
        this.recipeId = in.readInt();
        this.stepId = in.readInt();
    }

    public static final Parcelable.Creator<ShowRecipeData> CREATOR = new Parcelable.Creator<>() {
        @Override
        public ShowRecipeData createFromParcel(Parcel source) {
            return new ShowRecipeData(source);
        }

        @Override
        public ShowRecipeData[] newArray(int size) {
            return new ShowRecipeData[size];
        }
    };
}
