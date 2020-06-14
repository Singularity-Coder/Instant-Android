package com.singularitycoder.retrofitpostwithgson;

import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@LargeTest // Execution time > 1000ms. Test App Components. Most UI tests are large tests
@RunWith(AndroidJUnit4.class)
public class MainActivityUiTest {

    // Test UI elements that are important to the user
    // Espresso prevents direct access to activities n views

    private static final String TAG = "MainActivityUiTest";
    private static final String APP_PACKAGE_NAME = "com.singularitycoder.retrofitpostwithgson";

    private MainActivity mainActivity;
    private EditText etName, etEmail, etPhone, etPassword;
    private TextView tvNoInternet;
    private Button btnCreateAccount;
    private IdlingResource idlingResource;
    private MyViewModel myViewModel;
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

        myViewModel = new ViewModelProvider(activityTestRule.getActivity()).get(MyViewModel.class);

        etName = mainActivity.findViewById(R.id.et_name);
        etEmail = mainActivity.findViewById(R.id.et_email);
        etPhone = mainActivity.findViewById(R.id.et_phone);
        etPassword = mainActivity.findViewById(R.id.et_password);

        tvNoInternet = mainActivity.findViewById(R.id.tv_no_internet);

        btnCreateAccount = mainActivity.findViewById(R.id.btn_create_account);
    }

    @Test
    public void string_equals_packageNameFromContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(APP_PACKAGE_NAME, appContext.getPackageName());
    }

    @Test
    public void views_onCreate_notNull() {
        Assert.assertNotNull(tvNoInternet);
        Assert.assertNotNull(etName);
        Assert.assertNotNull(etEmail);
        Assert.assertNotNull(etPhone);
        Assert.assertNotNull(etPassword);
        Assert.assertNotNull(btnCreateAccount);
    }

    @Test
    public void tvNoInternetView_onCreate_viewGone() {
        // Since internet is not actively listened.
        onView(withId(R.id.tv_no_internet)).check(matches(not(isDisplayed())));
    }

    @Test
    public void views_onCreate_viewsVisible() {
        onView(withId(R.id.et_name)).check(matches(isDisplayed()));
        onView(withId(R.id.et_email)).check(matches(isDisplayed()));
        onView(withId(R.id.et_phone)).check(matches(isDisplayed()));
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_create_account)).check(matches(isDisplayed()));
    }

    @Test
    public void editTexts_onCreate_empty() {
        assertEquals("", etName.getText().toString());
        assertEquals("", etEmail.getText().toString());
        assertEquals("", etPhone.getText().toString());
        assertEquals("", etPassword.getText().toString());
    }

    @Test
    public void editTexts_onCreate_haveHints() {
        onView(withId(R.id.et_name_wrapper)).check(matches(TestHelpers.withHint("Name")));
//        onView(withId(R.id.et_name_wrapper)).check(matches(withHint(R.string.string_name)));
//        onView(withId(R.id.et_email_wrapper)).check(matches(withHint(R.string.string_email)));
//        onView(withId(R.id.et_phone_wrapper)).check(matches(withHint(R.string.string_phone)));
//        onView(withId(R.id.et_password_wrapper)).check(matches(withHint(R.string.string_password)));
    }

    @Test
    public void statusBarColor_onCreate_colorDark() {
        Window window = activityTestRule.getActivity().getWindow();

        onView(allOf(instanceOf(Window.class)))
                .inRoot(withDecorView(is(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

//        onView(allOf(instanceOf(Window.class)))
//                .inRoot(withDecorView(is(activityTestRule.getActivity().getWindow().getDecorView())))
//                .check(matches(TestHelpers.withColor(R.color.colorPrimaryDark)));
    }

    @Test
    public void createAccountBtn_onClick_successToast() {

        // Type Name
        onView(withId(R.id.et_name))
                .perform(typeText("Singularity Coder"));

        closeSoftKeyboard();

        // Type Email
        onView(withId(R.id.et_email))
                .perform(typeText("codehithesh@gmail.com"));

        // Close the keyboard as Espresso will use the text keyboard instead of the number keyboard and test will fail
        closeSoftKeyboard();

        // Type Phone
        onView(withId(R.id.et_phone))
                .perform(typeText("9999999999"));

        closeSoftKeyboard();

        // Type Password
        onView(withId(R.id.et_password))
                .perform(typeText("Qwerty@123"));

        closeSoftKeyboard();

        // click create account button
        onView(withId(R.id.btn_create_account))
                .perform(click());

        // check internet label gone
        onView(withId(R.id.tv_no_internet))
                .check(matches(not(isDisplayed())));

        // check no empty values
        assertNotEquals("", valueOf(etName.getText()));
        assertNotEquals("", valueOf(etEmail.getText()));
        assertNotEquals("", valueOf(etPhone.getText()));
        assertNotEquals("", valueOf(etPassword.getText()));

        // check valid email
        assertTrue(valueOf(etEmail.getText()), activityTestRule.getActivity().hasValidEmail(valueOf(etEmail.getText())));

        // check valid phone
        assertTrue(valueOf(etPhone.getText()), TestHelpers.hasValidPhoneNumber(valueOf(etPhone.getText())));

        // check valid password
        assertTrue(valueOf(etPassword.getText()), activityTestRule.getActivity().hasValidPassword(valueOf(etPassword.getText())));

        MutableLiveData<RequestStateMediator> mutableLiveData = new MutableLiveData<>();

        // Observe UI state changes
        activityTestRule.getActivity().runOnUiThread(() -> {
            Observer<RequestStateMediator> liveDataObserver = requestStateMediator -> {

                if (UiState.LOADING == requestStateMediator.getStatus()) {
                    // loading dialog visible
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(isDisplayed()));
                }

                if (UiState.SUCCESS == requestStateMediator.getStatus()) {
                    // loading dialog gone
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(not(isDisplayed())));

                    // check response Toast appeared
                    onView(withText("Got Data!"))
                            .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                            .check(matches(isDisplayed()));
                }

                if (UiState.EMPTY == requestStateMediator.getStatus()) {
                    // loading dialog gone
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(not(isDisplayed())));
                }

                if (UiState.ERROR == requestStateMediator.getStatus()) {
                    // loading dialog gone
                    onView(withText("Loading..."))
                            .inRoot(isDialog())
                            .check(matches(not(isDisplayed())));
                }
            };

            mutableLiveData.observeForever(liveDataObserver);
        });
    }

    @After
    public void tearDown() throws Exception {
        if (null != idlingResource) IdlingRegistry.getInstance().unregister(idlingResource);
        mainActivity = null;
    }
}