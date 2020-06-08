package com.singularitycoder.retrofitpostwithgson;

import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private static final String TAG = "LoginActivityTest";

    private CountingIdlingResource countingResource;
    private MainActivity mainActivity;
    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnCreateAccount;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        registerIdlingResources(countingResource);

        countingResource = new CountingIdlingResource("HelloWorldServerCalls");
        mainActivity = activityTestRule.getActivity();
        etName = mainActivity.findViewById(R.id.et_name);
        etEmail = mainActivity.findViewById(R.id.et_email);
        etPhone = mainActivity.findViewById(R.id.et_phone);
        etPassword = mainActivity.findViewById(R.id.et_password);
        btnCreateAccount = mainActivity.findViewById(R.id.btn_create_account);
    }

    // ESPRESSO ////////////////////////////////////////////////////////
    @Test
    public void checkEditTextIdExists() throws Exception {
        onView(withId(R.id.et_name));
        onView(withId(R.id.et_email));
        onView(withId(R.id.et_phone));
        onView(withId(R.id.et_password));
        onView(withId(R.id.btn_create_account));
    }

    @Test
    public void performButtonClick() throws Exception {
        onView(withId(R.id.btn_create_account)).perform(click());
    }

    @Test
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
                .check(matches(isDisplayed()));

//        closeSoftKeyboard();

        onView(withId(R.id.btn_create_account))
                .perform(click());
        //in last line we have PerformException - can not find R.id.btn_click,
    }

    // UNIT ////////////////////////////////////////////////////////

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

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}