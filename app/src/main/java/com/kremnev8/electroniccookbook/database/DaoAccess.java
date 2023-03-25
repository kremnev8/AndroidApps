package com.kremnev8.electroniccookbook.database;

import com.kremnev8.electroniccookbook.ingredient.database.IngredientDao;
import com.kremnev8.electroniccookbook.recipe.database.RecipeDao;
import com.kremnev8.electroniccookbook.recipe.database.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.recipe.database.RecipeStepDao;
import com.kremnev8.electroniccookbook.recipeview.database.ViewCacheDao;

interface DaoAccess {
    IngredientDao ingredientDao();

    RecipeDao recipeDao();

    RecipeStepDao recipeStepDao();

    RecipeIngredientDao recipeIngredientDao();

    ViewCacheDao viewCacheDao();
}
