package com.example.foodigo1;


import android.Manifest;
import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;



import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;


public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10;
    private static final float LOCATION_DISTANCE = 0.3f;
    private double latitude, longitude, altitude;
    private Location location;
    FusedLocationProviderClient fusedLocationClient;


    private LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LatLng bitmap;

    public Location getLocation() {
        return location;
    }


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "**************LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "*************onLocationChanged: " + location);


            mLastLocation.set(location);
            if (mListener != null) mListener.onLocationChanged(location);
            //new DistanceTask(this,this.getBitmap());
            //new DistanceTask(this, bitmap).execute(point);
            //LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            // new DistanceTask(this, bitmap).execute(point);


        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    public double[] getCurrentLocation() {
        // Vérifiez les permissions de localisation
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Si les permissions de localisation ne sont pas accordées, retournez null
            return null;
        }


        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            this.altitude = location.getAltitude();

            System.out.println("********GEtCurrentLocation() Les locations ont été mis à jour !");
            return new double[]{latitude, longitude};
        } else {
            return null;
        }
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    public double getAltitude() {
        return altitude;
    }

    public void setBitmap(LatLng bitmap) {
        this.bitmap = bitmap;
    }

    public Task<Location> getCurrentLocationWithTask() {
        // Créez une instance de FusedLocationProviderClient
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Créez une tâche de retour de valeur
        TaskCompletionSource<Location> taskSource = new TaskCompletionSource<>();
        // Appelez la méthode getLastLocation asynchrone de FusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // La localisation a été récupérée avec succès, définissez la valeur de retour de la tâche et terminez-la
                        taskSource.setResult(location);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La localisation n'a pas pu être récupérée, terminez la tâche avec une erreur
                        taskSource.setException(e);
                    }
                });
        // Retournez la tâche de retour de valeur
        return taskSource.getTask();
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }
    private LocationService.OnLocationChangedListener mListener;

    public void setOnLocationChangedListener(LocationService.OnLocationChangedListener listener) {
        mListener = listener;
        Log.e(TAG, "setOnLocationChangedListener" + mListener);
    }
}