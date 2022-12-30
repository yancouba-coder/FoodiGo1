package com.example.foodigo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //Officiel Maps activity
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    ManageFoodiesCaptured manager;
    /********************************************/
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
    public void onDestroy() {
        super.onDestroy();
        // Délier le service de l'activité
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Vérifier si le service est lié
        if (mBound) {
            // Appeler la méthode getCurrentLocation() sur l'instance du service
            double[] location = mLocationService.getCurrentLocation();
            if (location != null) {
                // Utiliser la latitude et la longitude ici
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Délier le service de l'activité
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /****************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        // Lier le service à l'activité
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Intent i= getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Fragment fragment= new Map_Fragment();
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragCarte,fragment).commit();
         manager= ManageFoodiesCaptured.getInstance(this);
        manager.displayCapturedFoodieForMapsActivity(this);
        manager.updatePoints(this);
    }


    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()){

            case R.id.menu:
                 i = new Intent(this, MenuActivity.class);
                 break;
            case R.id.photo_ico_black:
                 i = new Intent(this, PhotoActivity.class);
                 break;
            case R.id.arrow_left_black:
            case R.id.grid_ico_black:
                System.out.println("*******************La localisation est" +mLocationService.getCurrentLocation()[0]);;
                i = new Intent(this, GalleryFoodiesActivity.class);
                break;
            case R.id.home:
                i = new Intent(this, MainActivity.class);
                break;
            default:
                break;

        }
        if (i !=null) startActivity(i);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Intent i= getIntent();
        double latitude=i.getExtras().getDouble("latitude");
        double longitude=i.getExtras().getDouble("longitude");
        System.out.println("*************MAPS3 : La latitude est: " +latitude +  " La longitude est " +longitude);
        LatLng mapFrance= new LatLng(latitude,longitude);

        mMap.addMarker(new MarkerOptions().position(mapFrance).title("Vous êtes ici"));
        LatLng position = new LatLng(latitude,longitude);

        // Calculer la position initiale à 100 mètres à l'ouest (270 degrés) de latLng
        LatLng anaPosition = SphericalUtil.computeOffsetOrigin(position, 100, 270);

        //Put all the foodies on the map
        putFoodiesOnMap(mMap,position);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapFrance));
        // On affiche une carte zoomé sur le lieu ou se trouve l'utilisateur
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapFrance, 19.0f));
       //Le systeme de Zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                /*
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " KG " + latLng.longitude);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                googleMap.addMarker(markerOptions);
                /
                 */

            }
        });

    }

    //Une fonction qui permet de positionner tout les foodies sur la carte
    // C'est un void
    //
    public void putFoodiesOnMap(GoogleMap map, LatLng userPostion){
        //On parcour la liste des foodies
        // si il est pas capturé
        // On le positionne sur la carte
        //
        int [] angleDepositionnement = {90, 270, 360, 180, 315, 45};
        int i=0;
        int minimumDistance= 10 ;
        for (String foodie:manager.getListOfFoodies()) {
            // Si il est pas capturé on le positionne
            if(!manager.isCaptured(foodie)){
                int foodie_Points= manager.getPointOfFoodie(foodie);
                double foodiePoids= foodie_Points/100;

                double fooddistance= foodiePoids*minimumDistance;

                LatLng foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);
                //
                //Attention
                //On doit positionner les foodies sur des routes
                BitmapDrawable bt;
                bt= (BitmapDrawable) getDrawable(manager.getDrawableFoodie(foodie));
                Bitmap btt= Bitmap.createScaledBitmap(bt.getBitmap(),195,195,false);
                mMap.addMarker(new MarkerOptions().position(foodiePosition)
                        .title(foodie +" est à " +fooddistance +"m").snippet("Points : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
                //


            }
            i=i+1;

        }




    }
}