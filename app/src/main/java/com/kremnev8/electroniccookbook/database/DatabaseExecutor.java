package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.ingredient.model.IngredientDao;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.profile.model.ProfileDao;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeExtendedDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewStepCache;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
    public Ingredient findIngredient(String name) {
        return daoAccess.ingredientDao().findIngredient(name);
    }

    @Override
    public void insert(Ingredient ingredient) {
        ingredient.lastModified = new Date();
        executor.execute(() -> daoAccess.ingredientDao().insert(ingredient));
    }

    @Override
    public void update(Ingredient ingredient) {
        ingredient.lastModified = new Date();
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
    public Flowable<List<Recipe>> getRecipes(int profileId) {
        return daoAccess.recipeDao().getRecipes(profileId);
    }

    @Override
    public Flowable<Recipe> getRecipe(int id) {
        return daoAccess.recipeDao().getRecipe(id);
    }

    @Override
    public LiveData<List<Recipe>> getRecipesWithData(int profileId) {
        var flowable = getRecipes(profileId)
                .map(recipes -> {
                    for (var recipe : recipes) {
                        recipe.ingredients = getRecipeIngredientsDirect(recipe.id).blockingGet();
                        recipe.steps = getRecipeStepsDirect(recipe.id).blockingGet();
                    }
                    return recipes;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    @Override
    public LiveData<Recipe> getRecipeWithData(int id) {
        var flowable = getRecipe(id)
                .map(recipe -> {
                    recipe.ingredients = getRecipeIngredientsDirect(recipe.id).blockingGet();
                    recipe.steps = getRecipeStepsDirect(recipe.id).blockingGet();
                    return recipe;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    @Override
    public long upsert(Recipe recipe) {
        recipe.lastModified = new Date();
        executor.execute(() -> daoAccess.recipeDao().upsert(recipe));
        return -1;
    }

    public void upsertWithCallback(Recipe recipe, IInsertCallback callback) {
        executor.execute(() -> {
            recipe.lastModified = new Date();
            long id = daoAccess.recipeDao().upsert(recipe);
            callback.onComplete(id);
        });
    }

    @Override
    public void update(Recipe recipe) {
        executor.execute(() -> {
            recipe.lastModified = new Date();
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
        if (recipe.ingredients != null) {
            for (var ingredient : recipe.ingredients) {
                ingredient.recipe = recipe.id;
            }
        }
        if (recipe.steps != null) {
            for (var step : recipe.steps) {
                step.recipe = recipe.id;
            }
        }
        executor.execute(() -> {
            daoAccess.recipeDao().upsert(recipe);
            if (recipe.steps != null)
                daoAccess.recipeStepDao().upsertAllSteps(recipe.steps);
            if (recipe.ingredients != null) {
                daoAccess.recipeIngredientDao().upsertAllIngredients(recipe.ingredients);
                for (var recipeIngredient : recipe.ingredients) {
                    if (Strings.isNullOrEmpty(recipeIngredient.ingredientName)) continue;

                    Ingredient ingredient = daoAccess.ingredientDao().findIngredient(recipeIngredient.ingredientName);
                    if (ingredient == null) {
                        ingredient = new Ingredient();
                        ingredient.profileId = recipe.profileId;
                        ingredient.name = recipeIngredient.ingredientName;
                        daoAccess.ingredientDao().insert(ingredient);
                    }
                }
            }

            daoAccess.recipeStepCacheDao().clearRecipeCache(recipe.id);
            daoAccess.recipeIngredientCacheDao().clearIngredientCache(recipe.id);
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
    public void upsertTwoSteps(RecipeStep step1, RecipeStep step2) {
        executor.execute(() -> {
            daoAccess.recipeStepDao().upsertTwoSteps(step1, step2);
            daoAccess.recipeStepCacheDao().clearRecipeCache(step1.recipe);
        });
    }

    @Override
    public void upsertAllSteps(List<RecipeStep> steps) {
        executor.execute(() -> {
            daoAccess.recipeStepDao().upsertAllSteps(steps);
            if (steps.size() > 0)
                daoAccess.recipeStepCacheDao().clearRecipeCache(steps.get(0).recipe);
        });
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
