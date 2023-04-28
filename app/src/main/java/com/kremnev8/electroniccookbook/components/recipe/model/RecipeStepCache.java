package com.kremnev8.electroniccookbook.components.recipe.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipeStepCache",
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
public class RecipeStepCache implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipeId", index = true)
    public int recipeId;

    @ColumnInfo(name = "stepId", index = true)
    public int stepId;

    @ColumnInfo(name = "stepComplete")
    public boolean stepComplete;

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

    public RecipeStepCache() {
    }

    @Ignore
    public RecipeStepCache(int recipeId, int stepId) {
        this.recipeId = recipeId;
        this.stepId = stepId;
    }

    protected RecipeStepCache(Parcel in) {
        this.id = in.readInt();
        this.recipeId = in.readInt();
        this.stepId = in.readInt();
        this.stepComplete = in.readByte() != 0;
    }

    public static final Parcelable.Creator<RecipeStepCache> CREATOR = new Parcelable.Creator<>() {
        @Override
        public RecipeStepCache createFromParcel(Parcel source) {
            return new RecipeStepCache(source);
        }

        @Override
        public RecipeStepCache[] newArray(int size) {
            return new RecipeStepCache[size];
        }
    };
}
