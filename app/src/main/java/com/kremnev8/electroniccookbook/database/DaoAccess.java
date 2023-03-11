package com.kremnev8.electroniccookbook.database;

interface DaoAccess {
    IngredientDao ingredientDao();

    RecipeDao recipeDao();

    RecipeStepDao recipeStepDao();

    RecipeIngredientDao recipeIngredientDao();
}
