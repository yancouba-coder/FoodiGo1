package com.example.foodigo1;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.foodigo1.databinding.ActivityMaps3Binding;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Locale;
import androidx.core.app.ActivityCompat;

public class Map_Fragment extends Fragment {
    private GoogleMap mMap;


    /**************************CONNECTION AU SERVICE DE LOCATION*********************************/
    private LocationService mLocationService;
    private boolean mBound = false;
    private static final int REQUEST_CODE=123;

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
    public void onStart() {
        super.onStart();
        // Lier le service à l'activité
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Délier le service de l'activité
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Délier le service de l'activité
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    /*************************************FIN de Connection******************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_map_,container,false);
        SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Si les permissions de localisation ne sont pas accordées, demandez-les
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE);
                    System.out.println("****************************Demande d'autorisation");
                }
                if (!mBound) {
                    System.out.println("****************************Initialisation du service");
                    // Lier le service à l'activité
                    Intent intent = new Intent(getActivity(), LocationService.class);
                    getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                }
                if (mBound && mLocationService != null) {
                    //On accéde à la position de l'utilisateur
                    double[] sescoordonnes = mLocationService.getCurrentLocation();
                    double latitude = mLocationService.getLatitude();
                    double longitude = mLocationService.getLatitude();
                    System.out.println("**************************La latitude dans le fragment est " + latitude + " " + longitude);

                    LatLng mapUser = new LatLng(latitude, longitude);


                    mMap.addMarker(new MarkerOptions().position(mapUser).title("Marker in France"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mapUser));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapUser, 12.0f));
                    //On zoom sur la ville de l'utilisateur
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(latLng.latitude + " KG " + latLng.longitude);
                            googleMap.clear();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                            googleMap.addMarker(markerOptions);

                        }
                    });

                }
            }



        });

        return view;
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
                    Toast.makeText(getContext(),(int) currentLocation.getLatitude(),Toast.LENGTH_LONG).show();
                    SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment!=null;
                    supportMapFragment.getMapAsync(Maps3Activity.this);

            }
        }
        });
    }

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

    public Location getCurrentLocation() {
        return currentLocation;
    }

     */
}