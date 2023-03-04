package com.kremnev8.electroniccookbook.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients",indices = @Index(value = {"id"},unique = true))
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "iconUri")
    public String iconUri;

    @ColumnInfo(name = "amount")
    public float amount;

    @ColumnInfo(name = "units")
    public String units;

    public Ingredient(){}

    @Ignore
    public Ingredient(int id, String name, String iconUri, float amount, String units) {
        this.id = id;
        this.name = name;
        this.iconUri = iconUri;
        this.amount = amount;
        this.units = units;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.iconUri);
        dest.writeFloat(this.amount);
        dest.writeString(this.units);
    }

    protected Ingredient(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.iconUri = in.readString();
        this.amount = in.readFloat();
        this.units = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
