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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens to requests from the watch
 */
public class AppWearableListenerService extends WearableListenerService {

    private final String LOG_TAG = AppWearableListenerService.class.getSimpleName();

    public static final String PATH_REQUEST_FIND_BIKE = "/request/findBike";
    public static final String PATH_REQUEST_FIND_DOCK = "/request/findDock";
    public static final String PATH_REQUEST_GET_STATUS = "/request/getStatus";
    public static final String PATH_RESPONSE_FIND_BIKE = "/response/findBike";
    public static final String PATH_RESPONSE_FIND_DOCK = "/response/findDock";
    public static final String PATH_RESPONSE_GET_STATUS = "/response/getStatus";

    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_TEXT = "TEXT";
    public static final String KEY_TIMESTAMP = "TIMESTAMP";

    private GoogleApiClient mGoogleApiClient;

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

        // Loop through the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                double latitude = dataMap.getDouble(KEY_LATITUDE);
                double longitude = dataMap.getDouble(KEY_LONGITUDE);
                if (item.getUri().getPath().equals(PATH_REQUEST_FIND_BIKE)) {
                    handleFindBike(latitude, longitude);
                } else if (item.getUri().getPath().equals(PATH_REQUEST_FIND_DOCK)) {
                    handleFindDock(latitude, longitude);
                } else if (item.getUri().getPath().equals(PATH_REQUEST_GET_STATUS)) {
                    handleGetStatus(latitude, longitude);
                }
            }
        }
    }

    /*
     * Handle actions
     */

    private void handleFindBike(double latitude, double longitude) {
        Log.d(LOG_TAG, "PATH_REQUEST_FIND_BIKE request!");
        putRequest(PATH_RESPONSE_FIND_BIKE, "Coming soon!");
    }

    private void handleFindDock(double latitude, double longitude) {
        Log.d(LOG_TAG, "PATH_REQUEST_FIND_DOCK request!");
        putRequest(PATH_RESPONSE_FIND_DOCK, "Coming soon!");
    }

    private void handleGetStatus(double latitude, double longitude) {
        Log.d(LOG_TAG, "PATH_REQUEST_GET_STATUS request!");
        putRequest(PATH_RESPONSE_GET_STATUS, "Coming soon!");
    }

    /*
     * Send the response
     */

    public void putRequest(String path, String text) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        putDataMapRequest.getDataMap().putString(KEY_TEXT, text);
        putDataMapRequest.getDataMap().putLong(KEY_TIMESTAMP, new Date().getTime());
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }
}
