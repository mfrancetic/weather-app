package com.mfrancetic.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_API_KEY = Secret.WEATHER_API_KEY;

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    private static final String CITY_QUERY_PARAM = "q";

    private static final String API_KEY_QUERY_PARAM = "APPID";

    private String weatherData;

    public URL url;

    private TextView weatherResultTextView;

    private EditText enterCityEditText;

    private String cityName;

    private String weatherMain;

    private String weatherDescription;

    private String weatherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherResultTextView = findViewById(R.id.weather_result);
        enterCityEditText = findViewById(R.id.enter_city_edit_text);
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
                weatherText = getString(R.string.no_data_found);
            }
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
        cityName = getCityName();
        weatherData = getWeatherData(cityName);
        displayWeatherData(weatherText);
    }

    private String getCityName() {
        return enterCityEditText.getText().toString();
    }

    private String getWeatherData(String cityName) {
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
        weatherResultTextView.setText(weatherText);
    }
}