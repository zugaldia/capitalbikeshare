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
    }
}
