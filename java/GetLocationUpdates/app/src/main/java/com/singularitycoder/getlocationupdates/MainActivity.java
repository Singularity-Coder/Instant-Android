package com.singularitycoder.getlocationupdates;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.singularitycoder.getlocationupdates.databinding.ActivityMainBinding;

import static java.lang.String.valueOf;

public final class MainActivity
        extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final float MIN_DISPLACEMENT_IN_METERS = 0.1f;
    private final int REQUEST_CODE_LOCATION = 99;

    private boolean isConnected;

    @NonNull
    private final String TAG = "MainActivity";

    @Nullable
    private GoogleApiClient googleApiClient;

    @Nullable
    private LocationRequest locationRequest;

    @Nullable
    private ActivityMainBinding binding;

    @NonNull
    private LocationListener locationListener = location -> binding.tvLatLong.setText(location.getLatitude() + " and " + location.getLongitude());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialiseGoogleApiClient();
        setUpLocationRequestParams();
        getLocationUpdates();
        setUpListeners();
    }

    private void initialiseGoogleApiClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } catch (Exception ignored) {
        }
    }

    private void setUpLocationRequestParams() {
        try {
            locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(MIN_DISPLACEMENT_IN_METERS);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } catch (Exception ignored) {
        }
    }

    private void getLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);   // If permission not granted
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
//        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, looper);
            }
        } catch (Exception ignored) {
        }
    }

    private void setUpListeners() {
        binding.btnStartFetching.setOnClickListener(view -> startFetchingLocation());
        binding.btnStopFetching.setOnClickListener(view -> stopFetchingLocation());
    }

    private void startFetchingLocation() {
        if (isConnected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.btnStartFetching.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
            }
            binding.btnStartFetching.setEnabled(false);
            getLocationUpdates();
        } else {
            Toast.makeText(MainActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopFetchingLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.btnStartFetching.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
        }
        binding.tvLatLong.setText("");
        binding.btnStartFetching.setEnabled(true);
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            googleApiClient.connect();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            googleApiClient.disconnect();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);   // If permission not granted
        } else {
            Location location = null;
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            } catch (Exception ignored) {
            }
            Log.d(TAG, "onConnected: lat: " + valueOf(location.getLatitude()));
            Log.d(TAG, "onConnected: long: " + valueOf(location.getLongitude()));
        }

        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isConnected = false;
    }
}