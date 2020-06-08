package com.singularitycoder.rooming;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OwnerListActivityTest {

    private static final int ITEM_POSITION = 3;

    private MainActivity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mainActivity = activityTestRule.getActivity();
    }

    // RECYCLER ////////////////////////////////////////////////////////

    @Test
    public void testOnViewHolderCliked() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_movies)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.click()));
    }

    @Test
    public void scrollToPosition() {
        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.recycler_movies);
        int itemCount = recyclerView.getAdapter().getItemCount();
        Espresso.onView(ViewMatchers.withId(R.id.recycler_movies)).perform(RecyclerViewActions.scrollToPosition(itemCount - 1));
    }

    @Test
    public void checkStringOnItemMatches() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_movies)).perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_POSITION, ViewActions.click()));
        String title = "Title 4";
        Espresso.onView(ViewMatchers.withText(title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}