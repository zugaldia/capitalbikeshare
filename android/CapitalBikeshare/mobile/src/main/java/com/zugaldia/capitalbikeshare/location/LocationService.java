package com.zugaldia.capitalbikeshare.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * App location logic
 */
public class LocationService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String LOG_TAG = LocationService.class.getSimpleName();

    private Context context;
    private LocationCallback locationCallback;
    private GoogleApiClient mGoogleApiClient;

    public LocationService(Context context, LocationCallback locationCallback) {
        Log.d(LOG_TAG, "LocationService");
        this.context = context;
        this.locationCallback = locationCallback;
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(LOG_TAG, "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*
     * Start & stop the connection to the client
     */

    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        mGoogleApiClient.connect();
    }

    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /*
     * Required by ConnectionCallbacks
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "onConnected");

        // We are now ready to request locations, we can now call
        // getLastLocation()
        this.locationCallback.onLocationReady();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(LOG_TAG, "onConnectionSuspended");

        // The connection to Google Play services was lost for some reason
        // We call connect() to attempt to re-establish the connection
        mGoogleApiClient.connect();
    }

    /*
     * Required by OnConnectionFailedListener
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult.toString());
    }

    /*
     * API
     */

    public Location getLastLocation() {
        Log.d(LOG_TAG, "getLastLocation");
        if (!mGoogleApiClient.isConnected()) {
            Log.d(LOG_TAG, "Cannot get location, connection is not ready.");
            return null;
        }

        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
}
