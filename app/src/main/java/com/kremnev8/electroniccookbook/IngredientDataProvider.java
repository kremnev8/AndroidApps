package com.kremnev8.electroniccookbook;

import com.github.javafaker.Faker;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientDataProvider {

    public List<Ingredient> getIngredientData(){
        Faker faker = new Faker();
        var ingredients = new ArrayList<Ingredient>(50);

        for (int i = 0; i < 50; i++) {
            ingredients.add(new Ingredient(
                    i,
                    faker.food().ingredient(),
                    "",
                    faker.random().nextInt(0, 100).floatValue(),
                    "pc."
            ));
        }
        return ingredients;
    }

}
