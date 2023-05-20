package com.kremnev8.electroniccookbook.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.DeleteColumn;
import androidx.room.DeleteTable;
import androidx.room.RenameColumn;
import androidx.room.RenameTable;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kremnev8.electroniccookbook.IngredientDataProvider;
import com.kremnev8.electroniccookbook.common.Converters;
import com.kremnev8.electroniccookbook.components.ingredient.model.IngredientDao;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.profile.model.ProfileDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCacheDao;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepDao;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCache;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Ingredient.class,
        Recipe.class,
        RecipeStep.class,
        RecipeIngredient.class,
        Profile.class,
        RecipeStepCache.class,
        RecipeIngredientCache.class},
        version = 12,
        autoMigrations = {
                @AutoMigration(from = 1, to = 2, spec = AppDatabase.UriRenameMigration.class),
                @AutoMigration(from = 2, to = 3),
                @AutoMigration(from = 3, to = 4),
                @AutoMigration(from = 4, to = 5, spec = AppDatabase.RefactorMigration.class),
                @AutoMigration(from = 5, to = 6, spec = AppDatabase.IngredientNameMigration.class),
                @AutoMigration(from = 7, to = 8, spec = AppDatabase.AddIngredientCacheMigration.class),
                @AutoMigration(from = 8, to = 9),
                @AutoMigration(from = 9, to = 10, spec = AppDatabase.ProfileSegregationMigration.class),
                @AutoMigration(from = 10, to = 11, spec = AppDatabase.DeleteIngredientLinkMigration.class),
                @AutoMigration(from = 11, to = 12)
        }
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase implements DaoAccess {

    private static final String DATABASE_NAME = "app_database.db";

    @DeleteColumn(tableName = "recipeIngredient", columnName = "ingredient")
    public static class DeleteIngredientLinkMigration implements AutoMigrationSpec{

    }

    @DeleteTable(tableName = "timerCache")
    public static class ProfileSegregationMigration implements AutoMigrationSpec {
    }

    @RenameTable(fromTableName = "viewCache", toTableName = "recipeStepCache")
    public static class AddIngredientCacheMigration implements AutoMigrationSpec {
    }

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("BEGIN TRANSACTION;");

            database.execSQL("CREATE TABLE IF NOT EXISTS `recipeIngredientNew` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`recipe` INTEGER NOT NULL, " +
                    "`ingredientName` TEXT, " +
                    "`ingredient` INTEGER, " +
                    "`amount` REAL NOT NULL, " +
                    "`units` TEXT, " +
                    "FOREIGN KEY(`recipe`) REFERENCES `recipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                    "FOREIGN KEY(`ingredient`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");

            database.execSQL("INSERT INTO recipeIngredientNew(id,recipe,ingredientName,ingredient,amount,units) SELECT id,recipe,ingredientName,ingredient,amount,units FROM recipeIngredient;");

            database.execSQL("DROP TABLE recipeIngredient;");

            database.execSQL("ALTER TABLE 'recipeIngredientNew' RENAME TO 'recipeIngredient';");

            database.execSQL("DROP INDEX IF EXISTS index_recipeIngredient_ingredientName;");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_ingredients_name` ON `ingredients` (`name`)");

            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_recipeIngredient_id` ON `recipeIngredient` (`id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_recipeIngredient_recipe` ON `recipeIngredient` (`recipe`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_recipeIngredient_ingredient` ON `recipeIngredient` (`ingredient`)");

            database.execSQL("COMMIT;");
        }
    };

    @DeleteColumn(tableName = "recipeStep", columnName = "timerEnabled")
    public static class IngredientNameMigration implements AutoMigrationSpec {
    }

    @RenameColumn(tableName = "recipeStep", fromColumnName = "description", toColumnName = "text")
    @RenameColumn(tableName = "recipeIngredient", fromColumnName = "neededAmount", toColumnName = "amount")
    @DeleteColumn(tableName = "recipeStep", columnName = "isOptional")
    @DeleteColumn(tableName = "recipeStep", columnName = "name")
    @DeleteColumn(tableName = "viewCache", columnName = "timerIsRunning")
    public static class RefactorMigration implements AutoMigrationSpec {
    }

    @RenameColumn(tableName = "ingredients", fromColumnName = "iconUrl", toColumnName = "iconUri")
    public static class UriRenameMigration implements AutoMigrationSpec {
    }

    public abstract IngredientDao ingredientDao();

    public abstract RecipeDao recipeDao();

    public abstract RecipeStepDao recipeStepDao();

    public abstract RecipeIngredientDao recipeIngredientDao();

    public abstract RecipeStepCacheDao recipeStepCacheDao();

    public abstract RecipeIngredientCacheDao recipeIngredientCacheDao();

    public abstract ProfileDao profileDao();

    public static volatile AppDatabase instance = null;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return instance;
    }

    public static Callback callback = new Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                ProfileDao profileDao = instance.profileDao();
                if (!profileDao.hasProfile(1)){
                    Profile profile = new Profile();
                    profile.name = "Default Profile";
                    profile.passwordRequired = false;
                    profile.profileImageUrl = "";
                    profileDao.upsert(profile);
                }
            });
        }
    };
}