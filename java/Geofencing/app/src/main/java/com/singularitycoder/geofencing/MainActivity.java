package com.singularitycoder.geofencing;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

// 1. Connect to Google Play services - Steps 1 to 4
// 2. Request Location updates - Steps 5
// 2.1. Stop Location updates when activity is no longer in focus - Steps 6
// 3. Create Geofence objects - to create geofence objects u need latitude, longitude, radius, expiration time, transition time, geofence ID - Steps 7 n 8
// 3.1. Specify geofences n initial triggers - Steps 9
// 4. Handles Geofence trnsisitons -  Steps 10 - 12
// 5. Stop geofece monitoring - Steps 13

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // 7. Create Geofence objects - to create geofence objects u need latitude, longitude, radius, expiration time, transition time, geofence ID
    public static final String AREA_51_GEOFENCE_ID = "AREA 51";
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    /**
     * Map for storing information about Area 51.
     */
    public static final HashMap<String, LatLng> NEARBY_LOCS = new HashMap<String, LatLng>();

    static {
        // Area 51
        NEARBY_LOCS.put(AREA_51_GEOFENCE_ID, new LatLng(33.677156, -117.767582));
    }

    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;

    private GoogleMap googleMap;

    private GeofencingRequest geofencingRequest;
    private GoogleApiClient googleApiClient;

    private boolean isMonitoring = false;

    private MarkerOptions markerOptions;

    private Marker currentLocationMarker;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 1. create instance of Google API client. Use builder class to add Location services API
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        // 2. Users grant permissions while running app from 6.0
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
        }
    }

    // 3.1 call back interface that adds to Google API client
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Client Connected");
        isMonitoring = true;
        startGeofencing();
        startLocationMonitor();
    }

    // 3.2 call back interface that adds to Google API client
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Connection Suspended");
    }

    // 3.3 call back interface that adds to Google API client
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isMonitoring = false;
        Log.e(TAG, "Connection Failed: " + connectionResult.getErrorMessage());
    }

    // 4. connect n disconnect
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
    }

    // 4...
    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    // 5. make a location request by connecting to location services
    private void startLocationMonitor() {
        Log.d(TAG, "Start Location Monitor");
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }
                    markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                    markerOptions.title("Current Location");
                    currentLocationMarker = googleMap.addMarker(markerOptions);
                    Log.d(TAG, "Location Change Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    // 6. Stop location updates when activity is no longer in focus
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // 6...
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (LocationListener) this);
    }

    // 8. Create n add geofences using location API's builder class for creating Geofence objects n convenience class for adding them
    @NonNull
    private Geofence getGeofence() {
        LatLng latLng = NEARBY_LOCS.get(AREA_51_GEOFENCE_ID);
        return new Geofence.Builder()
                .setRequestId(AREA_51_GEOFENCE_ID)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS_IN_METERS)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    // 9. Specify geofences n intitial triggers

//    GEOFENCE_TRANSITION_ENTER: Triggers when device enters geofence
//    GEOFENCE_TRANSITION_EXIT: Triggers when device exits geofence
//    INITIAL_TRIGGER_ENTER: Tells LocationServices that it should be triggered if the device is already inside the geofence
//    INITIAL_TRIGGER_DWELL: Triggers events only when the device stops for a specified duration within a geofence. This reduces notifications in the form of alerts.

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences((List<Geofence>) getGeofence());
        return builder.build();
    }

    // 11. Define intent for geofence transitions
    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // 12. Add Geofences
    private void startGeofencing() {
        Log.d(TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();
        geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofence(getGeofence())
                .build();

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Google API client is not connected");
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Successfully Geofencing Connected");
                        } else {
                            Log.d(TAG, "Failed to add Geofencing " + status.getStatus());
                        }
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        isMonitoring = true;
        invalidateOptionsMenu();
    }

    // 13. Stop Geofence Monitoring - removes geofences - stops all further notifications when the device enters or exits previously added geofences
    private void stopGeoFencing() {
        pendingIntent = getGeofencePendingIntent();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess())
                    Log.d(TAG, "Stop Geofencing");
                else
                    Log.d(TAG, "Don't Stop Geofencing");
            }
        });
        isMonitoring = false;
        invalidateOptionsMenu();
    }


    @Override
    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Service Is MISSING");
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, response, 1).show();
        } else {
            Log.d(TAG, "Google Play Service Is AVAILABLE");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_monitor, menu);
        if (isMonitoring) {
            menu.findItem(R.id.start_monitor).setVisible(false);
            menu.findItem(R.id.stop_monitor).setVisible(true);
        } else {
            menu.findItem(R.id.start_monitor).setVisible(true);
            menu.findItem(R.id.stop_monitor).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_monitor:
                startGeofencing();
                break;
            case R.id.stop_monitor:
                stopGeoFencing();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final FragmentManager fragManager = this.getFragmentManager();
        final Fragment fragment = fragManager.findFragmentById(R.id.map);
        if (fragment != null) {
            fragManager.beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.googleMap = googleMap;
        LatLng latLng = NEARBY_LOCS.get(AREA_51_GEOFENCE_ID);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Area 51"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
        googleMap.setMyLocationEnabled(true);
        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(latLng.latitude, latLng.longitude))
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .strokeWidth(2f));
    }


}