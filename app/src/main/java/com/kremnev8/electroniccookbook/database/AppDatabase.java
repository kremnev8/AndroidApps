package com.kremnev8.electroniccookbook.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameColumn;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kremnev8.electroniccookbook.IngredientDataProvider;
import com.kremnev8.electroniccookbook.model.Ingredient;
import com.kremnev8.electroniccookbook.model.Recipe;
import com.kremnev8.electroniccookbook.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.model.RecipeStep;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Ingredient.class,
        Recipe.class,
        RecipeStep.class,
        RecipeIngredient.class},
        version = 2,
        autoMigrations = {
            @AutoMigration(from = 1, to = 2, spec = AppDatabase.UriRenameMigration.class)
        }
)
public abstract class AppDatabase extends RoomDatabase implements DaoAccess {

    private static final String DATABASE_NAME = "app_database.db";

    @RenameColumn(tableName = "ingredients",fromColumnName = "iconUrl", toColumnName = "iconUri")
    static class UriRenameMigration implements AutoMigrationSpec { }

    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeStepDao recipeStepDao();
    public abstract RecipeIngredientDao recipeIngredientDao();
    public static volatile AppDatabase instance = null;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return instance;
    }

    public static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {
                //Background work here
                IngredientDao ingredientDao = instance.ingredientDao();
                IngredientDataProvider dataProvider = new IngredientDataProvider();

                for (Ingredient ingredient: dataProvider.getIngredientData()) {
                    ingredientDao.insert(ingredient);
                }
            });

        }
    };
}