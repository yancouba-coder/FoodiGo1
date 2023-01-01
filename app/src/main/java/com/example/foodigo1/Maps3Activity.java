package com.example.foodigo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback , android.location.LocationListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //Officiel Maps activity
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    ManageFoodiesCaptured manager;
    private DistanceTask distanceAsyncTask;
    /********************************************/
    private LocationService mLocationService;
    private boolean mBound = false;
    private LatLng bitmap;
    Polyline line;//pour tracer une ligne

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


        /*double[] tableauDesLongitudes=i.getExtras().getDoubleArray("tableaudDesLongitudes");
        double[] tableauDesLatitude=i.getExtras().getDoubleArray("tableauDesLatitudes");
        double[] tableauDesDistances=i.getExtras().getDoubleArray("tableaudesDistances");



        putFoodiesOnMap2(mMap,tableauDesLatitude,tableauDesLongitudes,tableauDesDistances);

         */

        //Put all the foodies on the map
        try {
            putFoodiesOnMap(mMap,position);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapFrance));
        // On affiche une carte zoomé sur le lieu ou se trouve l'utilisateur
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapFrance, 19.0f));
       //Le systeme de Zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
/*
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

          //  }
        //});
        //Intent intent = new Intent(this, LocationServiceForMaps3.class);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint({"PotentialBehaviorOverride", "SuspiciousIndentation"})
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                //startService(intent);
                //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
               // boolean isproche=false;

                bitmap = marker.getPosition();
                mLocationService.getCurrentLocation();
                Location location=mLocationService.getLocation();
                onLocationChanged(location);
                LatLng userPos= new LatLng(mLocationService.getLatitude(),mLocationService.getLongitude());

                if(line!=null)
                eraseLine();
                drawLine(bitmap,userPos);

                if(location!=null){
                    LatLng point=new LatLng(location.getLatitude(),location.getLongitude());
                    if(distanceAsyncTask !=null){
                        distanceAsyncTask.cancel(true);
                    }
                    distanceAsyncTask= new DistanceTask(Maps3Activity.this,bitmap);
                    distanceAsyncTask.execute(point);
                     double distance= distanceAsyncTask.getDistance();



                   /* @SuppressLint("ServiceCast")
                    LocationServiceForMaps3 locationService = (LocationServiceForMaps3) getSystemService(LOCATION_SERVICE);

//                   Définit l'objet bitmap dans la classe LocationService
                    locationService.setBitmap(bitmap);

                    */


                }
                else{
                    Toast.makeText(Maps3Activity.this,"Impossible de récupérer votre position actuelle",Toast.LENGTH_LONG);

                }


                return true;
            }
        });

    }

   public void putFoodiesOnMap2(GoogleMap map, double[] tableaudesLatitudes, double[] tableauDesLongitudes, double[] tableauDesDistances){
      int k=0; //indice des tables de coordonnees
       for (String foodie:manager.getListOfFoodies()){
           if(!manager.isCaptured(foodie)){
               int foodie_Points= manager.getPointOfFoodie(foodie);
               //On crée une position libre sur une route ou autres
               LatLng foodiePosition= new LatLng(tableaudesLatitudes[k],tableauDesLongitudes[k]);
               //transforme l'image et on le positionne
               BitmapDrawable bt;
               bt= (BitmapDrawable) getDrawable(manager.getDrawableFoodie(foodie));
               Bitmap btt= Bitmap.createScaledBitmap(bt.getBitmap(),195,195,false);
               mMap.addMarker(new MarkerOptions().position(foodiePosition)
                       .title(foodie +" est à " +tableauDesDistances[k] +"m").snippet("Points : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
               //


           }
           k=k+1;

       }

   }
    //Une fonction qui permet de positionner tout les foodies sur la carte
    // C'est un void
    //
    public void putFoodiesOnMap(GoogleMap map, LatLng userPostion) throws IOException {
        //On parcour la liste des foodies
        // si il est pas capturé
        // On le positionne sur la carte
        //
        int [] angleDepositionnement = {90, 270, 360, 180, 315, 45};
        int i=0;//indice angle de positionnement
        int minimumDistance= 2;
        for (String foodie:manager.getListOfFoodies()) {
            // Si il n'est pas capturé on le positionne
            if(!manager.isCaptured(foodie)){
                int foodie_Points= manager.getPointOfFoodie(foodie);
                double foodiePoids= foodie_Points/100;

                double fooddistance= foodiePoids*minimumDistance;

                LatLng foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);
                /*while(isBatiment(foodiePosition)){
                    System.out.println("***************************$$$$$$$$$$$$$$$$L'emplacement "+ foodie+  " est Un batiment ");
                    //On cherche une place libre
                   //fooddistance=fooddistance+1;
                    angleDepositionnement[i]=angleDepositionnement[i] +30;
                    foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);

                }

                 */


                //
                //Attention
                //On doit positionner les foodies sur des routes
                BitmapDrawable bt;
                bt= (BitmapDrawable) getDrawable(manager.getDrawableFoodie(foodie));
                Bitmap btt= Bitmap.createScaledBitmap(bt.getBitmap(),195,195,false);
                map.addMarker(new MarkerOptions().position(foodiePosition)
                        .title(foodie +" est à " +fooddistance +"m").snippet("Points : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
                //


            }
            i=i+1;

        }


    }

    public void drawLine(LatLng pointA, LatLng pointB){
        List<LatLng> points = Arrays.asList(pointA, pointB);
        line = mMap.addPolyline(new PolylineOptions().addAll(points).color(Color.GREEN));
    }

    public void eraseLine() {
        line.remove();
    }
    public boolean isRoad(LatLng foodiePosition) throws IOException {
        boolean isRoad=false;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(foodiePosition.latitude, foodiePosition.longitude, 1);
        if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            // Vérifiez si l'adresse est une route en utilisant la méthode getMaxAddressLineIndex
            if (address.getMaxAddressLineIndex() > 0) {
                isRoad=true;
                //Toast.makeText(context, "Adresse correspondant à une route", Toast.LENGTH_SHORT).show();
            }
        }
      return  isRoad;
    }
    //Vérifie si les locatisations sont pas des batiments
    //Retenir juste les adresses qui sont à maximum 30 m
    //Dans ces adresses retenir ce qui font un angle bien déterminé
    public boolean isBatiment(LatLng userPosition) throws IOException {
        boolean isBat=true;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(userPosition.latitude, userPosition.longitude, 1);
        if (!addresses.isEmpty()) {
            for (Address address : addresses) {
                if (address.getMaxAddressLineIndex() == 0) {
                    // L'emplacement n'est pas un bâtiment
                    isBat = false;
                }
            }
        }
        return isBat;
    }

    public void montrerLepopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Distance inférieure à 1 mètre")
                .setMessage("Vous êtes à moins de 1 mètre du Foodie")
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Log.e(TAG, "onLocationChanged: " + location);
        System.out.println("*********MAP3:La localisation a changé dans ");
        mLocationService.getCurrentLocation();
        location=mLocationService.getLocation();
        LatLng point= new LatLng(location.getLatitude(),location.getLongitude());
        new DistanceTask(this, bitmap).execute(point);

    }

}