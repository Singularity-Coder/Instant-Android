package com.singularitycoder.geofencing;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static com.singularitycoder.geofencing.MainActivity.AREA_51_GEOFENCE_ID;

public class GeofenceService extends IntentService {

    private static final String TAG = "GeofenceIntentService";

    public GeofenceService() {
        super(TAG);
    }

    // 10. Handle geofence transitions
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "Geofencing Event Error " + geofencingEvent.getErrorCode());
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals(AREA_51_GEOFENCE_ID)) {
                Log.d(TAG, "You are inside Area 51");
            } else {
                Log.d(TAG, "You are outside Area 51");
            }
        }
    }
}