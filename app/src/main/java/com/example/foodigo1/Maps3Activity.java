package com.example.foodigo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodigo1.databinding.ActivityMaps3Binding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener {

    //
    private GoogleMap mMap;
    private ActivityMaps3Binding binding;
    Location currentLocation;
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
/**************************CONNECTION AU SERVICE DE LOCATION*********************************/
private LocationService mLocationService;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mLocationService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

/*******************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        Fragment fragment = new Map_Fragment();
       // LatLng maspFrance= new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
       // getCurrentLocation();
       // fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragCarte, fragment).commit();
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);



        // Initialize the Places client
       // String apiKey = getString(R.string.google_maps_key);
        //Places.initialize(getApplicationContext(), apiKey);
        //mPlacesClient = Places.createClient(this);
       // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
    public void findLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(Maps3Activity.this, Locale.getDefault());
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
        ActivityCompat.requestPermissions(Maps3Activity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        },REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                findLocation();
            }
            else{
                Toast.makeText(Maps3Activity.this,"Permission Required",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /*
            private void getFusedLocationProviderClient(){
                if(ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        !=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                    return;
                }
                Task<Location> task=fusedLocationProviderClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            currentLocation=location;
                            Toast.makeText(getApplicationContext(),(int) currentLocation.getLatitude(),Toast.LENGTH_LONG).show();
                            SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            assert supportMapFragment!=null;
                            supportMapFragment.getMapAsync((OnMapReadyCallback) Maps3Activity.this);

                        }
                    }
                });
            }

         */
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (REQUEST_CODE){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
                break;
        }

    }
/*
    public Location getCurrentLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Toast.makeText(getApplicationContext(), "the location result is " + locationResult, Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), "the current location is null ", Toast.LENGTH_LONG).show();

                    return;
                }
                for (Location  location: locationResult.getLocations()) {
                    if (location != null) {
                        Toast.makeText(getApplicationContext(), "the current location is " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        //TODO: user interface updates.
                    }
                }
            }
        } ;

        return currentLocation;
    }
    /*
 */
    @Override
    public void onClick(View view) {
        /*Intent i=new Intent(this,LocationService.class);
        LocationService locationService= new LocationService();
        locationService.onCreate();

         */
        TextView v= findViewById(R.id.textviewR);
        //startService(i);
        switch (view.getId()) {
            case R.id.arrow_left_black:
            case R.id.grid_ico_black:
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                findLocation();
                double[] cooord =mLocationService.getCurrentLocation();
               // String lat= locationService.getLatitude();
                //String longing=locationService.getLongitude();
                //locationService.getEcouteurGPS();
                //v.setText("La location est "+lat +" "+longing);
                System.out.println("La location est "+latitude +" "+longitude);
                System.out.println("Avec oIA La location est "+cooord[0] +" "+cooord[1]);
                break;
            case R.id.ananasImage:
               /* System.out.println("La location est");
                String lat= locationService.getLatitude();
                String longing=locationService.getLongitude();
                v.setText("La location est "+lat +" "+longing);
                System.out.println("La location est "+lat +" "+longing);
               // pickCurrentPlace();
                break;

                */
            default:
                break;
        }




    }





}