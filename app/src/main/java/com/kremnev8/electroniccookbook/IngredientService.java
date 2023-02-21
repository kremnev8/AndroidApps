package com.kremnev8.electroniccookbook;

import com.github.javafaker.Faker;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientService {

    public static IngredientService Instance;
    public List<Ingredient> ingredients;

    public IngredientService(){
        Faker faker = new Faker();
        ingredients = new ArrayList<>(50);

        for (int i = 0; i < 50; i++) {
            ingredients.add(new Ingredient(
                    i,
                    faker.food().ingredient(),
                    "",
                    faker.random().nextInt(0, 100).floatValue(),
                    "pc."
            ));
        }
        Instance = this;
    }

}
