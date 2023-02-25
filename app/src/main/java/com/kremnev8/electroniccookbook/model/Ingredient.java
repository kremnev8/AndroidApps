package com.kremnev8.electroniccookbook.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients",indices = @Index(value = {"id"},unique = true))
public class Ingredient {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "iconUrl")
    public String iconUrl;

    @ColumnInfo(name = "amount")
    public float amount;

    @ColumnInfo(name = "units")
    public String units;

    public Ingredient(){}

    @Ignore
    public Ingredient(int id, String name, String iconUrl, float amount, String units) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.amount = amount;
        this.units = units;
    }
}
