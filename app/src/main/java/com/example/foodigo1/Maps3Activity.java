package com.example.foodigo1;

import static android.content.ContentValues.TAG;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.pm.PackageManager;
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

import com.example.foodigo1.augmentedimage.AugmentedImageActivity;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, android.location.LocationListener, LocationService.OnLocationChangedListener{
    //private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE =298090 ;
    //Officiel Maps activity
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    ManageFoodiesCaptured manager;
    DistanceTask distanceAsyncTask;
    public final int TAKE_PICTURE_DISTANCE=2;

    private Maps3Activity act;
    private String foodieClicked;
    private Double distanceToFoodie;

    public Marker getRedUserMarker() {
        return redUserMarker;
    }

    private Marker redUserMarker;
    private HashMap<String,LatLng> foodies_positions;


    /**************LOC SERV2*****************/

    /********************************************/
    private LocationService mLocationService;
    /*private static Maps3Activity instance;
    private Maps3Activity(){} //empty constructor
    *//*
        Cette classe est uun singleton istancée par getInstnce() par le reste de l'application
         *//*
    public static Maps3Activity getInstance() {
        if (instance == null) {
            instance = new Maps3Activity();
        }
        return instance;
    }
*/
    private boolean mBound = false;
    private LatLng foodiePositionOnMap;
    private double latitude, longitude;
    Polyline line;//pour tracer une ligne
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //askPermission();
        act = this;
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
    /***********************SERVICE DE LOCALISATION*********************/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;


            mLocationService = binder.getService();

            mLocationService.setOnLocationChangedListener(act);

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
      //askPermission();
/*
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        double[] location = mLocationService.getCurrentLocation();
        startService(intent);

 */
        // Vérifier si le service est lié

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //askPermission();
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
    protected void onResume() {
        super.onResume();
        askPermission();


        Log.e(TAG,"Actvitivté on résume");
       // mLocationService.getCurrentLocation();
        if (mBound) {
            // Appeler la méthode getCurrentLocation() sur l'instance du service
            double[] location = mLocationService.getCurrentLocation();
            latitude=mLocationService.getLocation().getLatitude();
            longitude=mLocationService.getLocation().getLongitude();
            if (location != null) {
                // Utiliser la latitude et la longitude ici
            }
        }


    }

    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()){

            case R.id.menu:
                 i = new Intent(this, MenuActivity.class);
                 break;
            case R.id.photo_ico_black:
                 i = new Intent(this, AugmentedImageActivity.class);
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
    public void askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                montrerLepopUpForFoodie("Autorisation","Cette apllication a besoin d'accéder à votre position ");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                Log.e(TAG,"Permission non accordées");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

                // LOCATION_PERMISSION_REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } /*else {
           // mLocationService.getCurrentLocation();
            // Permission has already been granted
        }
        */


    }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        distanceToFoodie = 1000.;
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Intent i= getIntent();
        Log.e(TAG,"La location est :"+mLocationService.getCurrentLocation());

        //Log.e(TAG,"On Map READYLa longitude est "+mLocationService.getLocation().getLongitude() +"et la latitude " +mLocationService.getLocation().getLatitude());
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
        mMap.addMarker(new MarkerOptions().position(mapUser).title("Vous êtes ici"));

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
                foodieClicked = marker.getTitle();
                foodiePositionOnMap = marker.getPosition();
                mLocationService.getCurrentLocation();
                Location location=mLocationService.getLocation();
                //onLocationChanged(location);
                LatLng userPos= new LatLng(mLocationService.getLocation().getLatitude(),mLocationService.getLocation().getLongitude());


                if(line!=null)
                eraseLine();
                drawLine(foodiePositionOnMap,userPos);

                if(location!=null){
                    LatLng point=new LatLng(location.getLatitude(),location.getLongitude());

                    /*if(distanceAsyncTask !=null){
                        distanceAsyncTask.cancel(true);
                    }*/
                    //distanceAsyncTask = new DistanceTask(Maps3Activity.this,bitmap);
                    //distanceAsyncTask.execute(point);
                    distanceToFoodie = SphericalUtil.computeDistanceBetween(point, foodiePositionOnMap);
                    //distanceToFoodie = distanceAsyncTask.getDistance();
                    Log.e("************* distance : ", String.valueOf(distanceToFoodie));
/*
                    if(distance<TAKE_PICTURE_DISTANCE)
                        startCameraActivity(userPos,bitmap,marker.getTitle(),(int)distance);
                    else{

 */
                    marker.setTitle(foodieClicked);
                        marker.setSnippet("est à "+ distanceToFoodie.intValue() +"m");
                        marker.showInfoWindow();
                    //}


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
        foodies_positions=new HashMap<>();


        int i=0;//indice angle de positionnement

            int minimumDistance= 1;

        for (String foodie:manager.getListOfFoodies()) {
            // Si il est pas capturé on le positionne
            if(!manager.isCaptured(foodie)){
                int foodie_Points= manager.getPointOfFoodie(foodie);
                double foodiePoids= foodie_Points/100;

                double fooddistance= foodiePoids*minimumDistance;

                LatLng foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);
                foodies_positions.put(foodie,foodiePosition);
                Log.e(TAG,"Le hashmap contien" +foodies_positions.get(foodie));
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
    public void callAugmentedImageActivity(String foodieName){

        Intent appPhoto = new Intent(this, AugmentedImageActivity.class);
Log.d("foodies_positions", foodies_positions.toString());
        Log.d("foodies_positions.get(foodieName)", foodies_positions.get(foodieName).toString());

        LatLng positionFoodie = foodies_positions.get(foodieName);
        LatLng userPos= new LatLng(mLocationService.getLocation().getLatitude(),mLocationService.getLocation().getLongitude());

        double userLatitude=userPos.latitude;
        double userLongitude=userPos.longitude;
        //Foodie positions

        double foodieLatitude=positionFoodie.latitude;
        double foodieLongitude=positionFoodie.longitude;
        //Send positions and foodie name

        appPhoto.putExtra("userLatitude",userLatitude);
        appPhoto.putExtra("userLongitude",userLongitude);

        appPhoto.putExtra("foodieLatitude",foodieLatitude);
        appPhoto.putExtra("foodieLongitude",foodieLongitude);

        appPhoto.putExtra("foodieName", foodieName);
        startActivity(appPhoto);
        Log.e(TAG,"Démarrage de l'activité Photo");

        this.finish();



    }

    public void montrerLepopUp() {
        String foodieName = foodieClicked;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Distance inférieure à " + TAKE_PICTURE_DISTANCE +" mètre")
                .setMessage("Vous êtes à moins de " + TAKE_PICTURE_DISTANCE + " mètre de " + foodieName)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Prendre une photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callAugmentedImageActivity(foodieName);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    Boolean takeAPhoto = false;
    public void montrerLepopUp(String foodieName) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Distance inférieure à " + TAKE_PICTURE_DISTANCE +" mètre")
                .setMessage("Vous êtes à moins de " + TAKE_PICTURE_DISTANCE+ " mètre de " + foodieName)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Prendre une photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callAugmentedImageActivity(foodieName);
                        Log.e(TAG, " ********************************** L'activité va finish");
                        takeAPhoto = true;
                        act.finish();

                    }

                });
        Log.e(TAG, " ********************************** Tentative de dialogue");

        AlertDialog dialog = builder.create();
        if(!takeAPhoto) dialog.show();

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

        //TODO : on parcours la liste des foodies_positions et si un des foodie a une distance inférieure à TAKE_PICTURE_DISTANCE metres alors on fairt la suite
        for (Map.Entry foodie_position : foodies_positions.entrySet()){
            distanceToFoodie = SphericalUtil.computeDistanceBetween(point, (LatLng) foodie_position.getValue());
            Log.d("distanceToFoodie : ", "On se trouve à " + distanceToFoodie + " mètres de " + foodie_position.getKey());
            if (distanceToFoodie < TAKE_PICTURE_DISTANCE) montrerLepopUp((String) foodie_position.getKey());
        }
        //new DistanceTask(this, bitmap).execute(point);

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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, LocationService.class);
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    //mLocationService.getLocation();

                } else {
                    montrerLepopUp();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }


    public void notifyNewDistance(Double distance) {
        Log.d("notifyNewDistance : ", distance.toString());
         this.distanceToFoodie = distance;
    }


}
