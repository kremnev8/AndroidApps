package com.kremnev8.electroniccookbook.database;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class DatabaseModule {

   /* @Provides
    public IngredientDao provideIngredientDAO(AppDatabase database) {
        return database.ingredientDao();
    }

    @Provides
    public RecipeDao provideRecipeDao(AppDatabase database) {
        return database.recipeDao();
    }*/

    @Provides
    @Singleton
    public DatabaseExecutor provideDatabaseExecutor(DaoAccess daoAccess){
        return new DatabaseExecutor(daoAccess);
    }

    @Provides
    public DaoAccess provideDaoAccess(AppDatabase database) {
        return database;
    }

    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }
}
