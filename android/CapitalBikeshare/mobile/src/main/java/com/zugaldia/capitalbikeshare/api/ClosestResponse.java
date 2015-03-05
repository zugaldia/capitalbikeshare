package com.zugaldia.capitalbikeshare.api;

/**
 * Model for a closest bike/dock response
 */
public class ClosestResponse {
    public int code;
    public StationObject station;

    class StationObject {
        public double distance_m;
        public double distance_min;
        public double latitude;
        public double longitude;
        public String name;
        public int nb_bikes;
        public int nb_empty_docks;
    }
}
