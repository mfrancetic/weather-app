package com.mfrancetic.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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

    private static final String LONDON_QUERY = "London,uk";

    private String apiResult;

    public URL url;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiResult = "";
        url = createWeatherUrl(LONDON_QUERY);

        try {
            apiResult = new WeatherAsyncTask().execute(url.toString()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class WeatherAsyncTask extends AsyncTask<String, Void, String> {

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
}