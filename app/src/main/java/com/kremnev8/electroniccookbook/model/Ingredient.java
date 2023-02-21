package com.kremnev8.electroniccookbook.model;

public class Ingredient {
    public Integer id;
    public String name;
    public String iconUri;
    public Float amount;
    public String units;

    public Ingredient(Integer id, String name, String iconUri, Float amount, String units) {
        this.id = id;
        this.name = name;
        this.iconUri = iconUri;
        this.amount = amount;
        this.units = units;
    }
}
