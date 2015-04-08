package com.zugaldia.capitalbikeshare;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.wearable.DataMap;
import com.zugaldia.capitalbikeshare.common.AppConstants;
import com.zugaldia.capitalbikeshare.data.DataService;
import com.zugaldia.capitalbikeshare.data.ResponseCallback;
import com.zugaldia.capitalbikeshare.location.LocationCallback;
import com.zugaldia.capitalbikeshare.location.LocationService;

import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int SPEECH_REQUEST_CODE = 0;

    // Dataset for the list
    private static final int OPTION_FIND_BIKE = 0;
    private static final int OPTION_FIND_DOCK = 1;
    private static final int OPTION_GET_STATUS = 2;
    String[] elements = {
            "Find me a bike",
            "Find me a dock",
            "Get status nearby" };

    // Our services
    private DataService dataService;
    private LocationService locationService;

    // UI elements
    private LinearLayout progressBarContainer;
    private WearableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_layout);

        // Progress bar (when loading content)
        progressBarContainer = (LinearLayout) findViewById(R.id.progress_bar_container);

        // Main list
        listView = (WearableListView) findViewById(R.id.wearable_list);
        listView.setAdapter(new WearableListViewAdapter(this, elements));
        listView.setClickListener(this);

        // Our services
        dataService = new DataService(this, new ActivityResponseCallback());
        locationService = new LocationService(this);
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        dataService.onResume();
        locationService.onResume();

        // Makes sure the UI is in a good state
        listView.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.GONE);

        // I don't like the UI as it is right now with voice recognition. Though the code
        // is here, let's rethink it before enabling it. I'm leaning towards a floating
        // mic on the top right of the screen.
        //displaySpeechRecognizer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataService.onPause();
        locationService.onPause();
    }

    /*
     * Handle free-form speech input
     * See: http://developer.android.com/training/wearables/apps/voice.html#FreeFormSpeech
     */

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Log.d(LOG_TAG, "displaySpeechRecognizer");

        // Prepare the intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult");

        // Handle result
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            handleVoiceCommand(spokenText);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleVoiceCommand(String command) {
        Log.d(LOG_TAG, "Command: " + command);

        // We should probably be more flexible than this
        if (command.toLowerCase().trim().equals("find me a bike")) {
            launchAction(OPTION_FIND_BIKE);
        } else if (command.toLowerCase().trim().equals("find me a dock")) {
            launchAction(OPTION_FIND_DOCK);
        } else if (command.toLowerCase().trim().equals("get the status")) {
            launchAction(OPTION_GET_STATUS);
        }
    }

    /*
     * Required by WearableListView.ClickListener
     */

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Log.d(LOG_TAG, "onClick");

        // Show progress bar (and hide list)
        progressBarContainer.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        // Get the tag and launch
        Integer tag = (Integer) viewHolder.itemView.getTag();
        launchAction(tag);
    }

    @Override
    public void onTopEmptyRegionClick() {
        Log.d(LOG_TAG, "onTopEmptyRegionClick");
    }

    /*
     * Handle actions
     */

    private void launchAction(final int tag) {
        Log.d(LOG_TAG, "Getting location for action: " + String.valueOf(tag));

        // Get location
        getLocation(new LocationCallback() {

            @Override
            public void success(Location location) {
                Log.d(LOG_TAG, "Location found.");
                if (tag == OPTION_FIND_BIKE) {
                    handleFindBike(location);
                } else if (tag == OPTION_FIND_DOCK) {
                    handleFindDock(location);
                } else if (tag == OPTION_GET_STATUS) {
                    handleGetStatus(location);
                }
            }

            @Override
            public void error() {
                Log.d(LOG_TAG, "Location NOT found.");
                launchErrorCard(
                        "We couldn't get your location, please try again later.");
            }
        });
    }

    private void handleFindBike(Location location) {
        Log.d(LOG_TAG, "handleFindBike");
        dataService.putRequest(
                AppConstants.PATH_REQUEST_FIND_BIKE,
                location.getLatitude(), location.getLongitude());
    }

    private void handleFindDock(Location location) {
        Log.d(LOG_TAG, "handleFindDock");
        dataService.putRequest(
                AppConstants.PATH_REQUEST_FIND_DOCK,
                location.getLatitude(), location.getLongitude());
    }

    private void handleGetStatus(Location location) {
        Log.d(LOG_TAG, "handleGetStatus");
        dataService.putRequest(
                AppConstants.PATH_REQUEST_GET_STATUS,
                location.getLatitude(), location.getLongitude());
    }

    /*
     * Get the location. Because we request location updates every UPDATE_INTERVAL_MS, it's possible
     * that when the user first launches the app, we don't a have fresh location yet. To avoid
     * that, we try up to LOCATION_MAX_ATTEMPTS to give the service a chance to get a location.
     * (Is there a better pattern?)
     */

    public void getLocation(final LocationCallback callback) {
        // Run in a separate thread to keep the UI responsive
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Loop
                int attempt = 0;
                Location location = null;
                while (location == null && attempt < AppConstants.LOCATION_MAX_ATTEMPTS) {
                    attempt += 1;
                    Log.d(LOG_TAG, "Attempt #" + String.valueOf(attempt));
                    location = locationService.getLastLocation();
                    if (location == null) {
                        try {
                            Thread.sleep(AppConstants.UPDATE_INTERVAL_MS);
                        } catch (InterruptedException e) {
                            Log.d(LOG_TAG, "Thread.sleep() was interrupted.");
                        }
                    }
                }

                // Resolve
                if (location == null) {
                    callback.error();
                } else {
                    callback.success(location);
                }
            }
        }).start();
    }

    /*
     * API response callback
     */

    class ActivityResponseCallback implements ResponseCallback {

        @Override
        public void success(String path, DataMap dataMap) {
            // Get data
            String text = dataMap.getString(AppConstants.KEY_TEXT);
            double latitude = dataMap.getDouble(AppConstants.KEY_LATITUDE);
            double longitude = dataMap.getDouble(AppConstants.KEY_LONGITUDE);

            // Debug
            Log.d(LOG_TAG, "path: " + path);
            Log.d(LOG_TAG, "text: " + text);
            Log.d(LOG_TAG, "latitude: " + String.valueOf(latitude));
            Log.d(LOG_TAG, "longitude: " + String.valueOf(longitude));

            // Choose a title
            String title = "Status";
            if (path.equals(AppConstants.PATH_RESPONSE_FIND_BIKE)) {
                title = "Bikes";
            } else if (path.equals(AppConstants.PATH_RESPONSE_FIND_DOCK)) {
                title = "Docks";
            }

            // Launch new activity (card)
            Intent intent = new Intent(getApplicationContext(), ResponseActivity.class);
            intent.putExtra(AppConstants.INTENT_EXTRA_CARD_TITLE, title);
            intent.putExtra(AppConstants.INTENT_EXTRA_CARD_DESCRIPTION, text);
            startActivity(intent);
        }

        @Override
        public void error(String path) {
            launchErrorCard(
                    "We couldn't get the info from Capital Bikeshare, please try again later.");
        }
    }

    // Show an error message with a consistent UI
    public void launchErrorCard(String message) {
        Intent intent = new Intent(getApplicationContext(), ResponseActivity.class);
        intent.putExtra(AppConstants.INTENT_EXTRA_CARD_TITLE, "Error");
        intent.putExtra(AppConstants.INTENT_EXTRA_CARD_DESCRIPTION, message);
        startActivity(intent);
    }
}
