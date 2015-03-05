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
                if (targetBike != null) {
                    navigate(targetBike);
                }
            }
        });

        // Second text view (available docks)
        textDocks = (TextView) rootView.findViewById(R.id.text_docks);
        textDocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetDock != null) {
                    navigate(targetDock);
                }
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
    public void onStart() {
        super.onStart();
        locationService.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationService.onStop();
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

        textSummary.setText("Getting you a situation summary...");
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
                // Track station
                targetBike = closestResponse;

                // Build message
                String message = String.format(
                        "We found %s on %s, which is %s. Tap me to navigate.",
                        closestResponse.station.getBikesText(),
                        closestResponse.station.name,
                        closestResponse.station.getTimeText());
                textBikes.setText(message);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            textBikes.setText("Failed: " + error.toString());
        }
    }

    class ClosestDockCallback implements Callback<ClosestResponse> {

        @Override
        public void success(ClosestResponse closestResponse, Response response) {
            if (closestResponse == null || closestResponse.code != 200) {
                textDocks.setText("Oh noes, we couldn't find you a dock nearby.");
            } else {
                // Track station
                targetDock = closestResponse;

                // Build message
                String message = String.format(
                        "We found %s on %s, which is %s. Tap me to navigate.",
                        closestResponse.station.getDocksText(),
                        closestResponse.station.name,
                        closestResponse.station.getTimeText());
                textDocks.setText(message);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            textDocks.setText("Failed: " + error.toString());
        }
    }

    class StatusCallback implements Callback<StatusResponse> {

        @Override
        public void success(StatusResponse statusResponse, Response response) {
            if (statusResponse == null || statusResponse.code != 200) {
                textSummary.setText("Sorry, I got nothing.");
            } else {
                // Build message
                String message = String.format(
                        "We found %s with %s and %s in the vecinity.",
                        statusResponse.status.getStationsText(),
                        statusResponse.status.getBikesText(),
                        statusResponse.status.getDocksText());
                textSummary.setText(message);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            textSummary.setText(
                    String.format(
                            "Oops, something went wrong (%s). Tap me to retry.",
                            error.toString()));
        }
    }

}
