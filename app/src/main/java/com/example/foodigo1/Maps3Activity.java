package com.example.foodigo1;

import static android.content.ContentValues.TAG;

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

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, LocationSource.OnLocationChangedListener{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //Officiel Maps activity
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    ManageFoodiesCaptured manager;
    DistanceTask distanceAsyncTask;
    public final int TAKE_PICTURE_DISTANCE=1;

    /**************LOC SERV2*****************/

    /********************************************/
    private LocationService mLocationService;


    private boolean mBound = false;
    private LatLng bitmap;
    private double latitude, longitude;
    Polyline line;//pour tracer une ligne
    /***********************SERVICE DE LOCALISATION*********************/
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
       // Intent intent = new Intent(this, LocationService.class);
       // bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //double[] location = mLocationService.getCurrentLocation();
        //startService(intent);
        // Vérifier si le service est lié
        if (mBound) {
            // Appeler la méthode getCurrentLocation() sur l'instance du service
            double[] location = mLocationService.getCurrentLocation();
            latitude=mLocationService.getLatitude();
            longitude=mLocationService.getLongitude();
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
        startService(intent);

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
    protected void onResume() {
        super.onResume();
       // mLocationService.getCurrentLocation();
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
            case R.id.ananasImage:
                if(manager.isCaptured("ananas")){

                    montrerLepopUpForFoodie("Ananas","Bravo ! Vous avez déjà capturé l'ananas \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("Ananas"," Ananas n'est pas encore capturé \uD83D\uDE43");

                }
                break;
            case R.id.avocatImage:
                if(manager.isCaptured("avocat")){

                    montrerLepopUpForFoodie("Avocat","Bravo ! Vous avez déjà capturé l'avocat \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("Avocat"," Avocat n'est pas encore capturé \uD83D\uDE43");

                }
                break;

            case R.id.bananeImage:
                if(manager.isCaptured("banane")){

                    montrerLepopUpForFoodie("Banane","Bravo ! Vous avez déjà capturé la banane \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("Banane"," Banane n'est pas encore capturé \uD83D\uDE43");

                }
                break;
            case R.id.pastequeImage:
                if(manager.isCaptured("pasteque")){

                    montrerLepopUpForFoodie("Pasteque","Bravo ! Vous avez déjà capturé Pasteque \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("Pasteque"," Pasteque n'est pas encore capturé \uD83D\uDE43");

                }
                break;

            case R.id.mangueImage:
                if(manager.isCaptured("mangue")){

                    montrerLepopUpForFoodie("Mangue","Bravo ! Vous avez déjà capturé la mangue \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("Mangue"," Manque n'est pas encore capturé \uD83D\uDE43");

                }
                break;

            case R.id.pommesImage:
                if(manager.isCaptured("pommes")){

                    montrerLepopUpForFoodie("Pommes","Bravo ! Vous avez déjà capturé les Pommes \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("Pommes"," Les pommes n'ont pas été capturé \uD83D\uDE43");

                }
                break;

            default:
                break;

        }
        if (i !=null) startActivity(i);
    }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Intent i= getIntent();
        mLocationService.getCurrentLocation();
        Log.e(TAG,"On Map READYLa longitude est "+mLocationService.getLocation().getLongitude() +"et la latitude " +mLocationService.getLocation().getLatitude());
       // double latitude=i.getExtras().getDouble("latitude");
        //double longitude=i.getExtras().getDouble("longitude");
       /* double[] lesCoordonnees=mLocationService.getCurrentLocation();
        double latitude=mLocationService.getLatitude();
        double longitude=mLocationService.getLongitude();
        */
        latitude=mLocationService.getLocation().getLatitude();
        longitude=mLocationService.getLocation().getLongitude();
        System.out.println("*************MAPS3 : La latitude est: " +latitude +  " La longitude est " +longitude);
        LatLng mapUser= new LatLng(mLocationService.getLocation().getLatitude(),mLocationService.getLocation().getLongitude());
        //LatLng position = new LatLng(mLocationService.getLocation().getLatitude(),mLocationService.getLocation().getLongitude());
        /*double[] tableauDesLongitudes=i.getExtras().getDoubleArray("tableaudDesLongitudes");
        double[] tableauDesLatitude=i.getExtras().getDoubleArray("tableauDesLatitudes");
        double[] tableauDesDistances=i.getExtras().getDoubleArray("tableaudesDistances");
        putFoodiesOnMap2(mMap,tableauDesLatitude,tableauDesLongitudes,tableauDesDistances);
         */

        //Put all the foodies on the map
        try {
            putFoodiesOnMap(mMap,mapUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapUser));
        // On affiche une carte zoomé sur le lieu ou se trouve l'utilisateur
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapUser, 19.0f));
       //Le systeme de Zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(mapUser).title("Vous êtes ici"));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint({"PotentialBehaviorOverride", "SuspiciousIndentation"})
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                bitmap = marker.getPosition();
                mLocationService.getCurrentLocation();
                Location location=mLocationService.getLocation();
                //onLocationChanged(location);
                LatLng userPos= new LatLng(mLocationService.getLocation().getLatitude(),mLocationService.getLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(userPos).title("Vous êtes ici"));
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

                    if(distance<TAKE_PICTURE_DISTANCE)
                        startCameraActivity(userPos,bitmap,marker.getTitle(),(int)distance);

                    marker.setSnippet("est à "+ (int)distance +"m");
                    marker.showInfoWindow();
                }
                else{
                    Toast.makeText(Maps3Activity.this,"Impossible de récupérer votre position actuelle",Toast.LENGTH_LONG);

                }


                return true;

            }
        });

    }

   public void putFoodiesOnMap2(GoogleMap map, double[] tableaudesLatitudes, double[] tableauDesLongitudes, double[] tableauDesDistances) {
       int k = 0; //indice des tables de coordonnees
       for (String foodie : manager.getListOfFoodies()) {
           if (!manager.isCaptured(foodie)) {
               int foodie_Points = manager.getPointOfFoodie(foodie);
               //On crée une position libre sur une route ou autres
               LatLng foodiePosition = new LatLng(tableaudesLatitudes[k], tableauDesLongitudes[k]);
               //transforme l'image et on le positionne
               BitmapDrawable bt;
               bt = (BitmapDrawable) getDrawable(manager.getIdOfDrawableFoodie(foodie));
               Bitmap btt = Bitmap.createScaledBitmap(bt.getBitmap(), 195, 195, false);
               mMap.addMarker(new MarkerOptions().position(foodiePosition)
                       .title(foodie + " est à " + tableauDesDistances[k] + "m").snippet("Points : " + foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
           }
       }
   }
               //
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
                bt= (BitmapDrawable) manager.getDrawableFoodie(foodie);
                //bt= (BitmapDrawable) getDrawable(manager.getDrawableFoodie(foodie));
                Bitmap btt= Bitmap.createScaledBitmap(bt.getBitmap(),195,195,false);

                map.addMarker(new MarkerOptions().position(foodiePosition)
                        .title(foodie ).snippet(" est à " +fooddistance +"m \nPoints : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));

                mMap.addMarker(new MarkerOptions().position(foodiePosition)
                        .title(foodie +"\n" +foodie_Points).snippet("Points : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
                //


            }
            i=i+1;

        }

    }
    //public void pupFoodOnMaps(LatLng userPosition, )
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
        builder.setTitle("Distance inférieure à 2 mètre")
                .setMessage("Vous êtes à moins de 2 mètre du Foodie")
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
    public void montrerLepopUpForFoodie(String foodiname, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(foodiname)
                .setMessage(description)
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
        //On change la position du bitmap de l'utilisteur
        System.out.println("*********MAP3:La localisation a changé dans ");
        mLocationService.getCurrentLocation();
        location=mLocationService.getLocation();
        LatLng point= new LatLng(location.getLatitude(),location.getLongitude());
        new DistanceTask(this, bitmap).execute(point);
        //if(distance<TAKE_PICTURE_DISTANCE)
        //              startCameraActivity(userPos,bitmap,marker.getTitle(),(int)distance);


    }

    //Elle se déclanche quand on est à moins de 1 m
    // la fonction fait un nouvel intent sur l'activité de l'appareil photo
    //On crée l'intent de l'appareil photo dans la fonction
    // dans l'activité on lui passe en paramétre le nom du foodie
    //on démarre l'ativité photo
    //on met en extrat la position du foodie et la position de l'utilisateur
    //En tout on a 5 valeurs
    public void startCameraActivity(LatLng userPosition, LatLng foodiePosition, String foodieName, int distance ){
      if (distance<=1){
          Intent iCamera = new Intent(this, CameraActvity2.class);
           //User positions
          double userLatitude=userPosition.latitude;
          double userLongitude=userPosition.longitude;
          //Foodie positions

          double foodieLatitude=foodiePosition.latitude;
          double foodieLongitude=foodiePosition.longitude;
          //Send positions and foodie name

          iCamera.putExtra("userLatitude",userLatitude);
          iCamera.putExtra("userLongitude",userLongitude);

          iCamera.putExtra("foodieLatitude",foodieLatitude);
          iCamera.putExtra("foodieLongitude",foodieLongitude);

          iCamera.putExtra("foodieName", foodieName);
          startActivity(iCamera);
          Log.e(TAG,"Démarrage de l'activité Photo");

      }
      else
      {
          Toast.makeText(Maps3Activity.this,"Vous n'êtes pas à moins de 1 métres",Toast.LENGTH_LONG);
          Log.e(TAG,"Vous n'êtes pas à moins de 1m");

      }



    }






}
