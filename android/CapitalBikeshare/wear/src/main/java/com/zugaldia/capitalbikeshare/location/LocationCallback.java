package com.zugaldia.capitalbikeshare.location;

import android.location.Location;

/**
 * Async Location hander
 */
public interface LocationCallback {

    public void success(Location location);
    public void error();

}
