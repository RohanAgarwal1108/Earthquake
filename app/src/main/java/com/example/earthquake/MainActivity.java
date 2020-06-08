package com.example.earthquake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <ArrayList<Earthquake>>{
    private static final String USGS_RESPONSE_URL="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=3&limit=20";
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    public static final String LOG_TAG = MainActivity.class.getName();
    earthquakeadapter adapter;
    private TextView mytextview;
    private ProgressBar progress;
    private static final int Earthquake_loader_id=1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the {@link ListView} in the layout

        final ListView earthquakeListView = findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new earthquakeadapter( this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake current_earthquake= (Earthquake) earthquakeListView.getItemAtPosition(position);
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(current_earthquake.geturl()));
                startActivity(intent);
            }
        });
        mytextview=findViewById(R.id.empty);
        earthquakeListView.setEmptyView(mytextview);
        progress=findViewById(R.id.progress);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected==true){
        getSupportLoaderManager().initLoader(Earthquake_loader_id, null, this);
        }
        else{
            progress.setVisibility(View.GONE);
            mytextview.setText("No internet connection.");
        }

    }

    @NonNull
    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new earthquake_loader_async(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> data) {
        progress.setVisibility(View.GONE);
        Log.i(LOG_TAG,"onLoadFinished called");
        adapter.clear();
        if(data!=null){
            if(!data.isEmpty()){
                adapter.addAll(data);}}
            mytextview.setText("No earthquakes found.");
        }


    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Earthquake>> loader) {
        Log.i(LOG_TAG,"onLoadReset called");
        adapter.clear();
    }

    private static class earthquake_loader_async extends AsyncTaskLoader<ArrayList<Earthquake>> {
        private final String strings;
        earthquake_loader_async(@NonNull Context context, String string) {
            super(context);
            strings=string;
        }
        @Nullable
        @Override
        protected void onStartLoading() {
            forceLoad();
        }
        @Override
        public ArrayList<Earthquake> loadInBackground() {
            if(strings==null||strings.length()<1)
            {
                return null;}
            ArrayList<Earthquake> earthquakes=QueryUtils.fetchEarthquakeData(strings);
            return earthquakes;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
