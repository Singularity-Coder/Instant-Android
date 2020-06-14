package com.singularitycoder.retrofitpostwithgson;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;
import androidx.test.espresso.intent.Checks;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestHelpers {

    public static boolean hasValidPhoneNumber(final String phone) {
        if (phone.length() < 10) return false;
        else return true;
    }

    public static Matcher<Window> withColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<Window, Window>(Window.class) {
            @Override
            public boolean matchesSafely(Window window) {
                if ((window instanceof Window)) {
                    return false;
                }
                return color == window.getStatusBarColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

    // https://stackoverflow.com/questions/28742495/testing-background-color-espresso-android
    public static Matcher<View> withMyColor(final int colorResourceId) {
        return new BoundedMatcher<View, View>(View.class) {
            int actualColor;
            int expectedColor;
            String message;

            @Override
            protected boolean matchesSafely(View item) {
                if (item.getBackground() == null) {
                    message = item.getId() + " does not have a background";
                    return false;
                }
                Resources resources = item.getContext().getResources();
                expectedColor = ResourcesCompat.getColor(resources, colorResourceId, null);

                try {
                    actualColor = ((ColorDrawable) item.getBackground()).getColor();
                } catch (Exception e) {
                    actualColor = ((GradientDrawable) item.getBackground()).getColor().getDefaultColor();
                } finally {
                    if (actualColor == expectedColor) {
//                        Timber.i("Success...: Expected Color " + String.format("#%06X", (0xFFFFFF & expectedColor))
//                                + " Actual Color " + String.format("#%06X", (0xFFFFFF & actualColor)));
                    }
                }
                return actualColor == expectedColor;
            }

            @Override
            public void describeTo(final Description description) {
                if (actualColor != 0) {
                    message = "Background color did not match: Expected "
                            + String.format("#%06X", (0xFFFFFF & expectedColor))
                            + " was " + String.format("#%06X", (0xFFFFFF & actualColor));
                }
                description.appendText(message);
            }
        };
    }

    // https://stackoverflow.com/questions/28742495/testing-background-color-espresso-android
    public static Matcher<View> withTextColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            public boolean matchesSafely(EditText warning) {
                return color == warning.getCurrentTextColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

    public static Matcher<View> withHint(final String expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                String hint = String.valueOf(((TextInputLayout) view).getHint());

                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    // https://android.googlesource.com/platform/frameworks/testing/+/android-support-test/espresso/core/src/main/java/android/support/test/espresso/matcher/ViewMatchers.java#609
    // https://issuetracker.google.com/issues/37068009
    public static Matcher<View> withCustomHint(final Matcher<String> stringMatcher) {
        return new BaseMatcher<View>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matches(Object item) {
                try {
                    Method method = item.getClass().getMethod("getHint");
                    return stringMatcher.matches(method.invoke(item));
                } catch (NoSuchMethodException e) {
                } catch (InvocationTargetException e) {
                } catch (IllegalAccessException e) {
                }
                return false;
            }
        };
    }
}
