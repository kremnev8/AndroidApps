package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;

import com.kremnev8.electroniccookbook.components.ingredient.model.IngredientDao;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.profile.model.ProfileDao;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeExtendedDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepDao;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewStepCache;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class DatabaseExecutor implements
        IngredientDao,
        RecipeExtendedDao,
        RecipeStepDao,
        RecipeIngredientDao,
        RecipeStepCacheDao,
        RecipeIngredientCacheDao,
        ProfileDao {

    private final ExecutorService executor;
    private final DaoAccess daoAccess;

    public DatabaseExecutor(DaoAccess daoInterface) {
        this.daoAccess = daoInterface;
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public LiveData<List<Ingredient>> getIngredients(int profileId) {
        return daoAccess.ingredientDao().getIngredients(profileId);
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
    public void deleteIngredientById(int id) {
        executor.execute(() -> daoAccess.ingredientDao().deleteIngredientById(id));
    }

    @Override
    public void delete(Ingredient ingredient) {
        executor.execute(() -> daoAccess.ingredientDao().delete(ingredient));
    }

    @Override
    public LiveData<List<Recipe>> getRecipes(int profileId) {
        return daoAccess.recipeDao().getRecipes(profileId);
    }

    @Override
    public LiveData<Recipe> getRecipe(int id) {
        return daoAccess.recipeDao().getRecipe(id);
    }

    @Override
    public LiveData<List<Recipe>> getRecipesWithData(int profileId) {
        LiveData<List<Recipe>> recipes = getRecipes(profileId);
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
    public long upsert(Recipe recipe) {
        executor.execute(() -> daoAccess.recipeDao().upsert(recipe));
        return -1;
    }

    public void upsertWithCallback(Recipe recipe, IInsertCallback callback) {
        executor.execute(() -> {
            long id = daoAccess.recipeDao().upsert(recipe);
            callback.onComplete(id);
        });
    }

    @Override
    public void update(Recipe recipe) {
        executor.execute(() -> {
            daoAccess.recipeDao().update(recipe);
            daoAccess.recipeStepCacheDao().clearRecipeCache(recipe.id);
        });
    }

    @Override
    public LiveData<List<Profile>> getProfiles() {
        return daoAccess.profileDao().getProfiles();
    }

    @Override
    public Flowable<Profile> getProfile(int id) {
        return daoAccess.profileDao().getProfile(id);
    }

    @Override
    public boolean hasProfile(int id) {
        return daoAccess.profileDao().hasProfile(id);
    }

    @Override
    public long upsert(Profile profile) {
        executor.execute(() -> daoAccess.profileDao().upsert(profile));
        return 0;
    }

    @Override
    public void deleteProfileById(int id) {
        executor.execute(() -> daoAccess.profileDao().deleteProfileById(id));
    }

    @Override
    public void deleteRecipeById(int id) {
        executor.execute(() -> daoAccess.recipeDao().deleteRecipeById(id));
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
            for (var step : recipe.steps.getValue()) {
                step.recipe = recipe.id;
            }
        }
        executor.execute(() -> {
            daoAccess.recipeDao().upsert(recipe);
            if (recipe.steps != null)
                daoAccess.recipeStepDao().upsertAllSteps(recipe.steps.getValue());
            if (recipe.ingredients != null)
                daoAccess.recipeIngredientDao().upsertAllIngredients(recipe.ingredients.getValue());

            daoAccess.recipeStepCacheDao().clearRecipeCache(recipe.id);
        });
    }

    @Override
    public LiveData<List<RecipeIngredient>> getRecipeIngredients(int id) {
        return daoAccess.recipeIngredientDao().getRecipeIngredients(id);
    }

    @Override
    public Single<List<RecipeIngredient>> getRecipeIngredientsDirect(int id) {
        return daoAccess.recipeIngredientDao().getRecipeIngredientsDirect(id);
    }

    @Override
    public void upsertAllIngredients(List<RecipeIngredient> ingredients) {
        executor.execute(() -> daoAccess.recipeIngredientDao().upsertAllIngredients(ingredients));
    }

    @Override
    public void upsertIngredient(RecipeIngredient ingredient) {
        executor.execute(() -> daoAccess.recipeIngredientDao().upsertIngredient(ingredient));
    }

    @Override
    public void deleteIngredient(RecipeIngredient ingredient) {
        executor.execute(() -> {
            daoAccess.recipeIngredientDao().deleteIngredient(ingredient);
            daoAccess.recipeIngredientCacheDao().clearIngredientCache(ingredient.recipe);
        });
    }

    @Override
    public LiveData<List<RecipeStep>> getRecipeSteps(int id) {
        return daoAccess.recipeStepDao().getRecipeSteps(id);
    }

    @Override
    public Single<List<RecipeStep>> getRecipeStepsDirect(int id) {
        return daoAccess.recipeStepDao().getRecipeStepsDirect(id);
    }

    @Override
    public void upsertStep(RecipeStep step) {
        executor.execute(() -> {
            daoAccess.recipeStepDao().upsertStep(step);
            daoAccess.recipeStepCacheDao().clearRecipeCache(step.recipe);
        });
    }

    @Override
    public void upsertAllSteps(List<RecipeStep> steps) {
        executor.execute(() -> daoAccess.recipeStepDao().upsertAllSteps(steps));
    }

    @Override
    public void deleteStep(RecipeStep step) {
        executor.execute(() -> {
            daoAccess.recipeStepDao().deleteStep(step);
            daoAccess.recipeStepCacheDao().clearRecipeCache(step.recipe);
        });
    }

    @Override
    public LiveData<List<RecipeViewStepCache>> getRecipeCache(int recipeId) {
        return daoAccess.recipeStepCacheDao().getRecipeCache(recipeId);
    }

    @Override
    public Single<Boolean> hasRecipeCache(int recipeId) {
        return daoAccess.recipeStepCacheDao().hasRecipeCache(recipeId);
    }

    public Single<LiveData<List<RecipeViewStepCache>>> getOrCreateRecipeCache(int recipeId) {
        var cacheRequest = daoAccess.recipeStepCacheDao().hasRecipeCache(recipeId);
        return cacheRequest.map(hasCache -> {
            if (hasCache) {
                return daoAccess.recipeStepCacheDao().getRecipeCache(recipeId);
            }

            var stepsRequest = daoAccess.recipeStepDao().getRecipeStepsDirect(recipeId);
            var finalRes = stepsRequest.map(steps -> {
                if (steps != null) {
                    for (var step : steps) {
                        daoAccess.recipeStepCacheDao().upsert(new RecipeStepCache(recipeId, step.id));
                    }
                }
                return daoAccess.recipeStepCacheDao().getRecipeCache(recipeId);
            });

            return finalRes.blockingGet();
        });
    }

    @Override
    public long upsert(RecipeStepCache recipe) {
        executor.execute(() -> daoAccess.recipeStepCacheDao().upsert(recipe));
        return 0;
    }

    @Override
    public void clearRecipeCache(int recipeId) {
        executor.execute(() -> daoAccess.recipeStepCacheDao().clearRecipeCache(recipeId));
    }

    @Override
    public LiveData<List<RecipeViewIngredientCache>> getIngredientCache(int recipeId) {
        return daoAccess.recipeIngredientCacheDao().getIngredientCache(recipeId);
    }

    @Override
    public Single<Boolean> hasIngredientCache(int recipeId) {
        return daoAccess.recipeIngredientCacheDao().hasIngredientCache(recipeId);
    }

    @Override
    public long upsert(RecipeIngredientCache recipe) {
        executor.execute(() -> daoAccess.recipeIngredientCacheDao().upsert(recipe));
        return 0;
    }

    @Override
    public void clearIngredientCache(int recipeId) {
        executor.execute(() -> daoAccess.recipeIngredientCacheDao().clearIngredientCache(recipeId));
    }

    public Single<LiveData<List<RecipeViewIngredientCache>>> getOrCreateIngredientCache(int recipeId) {
        var cacheRequest = daoAccess.recipeIngredientCacheDao().hasIngredientCache(recipeId);
        return cacheRequest.map(hasCache -> {
            if (hasCache) {
                return daoAccess.recipeIngredientCacheDao().getIngredientCache(recipeId);
            }

            var stepsRequest = daoAccess.recipeIngredientDao().getRecipeIngredientsDirect(recipeId);
            var finalRes = stepsRequest.map(ingredients -> {
                if (ingredients != null) {
                    for (var ingredient : ingredients) {
                        daoAccess.recipeIngredientCacheDao().upsert(new RecipeIngredientCache(recipeId, ingredient.id));
                    }
                }
                return daoAccess.recipeIngredientCacheDao().getIngredientCache(recipeId);
            });

            return finalRes.blockingGet();
        });
    }
}
