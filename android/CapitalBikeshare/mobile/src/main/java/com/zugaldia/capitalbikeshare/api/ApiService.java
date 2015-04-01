package com.zugaldia.capitalbikeshare.api;

import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * See documentation here
 * https://github.com/zugaldia/capitalbikeshare/blob/master/appengine/README.md
 */
public class ApiService {

    private final String LOG_TAG = ApiService.class.getSimpleName();
    private final String ENDPOINT = "https://api-dot-com-zugaldia-capitalbikeshare.appspot.com";

    private ApiInterface service;

    public ApiService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        service = restAdapter.create(ApiInterface.class);
    }

    /*
     * API
     */

    public void getStatus(
            double latitude, double longitude, Callback<StatusResponse> callback) {
        service.getStatus(latitude, longitude, callback);
    }

    public void getClosestBike(
            double latitude, double longitude, Callback<ClosestResponse> callback) {
        service.getClosestBike(latitude, longitude, callback);
    }

    public void getClosestDock(
            double latitude, double longitude, Callback<ClosestResponse> callback) {
        service.getClosestDock(latitude, longitude, callback);
    }

}
