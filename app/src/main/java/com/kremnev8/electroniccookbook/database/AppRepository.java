package com.kremnev8.electroniccookbook.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Query;

import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.List;

public class AppRepository {

    public IngredientDao ingredientDao;
    public LiveData<List<Ingredient>> ingredients;
    private AppDatabase database;

    public AppRepository(Application application){
        database=AppDatabase.getInstance(application);
        ingredientDao=database.ingredientDao();
        ingredients=ingredientDao.getIngredients();
    }

    public void insert(Ingredient ingredient){
        ingredientDao.insert(ingredient);
    }

    public void update(Ingredient ingredient){
        ingredientDao.update(ingredient);
    }

    public void delete(Ingredient ingredient){
        ingredientDao.delete(ingredient);
    }

    public void deleteById(int id){
        ingredientDao.deleteById(id);
    }

    public LiveData<List<Ingredient>> getIngredients(){
        return ingredients;
    }
}
