package com.kremnev8.electroniccookbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.button.MaterialButton;
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
public class TestRecipeInteractions {

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
    public void test1_OpenRecipeList() {
        profile = profileProvider.getCurrentProfile()
                .firstOrError().blockingGet();

        onView(withId(R.id.menuButton))
                .perform(click());

        onView(withId(R.id.navigationMenu))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recipesButton))
                .perform(click());

        onView(withId(R.id.recipeList))
                .check(matches(isDisplayed()));
    }

    private ViewInteraction getTabWithName(int nameRes){
        return onView(withChild(allOf(withText(nameRes), withClassName(containsString("TextView")))));
    }

    @Test
    public void test2_recipeAdd() throws InterruptedException {
        test1_OpenRecipeList();

        var recipesCount = databaseExecutor.getRecipes(profile.id).firstOrError().blockingGet().size();

        onView(withId(R.id.addButton))
                .perform(click());

        onView(withText("Create New Recipe"))
                .perform(click());

        // about tab
        onView(withId(R.id.recipeEditLayout))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nameEdit))
                .perform(typeText("Test"), closeSoftKeyboard());

        onView(withId(R.id.descriptionEdit))
                .perform(typeText("TestDescription"), closeSoftKeyboard());

        Thread.sleep(500);

        //ingredient tab
        getTabWithName(R.string.ingredients_label)
                .perform(click());

        onView(withId(R.id.footerMainLayout))
                .perform(click());

        onView(withId(R.id.ingredientNameField))
                .perform(typeText("Garlic"), closeSoftKeyboard());

        Thread.sleep(500);

        // Steps tab
        getTabWithName(R.string.steps_tab_label)
                .perform(click());

        onView(withId(R.id.footerMainLayout))
                .perform(click());

        onView(withId(R.id.stepName))
                .perform(typeText("Monkey see, monkey do"), closeSoftKeyboard());

        onView(withId(R.id.actionButton))
                .perform(click());

        var newRecipes = databaseExecutor.getRecipes(profile.id).firstOrError().blockingGet();
        assertTrue(newRecipes.size() > recipesCount);
        var testRecipes = newRecipes.stream().filter(recipe -> Objects.equals(recipe.name, "Test")).count();
        assertTrue(testRecipes > 0);
    }

    @Test
    public void test3_recipeView(){
        test1_OpenRecipeList();

        onView(withId(R.id.recipeItemLayout))
                .perform(click());

        onView(withId(R.id.nameText))
                .check(matches(withText("Test")));

        onView(withId(R.id.descriptionText))
                .check(matches(withText("TestDescription")));

        getTabWithName(R.string.ingredients_label)
                .perform(click());

        onView(withId(R.id.ingredientName))
                .check(matches(withText("Garlic")));

        getTabWithName(R.string.steps_tab_label)
                .perform(click());

        onView(withId(R.id.stepName))
                .check(matches(withText("Monkey see, monkey do")));
    }

    @Test
    public void test4_recipeDelete(){
        test1_OpenRecipeList();

        onView(withId(R.id.recipeItemLayout))
                .perform(longClick());

        onView(withText(R.string.delete_label))
                .perform(click());

        var newRecipes = databaseExecutor.getRecipes(profile.id).firstOrError().blockingGet();
        var testRecipes = newRecipes.stream().filter(recipe -> Objects.equals(recipe.name, "Test")).count();
        assertEquals(0, testRecipes);
    }

}
