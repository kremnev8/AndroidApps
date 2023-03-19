package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;

import com.kremnev8.electroniccookbook.ingredient.database.IngredientDao;
import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.recipe.database.RecipeExtendedDao;
import com.kremnev8.electroniccookbook.recipe.database.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.recipe.database.RecipeStepDao;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseExecutor implements IngredientDao, RecipeExtendedDao, RecipeStepDao, RecipeIngredientDao {

    private final ExecutorService executor;
    private final DaoAccess daoAccess;

    public DatabaseExecutor(DaoAccess daoInterface){
        this.daoAccess = daoInterface;
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public LiveData<List<Ingredient>> getIngredients() {
        return daoAccess.ingredientDao().getIngredients();
    }

    @Override
    public void insert(Ingredient ingredient) {
        executor.execute(() -> daoAccess.ingredientDao().insert(ingredient));
    }

    @Override
    public void update(Ingredient ingredient) {
        executor.execute(() -> daoAccess.ingredientDao().update(ingredient));
    }

    @Override
    public void delete(Ingredient ingredient) {
        executor.execute(() -> daoAccess.ingredientDao().delete(ingredient));
    }

    @Override
    public LiveData<List<Recipe>> getRecipes() {
        return daoAccess.recipeDao().getRecipes();
    }

    @Override
    public LiveData<Recipe> getRecipe(int id) {
        return daoAccess.recipeDao().getRecipe(id);
    }

    @Override
    public LiveData<List<Recipe>> getRecipesWithData() {
        LiveData<List<Recipe>> recipes = getRecipes();
        if (recipes.getValue() != null) {
            for (var recipe : recipes.getValue()) {
                recipe.ingredients = getRecipeIngredients(recipe.id);
                recipe.steps = getRecipeSteps(recipe.id);
            }
        }

        return recipes;
    }

    @Override
    public LiveData<Recipe> getRecipeWithData(int id) {
        LiveData<Recipe> recipe = getRecipe(id);
        if (recipe.getValue() != null) {
            recipe.getValue().ingredients = getRecipeIngredients(id);
            recipe.getValue().steps = getRecipeSteps(id);
        }

        return recipe;
    }

    @Override
    public long insert(Recipe recipe) {
        executor.execute(() -> daoAccess.recipeDao().insert(recipe));
        return -1;
    }

    public void insertWithCallback(Recipe recipe, IInsertCallback callback) {
        executor.execute(() -> {
            long id = daoAccess.recipeDao().insert(recipe);
            callback.onComplete(id);
        });
    }

    @Override
    public void update(Recipe recipe) {
        executor.execute(() -> daoAccess.recipeDao().update(recipe));
    }

    @Override
    public void deleteById(int id) {
        executor.execute(() -> daoAccess.recipeDao().deleteById(id));
    }

    @Override
    public void delete(Recipe recipe) {
        executor.execute(() -> daoAccess.recipeDao().delete(recipe));
    }

    @Override
    public void insertWithData(Recipe recipe) {
        if (recipe.ingredients != null && recipe.ingredients.getValue() != null) {
            for (var ingredient : recipe.ingredients.getValue()) {
                ingredient.recipe = recipe.id;
            }
        }
        if (recipe.steps != null && recipe.steps.getValue() != null) {
            for (var step: recipe.steps.getValue()) {
                step.recipe = recipe.id;
            }
        }
        executor.execute(() -> {
            daoAccess.recipeDao().insert(recipe);
            if (recipe.steps != null)
                daoAccess.recipeStepDao().insertAllSteps(recipe.steps.getValue());
            if (recipe.ingredients != null)
                daoAccess.recipeIngredientDao().insertAllIngredients(recipe.ingredients.getValue());
        });
    }

    @Override
    public LiveData<List<RecipeIngredient>> getRecipeIngredients(int id) {
        return daoAccess.recipeIngredientDao().getRecipeIngredients(id);
    }

    @Override
    public void insertAllIngredients(List<RecipeIngredient> ingredients) {
        executor.execute(() -> daoAccess.recipeIngredientDao().insertAllIngredients(ingredients));
    }

    @Override
    public LiveData<List<RecipeStep>> getRecipeSteps(int id) {
        return daoAccess.recipeStepDao().getRecipeSteps(id);
    }

    @Override
    public void insertStep(RecipeStep step) {
        executor.execute(() ->daoAccess.recipeStepDao().insertStep(step));
    }

    @Override
    public void insertAllSteps(List<RecipeStep> steps) {
        executor.execute(() -> daoAccess.recipeStepDao().insertAllSteps(steps));
    }
}
