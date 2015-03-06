package com.zugaldia.capitalbikeshare;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.zugaldia.capitalbikeshare.data.DataService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_layout);

        // Main list
        WearableListView listView = (WearableListView) findViewById(R.id.wearable_list);
        listView.setAdapter(new WearableListViewAdapter(this, elements));
        listView.setClickListener(this);

        // Our services
        dataService = new DataService(this);
        locationService = new LocationService(this);
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        dataService.onResume();
        locationService.onResume();

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
        Location location = locationService.getLastLocation();
        if (location != null) {
            dataService.putRequest(
                    DataService.PATH_REQUEST_FIND_BIKE,
                    location.getLatitude(), location.getLongitude());
        }
    }

    private void handleFindDock() {
        Log.d(LOG_TAG, "handleFindDock");
        Location location = locationService.getLastLocation();
        if (location != null) {
            dataService.putRequest(
                    DataService.PATH_REQUEST_FIND_DOCK,
                    location.getLatitude(), location.getLongitude());
        }
    }

    private void handleGetStatus() {
        Log.d(LOG_TAG, "handleGetStatus");
        Location location = locationService.getLastLocation();
        if (location != null) {
            dataService.putRequest(
                    DataService.PATH_REQUEST_GET_STATUS,
                    location.getLatitude(), location.getLongitude());
        }
    }
}
