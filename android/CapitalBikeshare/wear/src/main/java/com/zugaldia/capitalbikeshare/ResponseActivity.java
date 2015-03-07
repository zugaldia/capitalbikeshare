package com.zugaldia.capitalbikeshare;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;


public class ResponseActivity extends Activity {

    private final String LOG_TAG = ResponseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_response);

        // Get intent data
        Intent intent = getIntent();
        String title = intent.getStringExtra(AppConstants.INTENT_EXTRA_CARD_TITLE);
        String description = intent.getStringExtra(AppConstants.INTENT_EXTRA_CARD_DESCRIPTION);

        // Adds the CardFragment instance to the activity
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CardFragment cardFragment = CardFragment.create(title, description, R.mipmap.ic_launcher);
        fragmentTransaction.add(R.id.frame_layout, cardFragment);
        fragmentTransaction.commit();
    }

}
