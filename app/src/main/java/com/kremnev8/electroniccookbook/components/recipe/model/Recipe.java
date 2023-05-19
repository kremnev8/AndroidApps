package com.kremnev8.electroniccookbook.components.recipe.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kremnev8.electroniccookbook.components.profile.model.Profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "recipe",
        indices = @Index(value = {"id"}, unique = true),
        foreignKeys = {
        @ForeignKey(entity = Profile.class,
                parentColumns = "id",
                childColumns = "profileId",
                onDelete = ForeignKey.CASCADE)
})
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "profileId", index = true, defaultValue = "1")
    public int profileId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "nutritionInfo")
    public String nutritionInfo;

    @ColumnInfo(name = "yield", defaultValue = "1")
    public int yield;

    @ColumnInfo(name = "imageUri")
    public String imageUri;

    @ColumnInfo(name = "lastModified", defaultValue = "0")
    public Date lastModified;

    @Ignore
    @Nullable
    public List<RecipeStep> steps;

    @Ignore
    @Nullable
    public List<RecipeIngredient> ingredients;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.profileId);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.nutritionInfo);
        dest.writeInt(this.yield);
        dest.writeString(this.imageUri);
        dest.writeLong(this.lastModified != null ? this.lastModified.getTime() : -1);
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        this.id = in.readInt();
        this.profileId = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.nutritionInfo = in.readString();
        this.yield = in.readInt();
        this.imageUri = in.readString();
        long tmpLastModified = in.readLong();
        this.lastModified = tmpLastModified == -1 ? null : new Date(tmpLastModified);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
