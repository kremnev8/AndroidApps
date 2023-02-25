package com.kremnev8.electroniccookbook.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kremnev8.electroniccookbook.IngredientDataProvider;
import com.kremnev8.electroniccookbook.model.Ingredient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Ingredient.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "app_database.db";

    public abstract IngredientDao ingredientDao();
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
