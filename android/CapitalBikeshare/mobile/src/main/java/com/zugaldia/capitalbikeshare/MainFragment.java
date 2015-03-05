package com.zugaldia.capitalbikeshare;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zugaldia.capitalbikeshare.api.ApiService;
import com.zugaldia.capitalbikeshare.api.StatusResponse;
import com.zugaldia.capitalbikeshare.location.LocationCallback;
import com.zugaldia.capitalbikeshare.location.LocationService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Main app fragment
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    private TextView textBikes;
    private TextView textDocks;
    private TextView textSummary;

    private LocationService locationService;
    private ApiService apiService;

    public MainFragment() {
        Log.d(LOG_TAG, "MainFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
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

        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        textBikes = (TextView) rootView.findViewById(R.id.text_bikes);
        textDocks = (TextView) rootView.findViewById(R.id.text_docks);
        textSummary = (TextView) rootView.findViewById(R.id.text_summary);
        return rootView;

    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
        locationService.onStart();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
        locationService.onStop();
    }

    /*
     * Called when we receive new location information
     */

    private class FragmentLocationCallback implements LocationCallback {

        @Override
        public void onLocationReady() {
            Log.d(LOG_TAG, "onLocationReady");
            refreshData();
        }
    }

    /*
     * Refresh data logic
     */

    private void refreshData() {
        Log.d(LOG_TAG, "refreshData");

        // Get latest location
        Location location = this.locationService.getLastLocation();
        if (location != null) {
            Log.d(LOG_TAG, String.valueOf(location.getLatitude()));
            Log.d(LOG_TAG, String.valueOf(location.getLongitude()));

            // Try the API
            apiService.getStatus(
                    location.getLatitude(),
                    location.getLongitude(),
                    new StatusCallback());
        }
    }

    /*
     * API callbacks
     */

    class StatusCallback implements Callback<StatusResponse> {

        @Override
        public void success(StatusResponse statusResponse, Response response) {
            Log.d(LOG_TAG, "success");
            Log.d(LOG_TAG, String.valueOf(statusResponse.status.stations));
            Log.d(LOG_TAG, String.valueOf(statusResponse.status.bikes));
            Log.d(LOG_TAG, String.valueOf(statusResponse.status.docks));
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "failure: " + error.toString());
        }
    }
}
