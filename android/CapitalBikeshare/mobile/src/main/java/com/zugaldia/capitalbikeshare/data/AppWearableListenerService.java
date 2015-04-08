package com.zugaldia.capitalbikeshare.data;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.zugaldia.capitalbikeshare.api.ApiService;
import com.zugaldia.capitalbikeshare.api.ClosestResponse;
import com.zugaldia.capitalbikeshare.api.StatusResponse;
import com.zugaldia.capitalbikeshare.common.AppConstants;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Listens to requests from the watch
 */
public class AppWearableListenerService extends WearableListenerService {

    private final String LOG_TAG = AppWearableListenerService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private ApiService apiService;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(LOG_TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        // Build our API service as well
        apiService = new ApiService();

        // Loop through the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                double latitude = dataMap.getDouble(AppConstants.KEY_LATITUDE);
                double longitude = dataMap.getDouble(AppConstants.KEY_LONGITUDE);
                if (item.getUri().getPath().equals(AppConstants.PATH_REQUEST_FIND_BIKE)) {
                    handleFindBike(latitude, longitude);
                } else if (item.getUri().getPath().equals(AppConstants.PATH_REQUEST_FIND_DOCK)) {
                    handleFindDock(latitude, longitude);
                } else if (item.getUri().getPath().equals(AppConstants.PATH_REQUEST_GET_STATUS)) {
                    handleGetStatus(latitude, longitude);
                }
            }
        }
    }

    /*
     * Handle actions
     */

    private void handleFindBike(double latitude, double longitude) {
        Log.d(LOG_TAG, "handleFindBike");
        apiService.getClosestBike(
                latitude, longitude, new ClosestBikeCallback());
    }

    private void handleFindDock(double latitude, double longitude) {
        Log.d(LOG_TAG, "handleFindDock");
        apiService.getClosestDock(
                latitude, longitude, new ClosestDockCallback());
    }

    private void handleGetStatus(double latitude, double longitude) {
        Log.d(LOG_TAG, "handleGetStatus");
        apiService.getStatus(
                latitude, longitude, new StatusCallback());
    }

    /*
     * Send the response
     */

    public void putRequest(String path, String text, double latitude, double longitude) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        putDataMapRequest.getDataMap().putString(AppConstants.KEY_TEXT, text);
        putDataMapRequest.getDataMap().putDouble(AppConstants.KEY_LATITUDE, latitude);
        putDataMapRequest.getDataMap().putDouble(AppConstants.KEY_LONGITUDE, longitude);
        putDataMapRequest.getDataMap().putLong(AppConstants.KEY_TIMESTAMP, new Date().getTime());
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }

    /*
     * API callbacks
     */

    class ClosestBikeCallback implements Callback<ClosestResponse> {

        @Override
        public void success(ClosestResponse closestResponse, Response response) {
            if (closestResponse == null || closestResponse.status_code != 200) {
                Log.d(LOG_TAG, "Failed: Server Error");
                putRequest(AppConstants.PATH_RESPONSE_FIND_BIKE, "ERROR", 0.0, 0.0);
            } else {
                putRequest(
                        AppConstants.PATH_RESPONSE_FIND_BIKE,
                        closestResponse.station.getBikesSummary(),
                        closestResponse.station.latitude,
                        closestResponse.station.longitude);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "Failed: " + error.toString());
            putRequest(AppConstants.PATH_RESPONSE_FIND_BIKE, "ERROR", 0.0, 0.0);
        }
    }

    class ClosestDockCallback implements Callback<ClosestResponse> {

        @Override
        public void success(ClosestResponse closestResponse, Response response) {
            if (closestResponse == null || closestResponse.status_code != 200) {
                Log.d(LOG_TAG, "Failed: Server Error");
                putRequest(AppConstants.PATH_RESPONSE_FIND_DOCK, "ERROR", 0.0, 0.0);
            } else {
                putRequest(
                        AppConstants.PATH_RESPONSE_FIND_DOCK,
                        closestResponse.station.getDocksSummary(),
                        closestResponse.station.latitude,
                        closestResponse.station.longitude);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "Failed: " + error.toString());
            putRequest(AppConstants.PATH_RESPONSE_FIND_DOCK, "ERROR", 0.0, 0.0);
        }
    }

    class StatusCallback implements Callback<StatusResponse> {

        @Override
        public void success(StatusResponse statusResponse, Response response) {
            if (statusResponse == null || statusResponse.status_code != 200) {
                Log.d(LOG_TAG, "Failed: Server Error");
                putRequest(AppConstants.PATH_RESPONSE_GET_STATUS, "ERROR", 0.0, 0.0);
            } else {
                // There's no latlon in this response
                putRequest(
                        AppConstants.PATH_RESPONSE_GET_STATUS,
                        statusResponse.status.getSummary(),
                        0.0, 0.0);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, "Failed: " + error.toString());
            putRequest(AppConstants.PATH_RESPONSE_GET_STATUS, "ERROR", 0.0, 0.0);
        }
    }
}
