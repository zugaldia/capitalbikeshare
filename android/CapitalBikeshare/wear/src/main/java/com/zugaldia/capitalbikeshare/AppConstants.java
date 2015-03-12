package com.zugaldia.capitalbikeshare;

/**
 * Shared by all app classes
 */
public class AppConstants {

    public static final String PATH_REQUEST_FIND_BIKE = "/request/findBike";
    public static final String PATH_REQUEST_FIND_DOCK = "/request/findDock";
    public static final String PATH_REQUEST_GET_STATUS = "/request/getStatus";
    public static final String PATH_RESPONSE_FIND_BIKE = "/response/findBike";
    public static final String PATH_RESPONSE_FIND_DOCK = "/response/findDock";
    public static final String PATH_RESPONSE_GET_STATUS = "/response/getStatus";

    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_TEXT = "TEXT";
    public static final String KEY_TIMESTAMP = "TIMESTAMP";

    public final static String INTENT_EXTRA_CARD_TITLE = "CARD_TITLE";
    public final static String INTENT_EXTRA_CARD_DESCRIPTION = "CARD_DESCRIPTION";

    /*
     * Location updates
     * For sample values, see: AndroidSDK -> samples -> android-22 -> wearable -> SpeedTracker ->
     * Wearable -> src -> main -> java -> com -> example -> android -> wearable -> speedtracker
     */

    public static final long UPDATE_INTERVAL_MS = 1 * 1000;
    public static final long FASTEST_INTERVAL_MS = 1 * 1000;
    public static final int LOCATION_MAX_ATTEMPTS = 3;

}
