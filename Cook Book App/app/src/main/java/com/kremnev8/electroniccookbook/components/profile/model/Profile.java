package com.kremnev8.electroniccookbook.components.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile",indices = @Index(value = {"id"},unique = true))
public class Profile implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profileImage")
    public String profileImageUrl;

    @ColumnInfo(name = "passwordRequired")
    public boolean passwordRequired;

    @ColumnInfo(name = "passwordSalt")
    public String passwordSalt;

    @ColumnInfo(name = "passwordHash")
    public String passwordHash;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.profileImageUrl);
        dest.writeByte(this.passwordRequired ? (byte) 1 : (byte) 0);
        dest.writeString(this.passwordSalt);
        dest.writeString(this.passwordHash);
    }

    public Profile() {
    }

    protected Profile(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.profileImageUrl = in.readString();
        this.passwordRequired = in.readByte() != 0;
        this.passwordSalt = in.readString();
        this.passwordHash = in.readString();
    }

    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
