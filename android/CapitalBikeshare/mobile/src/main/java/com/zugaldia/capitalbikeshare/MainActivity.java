package com.zugaldia.capitalbikeshare;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.joshdholtz.sentry.Sentry;
import com.zugaldia.capitalbikeshare.common.AppConfig;

/**
 * Main app activity
 */
public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        // Sentry
        initSentry();
    }

    /*
     * We now use Sentry
     * https://github.com/joshdholtz/Sentry-Android
     */

    public void initSentry() {
        Log.d(LOG_TAG, "initSentry");
        Sentry.init(this.getApplicationContext(), AppConfig.SENTRY_DSN);
    }

    /*
     * Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // About page
        if (id == R.id.action_about) {
            Toast toast = Toast.makeText(this, "Nothing here, yet.", Toast.LENGTH_LONG);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
