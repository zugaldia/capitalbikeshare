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

        public boolean isValid() {
            if (name == null) { return false; }
            if (latitude == 0.0 && longitude == 0.0) { return false; }
            return true;
        }

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

        public String getBikesSummary() {
            if (nb_bikes == 0) { return "Sorry, we found no bikes."; }
            return String.format(
                    "We found %s on %s, which is %s.",
                    getBikesText(),
                    name,
                    getTimeText());
        }

        public String getDocksSummary() {
            if (nb_empty_docks == 0) { return "Sorry, we found no docks."; }
            return String.format(
                    "We found %s on %s, which is %s.",
                    getDocksText(),
                    name,
                    getTimeText());
        }
    }
}
