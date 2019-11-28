package com.mfrancetic.weatherapp;

import android.widget.EditText;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private MainActivity mainActivity;

    private EditText editText;

    private TextView weatherResultTextView;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule
            <>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mainActivity = rule.getActivity();
        editText = mainActivity.findViewById(R.id.enter_city_edit_text);
        weatherResultTextView = mainActivity.findViewById(R.id.weather_result);
    }

    @Test
    public void clickingCheckWeatherButton_readsInputAndDisplaysWeatherData() {
        final String expectedCityName = "London";
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(expectedCityName);
            }
        });
        Espresso.onView(withId(R.id.check_weather_button)).perform(click());
        String actualCityName = mainActivity.getCityName();
        assertEquals("London", actualCityName);

        final String actualResult = weatherResultTextView.getText().toString();
        assertNotEquals("", actualResult);
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}