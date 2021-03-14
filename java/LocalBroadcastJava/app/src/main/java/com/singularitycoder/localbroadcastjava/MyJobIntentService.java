package com.singularitycoder.localbroadcastjava;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Date;

import static com.singularitycoder.localbroadcastjava.AppConstants.INTENT_ACTION_DATE;
import static com.singularitycoder.localbroadcastjava.AppConstants.INTENT_DATA_DATE;

public final class MyJobIntentService extends JobIntentService {

    static void enqueueMyWork(@NonNull final Context context, @NonNull final Integer jobId, @NonNull final Intent intent) {
        enqueueWork(context, MyJobIntentService.class, jobId, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Send local broadcast
        final Intent myIntent = new Intent(INTENT_ACTION_DATE).putExtra(INTENT_DATA_DATE, new Date().toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }
}
