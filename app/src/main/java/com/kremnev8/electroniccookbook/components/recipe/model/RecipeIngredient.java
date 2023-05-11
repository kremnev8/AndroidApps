package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;

import java.text.DecimalFormat;

@Entity(tableName = "recipeIngredient",
        indices = @Index(value = {"id"}, unique = true),
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "id",
                        childColumns = "recipe",
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

    @ColumnInfo(name = "amount")
    public float amount;

    @ColumnInfo(name = "units")
    public String units;


    public String getAmountString(){
        return Ingredient.getAmountString(amount, units);
    }

    public void setAmount(Ingredient.AmountPair pair){
        amount = pair.amount;
        units = pair.units;
    }
}
