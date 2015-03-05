package com.zugaldia.capitalbikeshare.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interface to our Capital Bike Share API
 */
public interface ApiInterface {

    @GET("/api/v1/data/closest_bike")
    void getClosestBike(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            Callback<ClosestResponse> cb);

    @GET("/api/v1/data/closest_dock")
    void getClosestDock(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            Callback<ClosestResponse> cb);

    @GET("/api/v1/data/status")
    void getStatus(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            Callback<StatusResponse> cb);

}
