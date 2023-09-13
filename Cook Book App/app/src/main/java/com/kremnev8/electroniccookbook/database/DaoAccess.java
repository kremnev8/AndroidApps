package com.kremnev8.electroniccookbook.database;

import com.kremnev8.electroniccookbook.components.ingredient.model.IngredientDao;
import com.kremnev8.electroniccookbook.components.profile.model.ProfileDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepDao;

interface DaoAccess {
    IngredientDao ingredientDao();
    RecipeDao recipeDao();
    RecipeStepDao recipeStepDao();
    RecipeIngredientDao recipeIngredientDao();
    RecipeStepCacheDao recipeStepCacheDao();
    RecipeIngredientCacheDao recipeIngredientCacheDao();
    ProfileDao profileDao();
}
