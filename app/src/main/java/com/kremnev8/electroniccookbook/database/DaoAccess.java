package com.kremnev8.electroniccookbook.database;

import com.kremnev8.electroniccookbook.components.ingredient.database.IngredientDao;
import com.kremnev8.electroniccookbook.components.recipe.database.RecipeDao;
import com.kremnev8.electroniccookbook.components.recipe.database.RecipeIngredientCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.database.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.components.recipe.database.RecipeStepCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.database.RecipeStepDao;

interface DaoAccess {
    IngredientDao ingredientDao();
    RecipeDao recipeDao();
    RecipeStepDao recipeStepDao();
    RecipeIngredientDao recipeIngredientDao();
    RecipeStepCacheDao recipeStepCacheDao();
    RecipeIngredientCacheDao recipeIngredientCacheDao();
}
