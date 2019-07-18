package com.loopme.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.loopme.Logging;

import java.util.concurrent.TimeUnit;

// TODO. Refactor.
public class GoogleLocationService {

    private static final String LOG_TAG = GoogleLocationService.class.getSimpleName();

    private GoogleLocationService() {
    }

    private static boolean isLocationServiceAvailable(Context context) {
        boolean hasLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        if (!hasLocationPermission)
            return false;

        GoogleApiAvailability gpa = GoogleApiAvailability.getInstance();
        return gpa.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    @SuppressLint("MissingPermission")
    public static Location getLocation(Context context) {
        Context appCtx = context.getApplicationContext();

        if (!isLocationServiceAvailable(appCtx))
            return null;

        try {
            Task<android.location.Location> task =
                    LocationServices.getFusedLocationProviderClient(appCtx).getLastLocation();

            Tasks.await(task, 1, TimeUnit.SECONDS);

            return task.getResult();

        } catch (Exception ex) {
            Logging.out(LOG_TAG, ex.toString());
            return null;
        }
    }
}
