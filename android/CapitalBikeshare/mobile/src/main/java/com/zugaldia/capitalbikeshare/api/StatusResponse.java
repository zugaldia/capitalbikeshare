package com.zugaldia.capitalbikeshare.api;

/**
 * Model for a status response
 */
public class StatusResponse {
    public int code;
    public StatusObject status;

    public class StatusObject {
        public int bikes;
        public int docks;
        public int stations;

        public String getBikesText() {
            if (bikes == 0) { return "no bikes"; }
            return (bikes == 1 ? "1 bike" : String.format("%d bikes", bikes));
        }

        public String getDocksText() {
            if (docks == 0) { return "no empty docks"; }
            return (docks == 1 ? "1 empty dock" : String.format("%d empty docks", docks));
        }

        public String getStationsText() {
            if (stations == 0) { return "no stations"; }
            return (stations == 1 ? "1 station" : String.format("%d stations", stations));
        }
    }
}
