package com.example.weathermoodbac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;


public class GPSTracker implements LocationListener {
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private Activity act;

    public GPSTracker(Activity act) {
        this.act = act;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        try {
            locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 50, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
