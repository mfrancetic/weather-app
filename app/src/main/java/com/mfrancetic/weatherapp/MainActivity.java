package com.mfrancetic.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_API_KEY = Secret.WEATHER_API_KEY;

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    private static final String CITY_QUERY_PARAM = "q";

    private static final String API_KEY_QUERY_PARAM = "APPID";

    private String weatherData;

    public URL url;

    private TextView weatherResultTextView;

    private EditText enterCityEditText;

    private ProgressBar loadingIndicator;

    private String cityName;

    private String weatherMain;

    private String weatherDescription;

    private String weatherText;

    private static final String WEATHER_TEXT_KEY = "weatherText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherResultTextView = findViewById(R.id.weather_result);
        enterCityEditText = findViewById(R.id.enter_city_edit_text);
        loadingIndicator = findViewById(R.id.loading_indicator);

        loadingIndicator.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            weatherText = savedInstanceState.getString(WEATHER_TEXT_KEY);
            displayWeatherData(weatherText);
        }
    }

    public class WeatherAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                weatherText = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    weatherMain = object.getString("main");
                    weatherDescription = object.getString("description");
                    weatherText = weatherText + "\n" + weatherMain + ": " + weatherDescription;
                }
            } catch (Exception e) {
                e.printStackTrace();
                weatherText = "";
                Toast.makeText(MainActivity.this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
            }
            displayWeatherData(weatherText);
        }
    }

    public static URL createWeatherUrl(String cityQuery) {
        URL url = null;
        Uri baseUri = Uri.parse(WEATHER_URL);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter(CITY_QUERY_PARAM, cityQuery)
                .appendQueryParameter(API_KEY_QUERY_PARAM, WEATHER_API_KEY)
                .build();
        try {
            url = new URL(builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public void checkWeather(View view) {
        hideKeyboard();
        cityName = getCityName();

        if (cityName.isEmpty()) {
            weatherResultTextView.setText("");
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_city_name), Toast.LENGTH_SHORT).show();
        } else {
            String encodedCityName;
            try {
                encodedCityName = URLEncoder.encode(cityName, "UTF-8");
                weatherData = getWeatherData(encodedCityName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getCityName() {
        return enterCityEditText.getText().toString();
    }

    private String getWeatherData(String cityName) {
        loadingIndicator.setVisibility(View.VISIBLE);

        url = createWeatherUrl(cityName);
        try {
            weatherData = new WeatherAsyncTask().execute(url.toString()).get();
        } catch (Exception e) {
            e.printStackTrace();
            weatherData = null;
        }
        return weatherData;
    }

    private void displayWeatherData(String weatherText) {
        loadingIndicator.setVisibility(View.GONE);
        weatherResultTextView.setText(weatherText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WEATHER_TEXT_KEY, weatherText);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(enterCityEditText.getWindowToken(), 0);
        }
    }
}