package com.kaleidoscope.currentweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textView;

    private String url = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=2add2f34a24831997571b28b88822110" + "&lang=ru";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.hide();

        editText = findViewById(R.id.searchLine);
        textView = findViewById(R.id.textViewResult);
    }

    public void onSearchClicked(View view) {

        String targetUrl = String.format(url, editText.getText().toString().trim());
        GetWeatherWorker urlWorker = new GetWeatherWorker();
        try {
            urlWorker.execute(targetUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

       urlWorker.setReceiver(textView);
    }

    private static class GetWeatherWorker extends AsyncTask<String, Void, String> {
        private URL url = null;
        private HttpsURLConnection urlConnection = null;
        private StringBuilder result = null;
        private TextView txtView;

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream iStream = urlConnection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(iStream);
                BufferedReader bReader = new BufferedReader(isReader);
                String line = bReader.readLine();
                result = new StringBuilder();
                while (line != null) {
                    result.append(line);
                    line = bReader.readLine();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.i("WHOLE", s);
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject weather = jsonArray.getJSONObject(0);
                // String main = weather.getString("main");

                result = new StringBuilder();
                result.append(weather.getString("description")).append('\n');
                JSONObject mainBlock = jsonObject.getJSONObject("main");
                double celsius = Double.parseDouble(mainBlock.getString("temp"));
                celsius -= 273.15; // Kelvin casting
                result.append(String.format("%.1f", celsius)).append('\n');

                txtView.setText(result.toString());



                //Log.i("OUT", main + " " + description);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        public void setReceiver(TextView view) {
            txtView = view;
        }
    }
}