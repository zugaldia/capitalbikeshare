package com.zugaldia.capitalbikeshare.data;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.zugaldia.capitalbikeshare.AppConstants;

import java.util.Date;

/**
 * Takes care of the communication with the phone
 */
public class DataService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener {

    private final String LOG_TAG = DataService.class.getSimpleName();

    private Context context;
    private ResponseCallback responseCallback;
    private GoogleApiClient mGoogleApiClient;

    public DataService(Context context, ResponseCallback responseCallback) {
        this.context = context;
        this.responseCallback = responseCallback;
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        // Request access only to the Wearable API
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    /*
     * Start & stop the connection to the client
     */

    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        mGoogleApiClient.connect();
    }

    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    /*
     * Required by GoogleApiClient.ConnectionCallbacks
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "onConnected");

        // Now we can use the Data Layer API
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(LOG_TAG, "onConnectionSuspended: " + cause);

        // The connection to Google Play services was lost for some reason
        // We call connect() to attempt to re-establish the connection
        mGoogleApiClient.connect();
    }

    /*
     * Required by GoogleApiClient.OnConnectionFailedListener
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);
    }

    /*
     * Send the message
     */

    public void putRequest(String path, double latitude, double longitude) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        putDataMapRequest.getDataMap().putDouble(AppConstants.KEY_LATITUDE, latitude);
        putDataMapRequest.getDataMap().putDouble(AppConstants.KEY_LONGITUDE, longitude);
        putDataMapRequest.getDataMap().putLong(AppConstants.KEY_TIMESTAMP, new Date().getTime());
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }

    /*
     * Required by DataApi.DataListener
     */

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // Loop through the events
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                this.responseCallback.success(item.getUri().getPath(), dataMap);
            }
        }
    }
}
