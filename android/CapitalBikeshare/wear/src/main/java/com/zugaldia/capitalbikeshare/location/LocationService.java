package com.zugaldia.capitalbikeshare.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.zugaldia.capitalbikeshare.common.AppConstants;

/**
 * A location service tailored for the wearable. Differently to the phone feature,
 * we're requesting updates in this case because getLastLocation() tends to be null.
 * See: http://developer.android.com/training/articles/wear-location-detection.html
 */
public class LocationService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String LOG_TAG = LocationService.class.getSimpleName();

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location location;

    public LocationService(Context context) {
        this.context = context;
        buildGoogleApiClient();

        // Jus for debugging purposes
        String message = hasGps() ? "has on-board GPS" : "does not have on-board GPS";
        Log.d(LOG_TAG, "The watch " + message);
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
     * Detect on-board GPS. This is somewhat irrelevant for this app because we need the
     * phone anyway to place the API call. Other apps only depending on GPS can use this
     * to warn the user or change their flow appropriately.
     */

    private boolean hasGps() {
        return this.context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_LOCATION_GPS);
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
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /*
     * Required by GoogleApiClient.ConnectionCallbacks
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "onConnected");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(LOG_TAG, "onConnectionSuspended");

        // The connection to Google Play services was lost for some reason
        // We call connect() to attempt to re-establish the connection
        mGoogleApiClient.connect();
    }

    /*
     * Required by GoogleApiClient.OnConnectionFailedListener
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed");
    }

    /*
     * API
     */

    public Location getLastLocation() {
        Log.d(LOG_TAG, "getLastLocation");

        // From one of the updates
        if (this.location != null) {
            return this.location;
        }

        // Try getLastLocation() otherwise
        if (mGoogleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        // Nothing
        return null;
    }

    /*
     * Required by LocationListener
     */

    private void requestLocationUpdates() {
        Log.d(LOG_TAG, "requestLocationUpdates");

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(AppConstants.UPDATE_INTERVAL_MS)
                .setFastestInterval(AppConstants.FASTEST_INTERVAL_MS);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            Log.d(LOG_TAG, "Successfully requested location updates");
                        } else {
                            Log.e(LOG_TAG,
                                    "Failed in requesting location updates, "
                                            + "status code: "
                                            + status.getStatusCode()
                                            + ", message: "
                                            + status.getStatusMessage());
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged");
        this.location = location;
    }
}
