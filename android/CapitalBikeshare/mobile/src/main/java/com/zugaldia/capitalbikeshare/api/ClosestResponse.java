package com.zugaldia.capitalbikeshare.api;

/**
 * Model for a closest bike/dock response
 */
public class ClosestResponse {
    public int code;
    public StationObject station;

    public class StationObject {
        public double distance_min;
        public double latitude;
        public double longitude;
        public String name;
        public int nb_bikes;
        public int nb_empty_docks;

        public String getBikesText() {
            return (nb_bikes == 1 ? "1 bike" : String.format("%d bikes", nb_bikes));
        }

        public String getDocksText() {
            return (nb_empty_docks == 1 ? "1 empty dock" : String.format("%d empty docks", nb_empty_docks));
        }

        public String getTimeText() {
            if (distance_min < 1) {
                return "a short walk away";
            } else if (distance_min < 2) {
                return "about a minute walk";
            } else {
                return String.format("about a %.0f minutes walk", distance_min);
            }
        }
    }
}
