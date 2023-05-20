package com.kremnev8.electroniccookbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestProfileInteraction {

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
    public void test1_OpenProfileList() {
        profile = profileProvider.getCurrentProfile()
                .firstOrError().blockingGet();

        onView(withId(R.id.menuButton))
                .perform(click());

        onView(withId(R.id.navigationMenu))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ChangeProfileText))
                .perform(click());

        onView(withId(R.id.profileList))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test2_profileAdd() {
        test1_OpenProfileList();

        var profilesCount = databaseExecutor.getProfilesBlocking().blockingGet().size();

        onView(withId(R.id.footerMainLayout))
                .perform(click());

        onView(withId(R.id.nameInput))
                .perform(typeText("Bender"), closeSoftKeyboard());

        onView(withId(R.id.passwordInput))
                .perform(typeText("Garlic"), closeSoftKeyboard());

        onView(withId(R.id.actionButton))
                .perform(click());

        var newProfilesCount = databaseExecutor.getProfilesBlocking().blockingGet().size();
        assertTrue(newProfilesCount > profilesCount);
    }

    @Test
    public void test3_useProfile() throws InterruptedException {
        test1_OpenProfileList();

        onView(withChild(withText("Bender")))
                .perform(click());

        onView(withId(R.id.loginLayout))
                .check(matches(isDisplayed()));

        onView(withId(R.id.passwordField))
                .perform(typeText("Bender"), closeSoftKeyboard());

        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withId(R.id.errorText))
                .check(matches(withText(R.string.login_failed)));

        onView(withId(R.id.passwordField))
                .perform(clearText(), typeText("Garlic"), closeSoftKeyboard());

        onView(withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.recipeList))
                .check(matches(isDisplayed()));
    }
}
