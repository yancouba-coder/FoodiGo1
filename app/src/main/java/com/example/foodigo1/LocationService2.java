package com.example.foodigo1;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class LocationService2 extends Service {
    public LocationService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        // Créez une instance de FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Créez une demande de localisation avec les paramètres souhaités
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Toutes les 10 secondes
        locationRequest.setFastestInterval(5000); // Au plus tôt toutes les 5 secondes
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Créez un callback qui sera appelé à chaque changement de localisation
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                // Obtenez la localisation actuelle à l'aide de la fonction getCurrentLocationWithTask
                Task<Location> locationTask = getCurrentLocationWithTask();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // La localisation a été récupérée avec succès, faites quelque chose avec elle
                        // par exemple, affichez-la à l'écran
                        Log.d("LOCATION", "Latitude : " + location.getLatitude() + ", Longitude : " + location.getLongitude());
                    }
                });
                locationTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La localisation n'a pas pu être récupérée, affichez une erreur
                        Log.e("LOCATION", "Error getting location", e);
                    }
                });
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Demandez la mise à jour de la localisation
        startLocationUpdates();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Arrêtez les mises à jour de localisation
        stopLocationUpdates();
    }
    private void startLocationUpdates() {
        // Vérifiez les permissions de localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si les permissions ne sont
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
           // ActivityCompat.requestPermissions((Activity) this, permissions, 1);
        } else {
// Si les permissions sont déjà accordées, demandez les mises à jour de localisation
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }
    private void stopLocationUpdates() {
        // Arrêtez les mises à jour de localisation
        fusedLocationClient.removeLocationUpdates(locationCallback);
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



}