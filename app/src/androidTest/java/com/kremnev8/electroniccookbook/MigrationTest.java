package com.kremnev8.electroniccookbook;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.kremnev8.electroniccookbook.database.AppDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class);
    }

    @Test
    public void migrate6to7() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);

        // Database has schema version 1. Insert some data using SQL queries.
        // You can't use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO ingredients VALUES (1, 'Pumkin', '', 5, 'Unit');");
        db.execSQL("INSERT INTO ingredients VALUES (2, 'Carrot', '', 7, 'Unit');");
        db.execSQL("INSERT INTO recipe VALUES (1, 'Pumkin Soup', 'Best soup', '1 unit of soup', 1, '');");

        db.execSQL("INSERT INTO recipeIngredient VALUES (1, 1, 'Pumkin', 1, 2, 'Unit');");
        db.execSQL("INSERT INTO recipeIngredient VALUES (2, 1, 'Carrot', 2, 1, 'Unit');");
        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 7, true, AppDatabase.MIGRATION_6_7);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }
}
