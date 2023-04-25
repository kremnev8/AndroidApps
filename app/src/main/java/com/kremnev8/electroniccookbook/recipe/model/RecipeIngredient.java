package com.kremnev8.electroniccookbook.recipe.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;

import java.text.DecimalFormat;

@Entity(tableName = "recipeIngredient",
        indices = @Index(value = {"id"}, unique = true),
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "id",
                        childColumns = "recipe",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Ingredient.class,
                        parentColumns = "id",
                        childColumns = "ingredient",
                        onDelete = ForeignKey.CASCADE)
        })
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipe", index = true)
    public int recipe;

    @ColumnInfo(name = "ingredientName")
    public String ingredientName;

    @ColumnInfo(name = "ingredient", index = true)
    public Integer ingredient;

    @ColumnInfo(name = "amount")
    public float amount;

    @ColumnInfo(name = "units")
    public String units;


    public String getAmountString(){
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
}
