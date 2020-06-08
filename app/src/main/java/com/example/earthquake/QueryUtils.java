package com.example.earthquake;

import android.annotation.SuppressLint;
import android.net.UrlQuerySanitizer;
import android.nfc.Tag;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    private QueryUtils() {
    }
    public static ArrayList<Earthquake> fetchEarthquakeData(String url1) {
        URL url=createURL(url1);
        String JsonResponse=null;
        try {
            JsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        return extractEarthquakes(JsonResponse);
    }

    private static URL createURL(String preurl){
        URL postUrl= null;
        try {
            postUrl = new URL(preurl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Error creating a url",e);
        }
        return postUrl;
    }

    private static String makeHttpRequest(URL url2) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url2 == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                    inputStream.close();
            }
        }
        return jsonResponse;}

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Earthquake> extractEarthquakes(String JSON_RESPONSE)  {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            JSONObject root= new JSONObject(JSON_RESPONSE);
            JSONArray features=root.getJSONArray("features");
            for(int i=0;i<features.length();i++)
            {
                JSONObject current_feature=features.getJSONObject(i);
                JSONObject properties=current_feature.getJSONObject("properties");
                double mag=properties.getDouble("mag");
                String place=properties.getString("place");
                long time=properties.getLong("time");
                String url=properties.getString("url");
                earthquakes.add(new Earthquake(mag,place,time,url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", String.valueOf(R.string.msg_log), e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}