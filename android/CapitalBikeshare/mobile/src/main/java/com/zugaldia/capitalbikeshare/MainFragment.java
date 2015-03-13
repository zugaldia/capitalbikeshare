package com.zugaldia.capitalbikeshare;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zugaldia.capitalbikeshare.api.ApiService;
import com.zugaldia.capitalbikeshare.api.ClosestResponse;
import com.zugaldia.capitalbikeshare.api.StatusResponse;
import com.zugaldia.capitalbikeshare.location.LocationCallback;
import com.zugaldia.capitalbikeshare.location.LocationService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Main app fragment
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    // UI elements
    private TextView textBikes;
    private TextView textDocks;
    private TextView textSummary;

    // App services
    private LocationService locationService;
    private ApiService apiService;

    // Tracks the locations of our target stations
    private ClosestResponse targetBike;
    private ClosestResponse targetDock;

    public MainFragment() {
        Log.d(LOG_TAG, "MainFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Our location service
        locationService = new LocationService(
                this.getActivity(),
                new FragmentLocationCallback());

        // Our API
        apiService = new ApiService();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // First text view (available clicks)
        textBikes = (TextView) rootView.findViewById(R.id.text_bikes);
        textBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(targetBike);
            }
        });

        // Second text view (available docks)
        textDocks = (TextView) rootView.findViewById(R.id.text_docks);
        textDocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(targetDock);
            }
        });

        // Third and last text view (summary)
        textSummary = (TextView) rootView.findViewById(R.id.text_summary);
        textSummary.setText("Welcome.");
        textSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationService.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationService.onPause();
    }

    /*
     * Called when we receive location events
     */

    private class FragmentLocationCallback implements LocationCallback {
        @Override
        public void onLocationReady() {
            refreshData();
        }
    }

    /*
     * Open a map intent to navigate to the station
     * Format is geo:0,0?q=latitude,longitude(label)"
     * See: https://developers.google.com/maps/documentation/android/intents#search_for_a_location
     */

    private void navigate(ClosestResponse response) {
        // Only navigate if we have a station to go to
        if (response == null || !response.station.isValid() ) {
            String message = "Sorry, we couldn't find any stations available.";
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        String uri = null;
        try {
            uri = String.format(
                    "geo:0,0?q=%f,%f(%s)",
                    response.station.latitude,
                    response.station.longitude,
                    URLEncoder.encode(response.station.name, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.d(LOG_TAG, "URLEncoder failed for station name: " + response.station.name);
        }

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    }

    /*
     * Refresh data logic
     */

    private void refreshData() {
        textSummary.setText("Loading...");

        // Reset the data
        targetBike = null;
        targetDock = null;

        // Get the latest location
        Location location = this.locationService.getLastLocation();
        if (location == null) {
            textSummary.setText("Could not get your location (is your GPS on?). Tap me to retry.");
            return;
        }

        // Got location
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Call the API
        textBikes.setText("Finding you a bike...");
        apiService.getClosestBike(latitude, longitude, new ClosestBikeCallback());

        textDocks.setText("Finding you a dock...");
        apiService.getClosestDock(latitude, longitude, new ClosestDockCallback());

        textSummary.setText("Getting you the situation summary...");
        apiService.getStatus(latitude, longitude, new StatusCallback());
    }

    /*
     * API callbacks
     */

    class ClosestBikeCallback implements Callback<ClosestResponse> {

        @Override
        public void success(ClosestResponse closestResponse, Response response) {
            if (closestResponse == null || closestResponse.code != 200) {
                textBikes.setText("Oh noes, we couldn't find you a bike nearby.");
            } else {
                targetBike = closestResponse;
                String summary = closestResponse.station.getBikesSummary();
                if(closestResponse.station.isValid()) { summary += " Tap me to navigate."; }
                textBikes.setText(summary);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "ClosestBikeCallback failed: " + error.toString());
            textBikes.setText("Oh noes, we couldn't get you the data.");
        }
    }

    class ClosestDockCallback implements Callback<ClosestResponse> {

        @Override
        public void success(ClosestResponse closestResponse, Response response) {
            if (closestResponse == null || closestResponse.code != 200) {
                textDocks.setText("Oh noes, we couldn't find you a dock nearby.");
            } else {
                targetDock = closestResponse;
                String summary = closestResponse.station.getDocksSummary();
                if(closestResponse.station.isValid()) { summary += " Tap me to navigate."; }
                textDocks.setText(summary);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "ClosestDockCallback failed: " + error.toString());
            textDocks.setText("Oh noes, we couldn't get you the data");
        }
    }

    class StatusCallback implements Callback<StatusResponse> {

        @Override
        public void success(StatusResponse statusResponse, Response response) {
            if (statusResponse == null || statusResponse.code != 200) {
                textSummary.setText("Sorry, I got nothing.");
            } else {
                textSummary.setText(statusResponse.status.getSummary()
                        + " Tap me to refresh.");
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "StatusCallback failed: " + error.toString());
            textSummary.setText(
                    "Our server is misbehaving (or your Internet connection is down?), "
                            + "please tap me again in a few to retry.");
        }
    }

}
