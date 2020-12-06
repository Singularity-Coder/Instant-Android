package com.singularitycoder.roomnews.view;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.google.android.material.textfield.TextInputLayout;
import com.singularitycoder.roomnews.R;
import com.singularitycoder.roomnews.helper.espresso.EspressoTestingIdlingResource;
import com.singularitycoder.roomnews.helper.retrofit.StateMediator;
import com.singularitycoder.roomnews.helper.retrofit.UiState;
import com.singularitycoder.roomnews.viewmodel.NewsViewModel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.singularitycoder.roomnews.TestHelpers.atPosition;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

// http://d.android.com/tools/testing
// @MediumTest annotation for tests whose Execution time ~1000ms. Test App Components. Most UI tests are medium tests
// Test UI elements that are important to the user
// Espresso prevents direct access to activities n views
// The test naming pattern is onView_actionPerformed_resultOfAction
// For unit tests the naming pattern is input_onMethod_output

@MediumTest
@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    private static final String TAG = "HomeFragmentTest";
    private static final String APP_PACKAGE_NAME = "com.singularitycoder.roomnews";

    private MainActivity mainActivity;
    private HomeFragment homeFragment;
    private TextInputLayout tilSearch;
    private EditText etSearch;
    private TextView tvNoInternet, tvNothing;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout clShimmerRoot;
    private RecyclerView recyclerView;
    private IdlingResource idlingResource;
    private NewsViewModel newsViewModel;
    private CountingIdlingResource countingIdlingResource;
    private UiDevice uiDevice;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mainActivity = activityTestRule.getActivity();
        initialiseHomeFragment();
        initialiseIdlingResource();
        registerIdlingResource();
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        newsViewModel = new ViewModelProvider(activityTestRule.getActivity()).get(NewsViewModel.class);
        initialiseViews();
    }

    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoTestingIdlingResource.getIdlingResource());   // let espresso know how to synchronize with background tasks
    }

    private void initialiseHomeFragment() {
        final FragmentManager fragmentManager = activityTestRule.getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.con_lay_news_home_root);
    }

    private void initialiseIdlingResource() {
        idlingResource = homeFragment.getWaitingState();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    private void initialiseViews() {
        tvNoInternet = mainActivity.findViewById(R.id.tv_no_internet);
        tvNothing = mainActivity.findViewById(R.id.tv_nothing);
        swipeRefreshLayout = mainActivity.findViewById(R.id.swipe_refresh_layout);
        recyclerView = mainActivity.findViewById(R.id.recycler_news);
        clShimmerRoot = mainActivity.findViewById(R.id.shimmer_root);
    }

    @Test
    public void string_equals_packageNameFromContext() {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(APP_PACKAGE_NAME, appContext.getPackageName());
    }

    @Test
    public void views_loadedInOnCreate_notNull() {
        Assert.assertNotNull(tvNoInternet);
        Assert.assertNotNull(tvNothing);
        Assert.assertNotNull(etSearch);
        Assert.assertNotNull(swipeRefreshLayout);
        Assert.assertNotNull(recyclerView);
        Assert.assertNotNull(clShimmerRoot);
    }

    @Test
    public void views_loadedInOnCreate_viewNotDisplayed() {
        // Since internet is not actively listened.
        onView(withId(R.id.tv_no_internet)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tv_nothing)).check(matches(not(isDisplayed())));
        onView(withId(R.id.shimmer_root)).check(matches(not(isDisplayed())));
    }

    @Test
    public void views_loadedInOnCreate_viewGone() {
        onView(withId(R.id.tv_no_internet)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.tv_nothing)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.shimmer_root)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void views_loadedInOnCreate_viewsDisplayed() {
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_news)).check(matches(isDisplayed()));
    }

    @Test
    public void views_loadedInOnCreate_viewsVisible() {
        onView(withId(R.id.swipe_refresh_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.recycler_news)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void searchEditTextView_loadedInOnCreate_empty() {
        assertEquals("", valueOf(etSearch.getText()));
    }

    @Test
    public void inOnCreate_loadRestaurantListApi_successToast() {
        onView(withId(R.id.tv_no_internet))
                .check(matches(not(isDisplayed())));    // check internet label gone

        assertTrue("", ("").equals(valueOf(etSearch.getText())));       // check search field is empty - we can use assertEquals as well but just another way

        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        // Observe UI state changes
        activityTestRule.getActivity().runOnUiThread(() -> {
            final Observer<StateMediator<Object, UiState, String, String>> liveDataObserver = requestStateMediator -> {

                if (UiState.LOADING == requestStateMediator.getStatus()) {
                    onView(withId(R.id.shimmer_root))
                            .check(matches(isDisplayed()));     // loading dialog visible
                }

                if (UiState.SUCCESS == requestStateMediator.getStatus()) {
                    onView(withId(R.id.shimmer_root))
                            .check(matches(not(isDisplayed())));    // loading gone

                    onView(withText("Got Data!"))
                            .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                            .check(matches(isDisplayed()));     // check response Toast appeared

                    onView(withId(R.id.recycler_news))
                            .check(matches(isDisplayed()));     // verify the visibility of recycler view on screen

                    onView(withId(R.id.recycler_news))
                            .perform(scrollToPosition(0))
                            .check(matches(atPosition(0, hasDescendant(withText("Mayflower")))));   // verify recyclerview item at position 0 has for the title has Mayflower
                }

                if (UiState.EMPTY == requestStateMediator.getStatus()) {
                    onView(withId(R.id.shimmer_root))
                            .check(matches(not(isDisplayed())));    // loading gone
                }

                if (UiState.ERROR == requestStateMediator.getStatus()) {
                    onView(withId(R.id.shimmer_root))
                            .check(matches(not(isDisplayed())));    // loading gone
                }
            };

            mutableLiveData.observeForever(liveDataObserver);
        });
    }

    @After
    public void tearDown() throws Exception {
        unregisterIdlingResource();
        if (null != idlingResource) IdlingRegistry.getInstance().unregister(idlingResource);
        mainActivity = null;
    }

    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoTestingIdlingResource.getIdlingResource());
    }
}