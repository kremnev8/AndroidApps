package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.model.Ingredient;
import com.kremnev8.electroniccookbook.model.Recipe;
import com.kremnev8.electroniccookbook.model.RecipeWithData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.EntryPoint;
import dagger.hilt.EntryPoints;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

public class DatabaseExecutor implements IngredientDao, RecipeDao {

    private final ExecutorService executor;
    private final DaoInterface daoInterface;

    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface DaoInterface {
        IngredientDao getIngredientDao();
        RecipeDao getRecipeDao();
    }

    public DatabaseExecutor(){
        daoInterface = EntryPoints.get(MainActivity.Instance, DaoInterface.class);
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public LiveData<List<Ingredient>> getIngredients() {
        return daoInterface.getIngredientDao().getIngredients();
    }

    @Override
    public void insert(Ingredient ingredient) {
        executor.execute(() -> {
            daoInterface.getIngredientDao().insert(ingredient);
        });
    }

    @Override
    public void update(Ingredient ingredient) {
        executor.execute(() -> {
            daoInterface.getIngredientDao().update(ingredient);
        });
    }

    @Override
    public void delete(Ingredient ingredient) {
        executor.execute(() -> {
            daoInterface.getIngredientDao().delete(ingredient);
        });
    }

    @Override
    public LiveData<List<RecipeWithData>> getRecipes() {
        return daoInterface.getRecipeDao().getRecipes();
    }

    @Override
    public void insert(Recipe recipe) {
        executor.execute(() -> {
            daoInterface.getRecipeDao().insert(recipe);
        });
    }

    @Override
    public void update(Recipe recipe) {
        executor.execute(() -> {
            daoInterface.getRecipeDao().update(recipe);
        });
    }

    @Override
    public void deleteById(int id) {
        executor.execute(() -> {
            daoInterface.getRecipeDao().deleteById(id);
        });
    }

    @Override
    public void delete(Recipe recipe) {
        executor.execute(() -> {
            daoInterface.getRecipeDao().delete(recipe);
        });
    }
}
