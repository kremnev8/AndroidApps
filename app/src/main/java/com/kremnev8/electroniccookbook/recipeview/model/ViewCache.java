package com.kremnev8.electroniccookbook.recipeview.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

@Entity(tableName = "viewCache",
        indices = @Index(value = {"id"},unique = true),
        foreignKeys = {
        @ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = RecipeStep.class,
                parentColumns = "id",
                childColumns = "stepId")
        })
public class ViewCache implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipeId", index = true)
    public int recipeId;

    @ColumnInfo(name = "stepId", index = true)
    public int stepId;

    @ColumnInfo(name = "stepComplete")
    public boolean stepComplete;

    @Ignore
    public RecipeStep step;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.recipeId);
        dest.writeInt(this.stepId);
        dest.writeByte(this.stepComplete ? (byte) 1 : (byte) 0);
    }

    public ViewCache() {
    }

    @Ignore
    public ViewCache(int recipeId, int stepId) {
        this.recipeId = recipeId;
        this.stepId = stepId;
    }

    protected ViewCache(Parcel in) {
        this.id = in.readInt();
        this.recipeId = in.readInt();
        this.stepId = in.readInt();
        this.stepComplete = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ViewCache> CREATOR = new Parcelable.Creator<>() {
        @Override
        public ViewCache createFromParcel(Parcel source) {
            return new ViewCache(source);
        }

        @Override
        public ViewCache[] newArray(int size) {
            return new ViewCache[size];
        }
    };
}
