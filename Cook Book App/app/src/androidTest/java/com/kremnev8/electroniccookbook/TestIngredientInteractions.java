package com.kremnev8.electroniccookbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestIngredientInteractions {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> rule = new  ActivityScenarioRule<>(MainActivity.class);


    @Inject
    DatabaseExecutor databaseExecutor;
    @Inject
    IProfileProvider profileProvider;

    private Profile profile;

    @Before
    public void init() {
        hiltRule.inject();
    }

    @After
    public void after(){
        ApplicationModule.dispose();
    }

    @Test
    public void test1_OpenIngredientList() {
        profile = profileProvider.getCurrentProfile()
                .firstOrError().blockingGet();

        onView(withId(R.id.menuButton))
                .perform(click());

        onView(withId(R.id.navigationMenu))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ingredientsButton))
                .perform(click());

        onView(withId(R.id.ingredientsList))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test2_ingredientAdd(){
        test1_OpenIngredientList();

        var ingredientsCount = databaseExecutor.getIngredientsOnce(profile.id).blockingGet().size();

        onView(withId(R.id.addButton))
                .perform(click());

        onView(withId(R.id.ingredientsEditLayout))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nameInput))
                .perform(typeText("Test"), closeSoftKeyboard());

        onView(withId(R.id.actionButton))
                .perform(click());

        var newIngredients = databaseExecutor.getIngredientsOnce(profile.id).blockingGet();
        assertTrue(newIngredients.size() > ingredientsCount);
        var testIngredients = newIngredients.stream().filter(ingredient -> Objects.equals(ingredient.name, "Test")).count();
        assertTrue(testIngredients > 0);
    }

    @Test
    public void test3_ingredientDelete(){
        test1_OpenIngredientList();

        onView(withId(R.id.ingredientItemLayout))
                .perform(longClick());

        onView(withText(R.string.delete_label))
                .perform(click());

        var newIngredients = databaseExecutor.getIngredientsOnce(profile.id).blockingGet();
        var testIngredients = newIngredients.stream().filter(ingredient -> Objects.equals(ingredient.name, "Test")).count();
        assertEquals(0, testIngredients);
    }
}
