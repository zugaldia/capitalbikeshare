package com.zugaldia.capitalbikeshare.data;

import com.google.android.gms.wearable.DataMap;

/**
 * Called when the phone gets back to us with the API response
 */
public interface ResponseCallback {

    public void success(String path, DataMap dataMap);
    public void error(String path);

}
