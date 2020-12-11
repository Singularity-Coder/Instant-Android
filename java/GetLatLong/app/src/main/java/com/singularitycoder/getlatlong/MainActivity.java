package com.singularitycoder.getlatlong;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.singularitycoder.getlatlong.databinding.ActivityMainBinding;

public final class MainActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final int REQUEST_CODE_LOCATION = 101;

    @Nullable
    private GoogleApiClient googleApiClient;

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialiseGoogleApiClient();
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

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            Location location = null;
            try {
                FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
                location = fusedLocationProviderApi.getLastLocation(googleApiClient);
            } catch (Exception ignored) {
            }

            if (null == location) {
                binding.tvLatLong.setText("Unable to get location");
            } else {
                binding.tvLatLong.setText(location.getLatitude() + " and " + location.getLongitude());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Need Location Permission for this feature to work!", Toast.LENGTH_SHORT);
            }
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
        binding.btnGetLocation.setOnClickListener(view -> getCurrentLocation());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Location Connection Suspended", Toast.LENGTH_SHORT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Location Connection Failed", Toast.LENGTH_SHORT);
    }
}