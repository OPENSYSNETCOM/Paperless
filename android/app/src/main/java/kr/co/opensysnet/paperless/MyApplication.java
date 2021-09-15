package kr.co.opensysnet.paperless;

import android.app.Application;

public class MyApplication extends Application {
    private static double gLatitude;
    private static double gLongitude;

    public void setLocation(double latitude, double longitude) {
        gLatitude = latitude;
        gLongitude = longitude;
    }

    public double getLatitude() {
        return gLatitude;
    }

    public double getLongitude() {
        return gLongitude;
    }
}
