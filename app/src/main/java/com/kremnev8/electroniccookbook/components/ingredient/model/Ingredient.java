package com.kremnev8.electroniccookbook.components.ingredient.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.common.primitives.Floats;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity(tableName = "ingredients",indices = {
        @Index(value = {"id"},unique = true),
        @Index(value = {"name"},unique = true)
}, foreignKeys = {
        @ForeignKey(entity = Profile.class,
                parentColumns = "id",
                childColumns = "profileId",
                onDelete = ForeignKey.CASCADE)
})
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "profileId", index = true, defaultValue = "1")
    public int profileId;

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

    public String getAmountString()
    {
        return getAmountString(amount, units);
    }

    public void setAmount(AmountPair pair){
        amount = pair.amount;
        units = pair.units;
    }

    public static String getAmountString(float amount, String units){
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(3);
        String amountFormatted = df.format(amount);

        if (amount != 0 && units != null && units.length() > 0)
            return amountFormatted + " " + units;
        else if (amount == 0 && units != null && units.length() > 0)
            return units;
        else if (amount != 0)
            return amountFormatted;
        else
            return "";
    }

    public static AmountPair ParseAmountString(String string){
        AmountPair pair = new AmountPair();
        String[] parts = string.split(" ");
        if (parts.length == 1){
            pair.amount = 0;
            pair.units = string;
        }else if (parts.length > 1){
            Float amount = Floats.tryParse(parts[0]);
            if (amount == null){
                pair.amount = 0;
                pair.units = string;
            }else {
                pair.amount = amount;
                pair.units = Arrays.stream(parts).skip(1).collect(Collectors.joining(""));
            }
        }
        return pair;
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

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public static class AmountPair{
        public float amount;
        public String units;
    }
}
