package com.singularitycoder.httpurlconnection2.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public final class LocationTracker extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS = 10; // 10 meters
    private static final long MIN_TIME_BTW_UPDATES_IN_MILLIS = 1000 * 60 * 1; // 1 minute

    private double latitude;
    private double longitude;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    @NonNull
    private final Context mContext;

    @Nullable
    private Location location;

    @Nullable
    protected LocationManager locationManager;

    public LocationTracker(Context context) {
        this.mContext = context;
        initialiseLocationManager();
        checkIfLocationProvidersAreEnabled();
        getLocation();
    }

    private void initialiseLocationManager() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        } catch (Exception ignored) {
        }
    }

    private void checkIfLocationProvidersAreEnabled() {
        try {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }
    }

    public void getLocation() {
        if (!isGPSEnabled && !isNetworkEnabled) return;
        try {
            this.canGetLocation = true;
            getLocationFromNetworkProvider();
            getLocationFromGPS();
        } catch (Exception ignored) {
        }
    }

    private void getLocationFromGPS() {
        if (!isGPSEnabled) return;
        if (location != null) return;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BTW_UPDATES_IN_MILLIS,
                MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS, this);

        if (locationManager == null) return;
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) return;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void getLocationFromNetworkProvider() {
        if (!isNetworkEnabled) return;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BTW_UPDATES_IN_MILLIS,
                MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS, this);

        if (locationManager == null) return;
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location == null) return;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public void stopUsingGPS() {
        if (locationManager == null) return;
        locationManager.removeUpdates(LocationTracker.this);
    }

    public double getLatitude() {
        if (location != null) latitude = location.getLatitude();
        return latitude;
    }

    public double getLongitude() {
        if (location != null) longitude = location.getLongitude();
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Location Settings");
        alertDialog.setMessage("GPS is not enabled. Please go to settings menu and enable it for the app to work properly!");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Settings".toUpperCase(), (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        });
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}