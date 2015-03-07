package com.zugaldia.capitalbikeshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.wearable.DataMap;
import com.zugaldia.capitalbikeshare.data.DataService;
import com.zugaldia.capitalbikeshare.data.ResponseCallback;
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
    private ProgressBar progressBar;
    private WearableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_layout);

        // Progress bar (when loading content)
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
        progressBar.setVisibility(View.GONE);

        // I don't like the UI as it is right now with voice recognition. Though the code
        // is here, let's rethink it before enabling it.
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
            handleFindBike();
        } else if (command.toLowerCase().trim().equals("find me a dock")) {
            handleFindDock();
        } else if (command.toLowerCase().trim().equals("get the status")) {
            handleGetStatus();
        }
    }

    /*
     * Required by WearableListView.ClickListener
     */

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Log.d(LOG_TAG, "onClick");

        // Show progress bar (and hide list)
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        Integer tag = (Integer) viewHolder.itemView.getTag();
        if (tag == OPTION_FIND_BIKE) {
            handleFindBike();
        } else if (tag == OPTION_FIND_DOCK) {
            handleFindDock();
        } else if (tag == OPTION_GET_STATUS) {
            handleGetStatus();
        }
    }

    @Override
    public void onTopEmptyRegionClick() {
        Log.d(LOG_TAG, "onTopEmptyRegionClick");
    }

    /*
     * Handle actions
     */

    private void handleFindBike() {
        Log.d(LOG_TAG, "handleFindBike");
        Location location = getLocation();
        if (location != null) {
            dataService.putRequest(
                    AppConstants.PATH_REQUEST_FIND_BIKE,
                    location.getLatitude(), location.getLongitude());
        }
    }

    private void handleFindDock() {
        Log.d(LOG_TAG, "handleFindDock");
        Location location = getLocation();
        if (location != null) {
            dataService.putRequest(
                    AppConstants.PATH_REQUEST_FIND_DOCK,
                    location.getLatitude(), location.getLongitude());
        }
    }

    private void handleGetStatus() {
        Log.d(LOG_TAG, "handleGetStatus");
        Location location = getLocation();
        if (location != null) {
            dataService.putRequest(
                    AppConstants.PATH_REQUEST_GET_STATUS,
                    location.getLatitude(), location.getLongitude());
        }
    }

    /*
     * Get location
     */

    public Location getLocation() {
        // This can be null, we should wait one second or so if that's the case
        Location location = locationService.getLastLocation();
        return location;
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
    }
}
