package com.singularitycoder.retrofitpostwithgson;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import junit.framework.AssertionFailedError;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@LargeTest // Execution time > 1000ms. Test App Components. Most UI tests are large tests
@RunWith(AndroidJUnit4.class)
public class DummyTest {

    // Test UI elements that are important to the user
    // Espresso prevents direct access to activities n views

    private static final String TAG = "MainActivityUiTest";

    private MainActivity mainActivity;
    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnCreateAccount;
    private IdlingResource idlingResource;
    private CountingIdlingResource countingIdlingResource;
    private UiDevice uiDevice;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mainActivity = activityTestRule.getActivity();

        idlingResource = activityTestRule.getActivity().getWaitingState();
        IdlingRegistry.getInstance().register(idlingResource);

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        etName = mainActivity.findViewById(R.id.et_name);
        etEmail = mainActivity.findViewById(R.id.et_email);
        etPhone = mainActivity.findViewById(R.id.et_phone);
        etPassword = mainActivity.findViewById(R.id.et_password);

        btnCreateAccount = mainActivity.findViewById(R.id.btn_create_account);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.singularitycoder.retrofitpostwithgson", appContext.getPackageName());
    }

    @Test   // useless
    public void loginTest() throws Exception {
        onView(withId(R.id.et_name))
                .perform(typeText("Hithesh"))
                .check(matches(withText("Hithesh")));
        onView(withId(R.id.et_email))
                .perform(typeText("hithesh@gmail.com"))
                .check(matches(withText("hithesh@gmail.com")));
        onView(withId(R.id.et_phone))
                .perform(typeText("hitWonder"))
                .check(matches(withText("hitWonder")));
        onView(withId(R.id.et_password))
                .perform(typeText("hitWonder"))
                .check(matches(withText("hitWonder")));
        onView(withId(R.id.btn_create_account))
                .perform(click());
        onView(withId(R.id.btn_create_account))
                .check(matches(withText("Success")));
    }

    @Test
    public void randLoginTest() throws Exception {
        // checks whether the view is displayed or not
        onView(withId(R.id.btn_create_account)).check(matches(isDisplayed()));
        Log.d(TAG, "We are in MainActivity, user is not logged in");

        //press button  - walk to next activity
        onView(withId(R.id.btn_create_account)).perform(click());

        //register MyUserHelperV2 - this is Server decorator
//        final MainActivity act = (MainActivity) getCurrentActivity();
//        MainActivity.Server aHelper = act.getUserHelper();
//        MyUserHelperV2 helper = new MyUserHelperV2(aHelper, countingResource);
//        act.setUserHelper(helper);

        //set password and email
        onView(withId(R.id.et_email))
                .perform(typeText("test@mail.ru"));

        onView(withId(R.id.et_password))
                .perform(typeText("password111"));

        //Check if button R.id.btn_click exists:
        onView(withId(R.id.btn_create_account))
                .perform(click())
                .check(matches(isDisplayed()));

//        closeSoftKeyboard();

        onView(withId(R.id.btn_create_account))
                .perform(click());
        //in last line we have PerformException - can not find R.id.btn_click,
    }

    @Test
    public void checkTextViewValue() {
        assertEquals("Hello World!", etEmail.getText().toString());
    }

    @Test
    public void checkIfTextViewIsNull() {
        Assert.assertNotNull(etEmail);
    }

    @Test
    public void checkIfEditTextValue() {
        assertEquals("", etPassword.getText().toString());
    }

    @Test
    public void checkIfValidEmail() {
        assertTrue("hitwonder@gmail.com", isValidEmail(etPassword.getText().toString()));
    }

    @Test
    public void checkIfValidMail() {
        assertTrue("hitwonder@gmail.com", isValidMail(etPassword.getText().toString()));
    }

    public boolean isValidEmail(String emailInput) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailInput);
        return matcher.matches();
    }

    public boolean isValidMail(String mail) {
        return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    @Test
    public void idlingResourceTest() {
        // Test tea objects appear in the gridview
//        onData(anything()).inAdapterView(withId(R.id.tea_grid_view)).atPosition(0).perform(click());
    }

    @Test
    public void clickDecrementButton_ChangesQuantityAndCost() {

        // Check the initial quantity variable is zero
        onView(withId(R.id.tv_no_internet))
                .check(matches(withText("0")));

        // Click on decrement button
        onView(withId(R.id.tv_no_internet))
                .perform(click());

        // Verify that the decrement button decreases the quantity by 1
        onView(withId(R.id.tv_no_internet))
                .check(matches(withText("0")));

        // Verify that the increment button also increases the total cost to $5.00
        onView(withId(R.id.tv_no_internet))
                .check(matches(withText("$0.00")));
    }

    @Test
    public void statusBarColor_onCreate_darker() {
        try {
            onView(withText("Button")).perform(click());
            // View is in hierarchy

        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }


        // check if view is displayed on screen or not
        try {
            onView(withText("Button")).check(matches(isDisplayed()));
            // View is displayed
        } catch (AssertionFailedError e) {
            // View not displayed
        }

        // check if not visible on screen
        onView(withId(R.id.tv_no_internet)).check(matches(not(isDisplayed())));

        // if scrollview n not displayed yet
        onView(withId(R.id.tv_no_internet)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.tv_no_internet)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.tv_no_internet)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));

        // check if any of the views is visible
        onView(anyOf(withId(R.id.tv_no_internet), withId(R.id.tv_no_internet)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));

        // confirm that this view doesnt exist. It should not exist. not gone or invisible
        onView(withId(R.id.tv_no_internet)).check(doesNotExist());

        final AtomicBoolean view1Displayed = new AtomicBoolean(true);
        onView(withId(R.id.tv_no_internet))
                .inRoot(withDecorView(is(activityTestRule.getActivity().getWindow().getDecorView())))
                .withFailureHandler(new FailureHandler() {
                    @Override
                    public void handle(Throwable error, org.hamcrest.Matcher<View> viewMatcher) {
                        view1Displayed.set(false);
                    }
                }).check(ViewAssertions.matches(isDisplayed()));

        if (view1Displayed.get()) {
            try {
                onView(withId(R.id.tv_no_internet))
                        .inRoot(withDecorView(is(activityTestRule.getActivity().getWindow().getDecorView())))
                        .check(ViewAssertions.matches(not(isDisplayed())));
            } catch (NoMatchingViewException ignore) {
            }
        } else {
            onView(withId(R.id.tv_no_internet))
                    .inRoot(withDecorView(is(activityTestRule.getActivity().getWindow().getDecorView())))
                    .check(ViewAssertions.matches(isDisplayed()));
        }

        // dialogs
        onView(withText("dialogText")).check(matches(isDisplayed()));
        onView(withId(R.id.tv_no_internet)).check(matches(allOf(withText("dialog text"), isDisplayed())));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Dialog"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));

    }

    private void uiAutomator() throws UiObjectNotFoundException {
        // Search for correct button in the dialog.
        UiObject button = uiDevice.findObject(new UiSelector().text("ButtonText"));
        if (button.exists() && button.isEnabled()) {
            button.click();
        }
    }

    @Test
    public void statusBarColor_onCreate_colorDark() {
//        int type = root.getWindowLayoutParams().get().type;
//        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
//            IBinder windowToken = root.getDecorView().getWindowToken();
//            IBinder appToken = root.getDecorView().getApplicationWindowToken();
//            if (windowToken == appToken) {
//                // windowToken == appToken means this window isn't contained by any other windows.
//                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
//                return true;
//            }
//        }
    }

    private void dialogs() {
        // android.R.id.button1, android.R.id.button2 and android.R.id.button3 for "positive", "neutral" and "negative", are global symbols
        // For anything outside your UI, UIAutomator is recommended
        int titleId = activityTestRule.getActivity().getResources()
                .getIdentifier("android.R.id.alertTitle", "id", "android");

        onView(withId(titleId))
                .inRoot(isDialog())
                .check(matches(withText("R.string.my_title")))
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.text1))
                .inRoot(isDialog())
                .check(matches(withText("R.string.my_message")))
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button2))
                .inRoot(isDialog())
                .check(matches(withText(android.R.string.no)))
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button3))
                .inRoot(isDialog())
                .check(matches(withText(android.R.string.yes)))
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button3)).perform(click());
    }

    /* Copyright 2019 Google LLC.
SPDX-License-Identifier: Apache-2.0 */
    public static class LiveDataTestUtil {
        public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
            final Object[] data = new Object[1];
            final CountDownLatch latch = new CountDownLatch(1);
            Observer<T> observer = new Observer<T>() {
                @Override
                public void onChanged(@Nullable T o) {
                    data[0] = o;
                    latch.countDown();
                    liveData.removeObserver(this);
                }
            };
            liveData.observeForever(observer);
            // Don't wait indefinitely if the LiveData is not set.
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("LiveData value was never set.");
            }
            //noinspection unchecked
            return (T) data[0];
        }
    }

//    public static Matcher<View> withHint(final String expectedHint) {
//        return new TypeSafeMatcher<View>() {
//
//            @Override
//            public boolean matchesSafely(View view) {
//                if (!(view instanceof EditText)) {
//                    return false;
//                }
//
//                String hint = ((EditText) view).getHint().toString();
//
//                return expectedHint.equals(hint);
//            }
//
//            @Override
//            public void describeTo(Description description) {
//            }
//        };
//    }


//    public static Matcher<View> withCustomHint(final Matcher<String> stringMatcher) {
//        return new BaseMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//            }
//
//            @Override
//            public boolean matches(Object item) {
//                try {
//                    Method method = item.getClass().getMethod("getHint");
//                    return stringMatcher.matches(method.invoke(item));
//                } catch (NoSuchMethodException e) {
//                } catch (InvocationTargetException e) {
//                } catch (IllegalAccessException e) {
//                }
//                return false;
//            }
//        };
//    }


    @After
    public void tearDown() throws Exception {
        if (null != idlingResource) IdlingRegistry.getInstance().unregister(idlingResource);
        mainActivity = null;
    }
}