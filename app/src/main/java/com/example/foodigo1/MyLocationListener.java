package com.example.foodigo1;

import static android.content.Context.LOCATION_SERVICE;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.security.Provider;

class MyLocationListener extends Service implements LocationListener {
    private Double latitude;
    private Double longitude;
    private String altitude;
    private String accuracy;
    private static final int REQUEST_CODE=100;
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    public MyLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }


    }

    public void onLocationChanged(Location localisation)
    {
        Log.d("GPS", "localisation : " + localisation.toString());
        String coordonnees = String.format("Latitude : %f - Longitude : %f\n", localisation.getLatitude(), localisation.getLongitude());
        latitude= localisation.getLatitude();
        longitude= localisation.getLongitude();
        altitude= String.valueOf(localisation.getAltitude());
        accuracy= String.valueOf(localisation.getAccuracy());
        Log.d("GPS", "coordonnees : " + coordonnees);
    }



    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    @Override
    public void onStatusChanged(String fournisseur, int status, Bundle extras)
    {
        //Toast.makeText(, fournisseur + " état : " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String fournisseur)
    {
       // Toast.makeText(, fournisseur + " activé !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String fournisseur)
    {
        //Toast.makeText(, fournisseur + " désactivé !", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
