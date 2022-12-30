package com.example.foodigo1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationGiver {

    public LocationGiver(Activity activity_){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(activity_);
        this.activity=activity_;


    }
    private Activity activity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=100;
    private int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    private Double longitude, latitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void findLocation( ){
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latitude = addresses.get(0).getLatitude();
                            longitude=addresses.get(0).getLongitude();
                            System.out.println("La latitude est :" + latitude + " La longitude est : "+ longitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

        }else{

            permissionAsk();
            //fournisseur=LocationManager.GPS_PROVIDER;

        }

    }

    private void permissionAsk() {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        },REQUEST_CODE);
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                findLocation();
            }
            else{
                Toast.makeText(activity.getApplicationContext(),"Permission Required",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

 */
}
