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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Arrays;

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
    public static int TAKE_PICTURE_DISTANCE=1;
    //private  MarkerOptions userMarker;
    private int maxDistance, minDistance;
    private boolean isPopUpAppear=false;

    private Maps3Activity act;
    private String foodieClicked;
    private Double distanceToFoodie;
    private Circle circle;
    Boolean takeAPhoto = false;


    private Marker userMarker;
    private HashMap<String,LatLng> foodies_positions;


    /* *************LOC SERV2*****************/

    /* *******************************************/
    private LocationService mLocationService;
    /*private static Maps3Activity instance;
    private Maps3Activity(){} //empty constructor
    *//*
        Cette classe est uun singleton istanc??e par getInstnce() par le reste de l'application
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
        // Lier le service ?? l'activit??
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
        minDistance=manager.getDistance("MinimumDistance");
        maxDistance=manager.getDistance("MaximumDistance");
        TAKE_PICTURE_DISTANCE=minDistance;


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
        // D??lier le service de l'activit??
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
        // V??rifier si le service est li??

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //askPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // D??lier le service de l'activit??
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


        Log.e(TAG,"Actvitivt?? on r??sume");
       // mLocationService.getCurrentLocation();
        if (mBound) {
            // Appeler la m??thode getCurrentLocation() sur l'instance du service
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
            /*case R.id.photo_ico_black:
                 i = new Intent(this, AugmentedImageActivity.class);
                 break;*/
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

                    montrerLepopUpForFoodie("ananas","Bravo ! Vous avez d??j?? captur?? l'ananas \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("ananas"," Ananas n'est pas encore captur?? \uD83D\uDE43");

                }
                break;
            case R.id.avocatImage:
                if(manager.isCaptured("avocat")){

                    montrerLepopUpForFoodie("avocat","Bravo ! Vous avez d??j?? captur?? l'avocat \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("avocat"," Avocat n'est pas encore captur?? \uD83D\uDE43");

                }
                break;

            case R.id.bananeImage:
                if(manager.isCaptured("banane")){

                    montrerLepopUpForFoodie("banane","Bravo ! Vous avez d??j?? captur?? la banane \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("banane"," Banane n'est pas encore captur?? \uD83D\uDE43");

                }
                break;
            case R.id.pastequeImage:
                if(manager.isCaptured("pasteque")){

                    montrerLepopUpForFoodie("pasteque","Bravo ! Vous avez d??j?? captur?? Pasteque \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("pasteque"," Pasteque n'est pas encore captur?? \uD83D\uDE43");

                }
                break;

            case R.id.mangueImage:
                if(manager.isCaptured("mangue")){

                    montrerLepopUpForFoodie("mangue","Bravo ! Vous avez d??j?? captur?? la mangue \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("mangue"," Manque n'est pas encore captur?? \uD83D\uDE43");

                }
                break;

            case R.id.pommesImage:
                if(manager.isCaptured("pommes")){

                    montrerLepopUpForFoodie("pommes","Bravo ! Vous avez d??j?? captur?? les Pommes \uD83D\uDE0A");

                }
                else{
                    montrerLepopUpForFoodie("pommes"," Les pommes n'ont pas ??t?? captur?? \uD83D\uDE43");

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
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                montrerLepopUpForFoodie("Autorisation","Cette apllication a besoin d'acc??der ?? votre position ");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                Log.e(TAG,"Permission non accord??es");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

                // LOCATION_PERMISSION_REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }// else {
           // mLocationService.getCurrentLocation();
            // Permission has already been granted
       // }



    }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        distanceToFoodie = 1000.;
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Intent i= getIntent();
        Log.e(TAG,"La location est :"+mLocationService.getCurrentLocation());


        latitude=mLocationService.getLocation().getLatitude();
        longitude=mLocationService.getLocation().getLongitude();
        System.out.println("*************MAPS3 : La latitude est: " +latitude +  " La longitude est " +longitude);
        LatLng mapUser= new LatLng(mLocationService.getLocation().getLatitude(),mLocationService.getLocation().getLongitude());

        //Put all the foodies on the map
        try {
            putFoodiesOnMap(mMap,mapUser);
        } catch (IOException e) {
            e.printStackTrace();

        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapUser));
        // On affiche une carte zoom?? sur le lieu ou se trouve l'utilisateur
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapUser, 19.0f));
       //Le systeme de Zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        addAndRemoveUserMarker(mapUser);
        drawCercle(mapUser);
        //On regarde si il y'a d'embler un Foodie dans le cerle de capture
        computeDistanceForEachFoodie(mapUser);





        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint({"PotentialBehaviorOverride", "SuspiciousIndentation"})
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                //Pour ne pas consid??rer la position du User comme un Foodie
                if (marker.getTag() != "userFirstPositionMarker") {

                foodieClicked = marker.getTitle();
                foodiePositionOnMap = marker.getPosition();
                mLocationService.getCurrentLocation();
                Location location = mLocationService.getLocation();
                //onLocationChanged(location);
                //Set the marker of user
                LatLng userPos = new LatLng(mLocationService.getLocation().getLatitude(), mLocationService.getLocation().getLongitude());
                addAndRemoveUserMarker(userPos);

                if (line != null)
                    eraseLine();
                drawLine(foodiePositionOnMap, userPos);
                if(circle!=null)
                    circle.remove();
                drawCercle(userPos);

                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

                    /*if(distanceAsyncTask !=null){
                        distanceAsyncTask.cancel(true);
                    }*/
                    //distanceAsyncTask = new DistanceTask(Maps3Activity.this,bitmap);
                    //distanceAsyncTask.execute(point);
                    distanceToFoodie = SphericalUtil.computeDistanceBetween(point, foodiePositionOnMap);
                    //distanceToFoodie = distanceAsyncTask.getDistance();
                    Log.e("************* distance : ", String.valueOf(distanceToFoodie));
                   // distanceAsyncTask= new DistanceTask(Maps3Activity.this, point, foodies_positions);
                   //distanceAsyncTask.execute(point);

                   if(distanceToFoodie<TAKE_PICTURE_DISTANCE)
                       montrerLepopUp(foodieClicked);
                    //else{


                    marker.setTitle(foodieClicked);
                    marker.setSnippet("est ?? " + distanceToFoodie.intValue() + "m");
                    marker.showInfoWindow();
                    //}


                } else {
                    Toast.makeText(Maps3Activity.this, "Impossible de r??cup??rer votre position actuelle", Toast.LENGTH_LONG);

                }
            }


                return true;

            }
        });

    }
    public void computeDistanceForEachFoodie(LatLng userPosition){
        if(foodies_positions!=null){
            for (Map.Entry foodie_position : foodies_positions.entrySet()){
                distanceToFoodie = SphericalUtil.computeDistanceBetween(userPosition, (LatLng) foodie_position.getValue());
                Log.d("distanceToFoodie : ", "On se trouve ?? " + distanceToFoodie + " m??tres de " + foodie_position.getKey());
                if (distanceToFoodie < TAKE_PICTURE_DISTANCE)
                    montrerLepopUp((String) foodie_position.getKey());
            }

        }
        else{
            Log.e(TAG,"Le Hashmap foodie_positions est null");
        }

    }


   public void putFoodiesOnMap2(GoogleMap map, double[] tableaudesLatitudes, double[] tableauDesLongitudes, double[] tableauDesDistances) {
       int k = 0; //indice des tables de coordonnees
       for (String foodie : manager.getListOfFoodies()) {
           if (!manager.isCaptured(foodie)) {
               int foodie_Points = manager.getPointOfFoodie(foodie);
               //On cr??e une position libre sur une route ou autres
               LatLng foodiePosition = new LatLng(tableaudesLatitudes[k], tableauDesLongitudes[k]);
               //transforme l'image et on le positionne
               BitmapDrawable bt;
               bt = (BitmapDrawable) getDrawable(manager.getIdOfDrawableFoodie(foodie));
               Bitmap btt = Bitmap.createScaledBitmap(bt.getBitmap(), 195, 195, false);
               mMap.addMarker(new MarkerOptions().position(foodiePosition)
                       .title(foodie + " est ?? " + tableauDesDistances[k] + "m").snippet("Points : " + foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
           }
       }
   }
               //
    //Une fonction qui permet de positionner tout les foodies sur la carte
    // C'est un void
    //
    public void putFoodiesOnMap(GoogleMap map, LatLng userPostion) throws IOException {
        //On parcour la liste des foodies
        // si il est pas captur??
        // On le positionne sur la carte
        //
        int [] angleDepositionnement = {90, 270, 360, 180, 315, 45};
        foodies_positions=new HashMap<>();
        int i=0;//indice angle de positionnement
        for (String foodie:manager.getListOfFoodies()) {
            // Si il est pas captur?? on le positionne
            if(!manager.isCaptured(foodie)){
                int foodie_Points= manager.getPointOfFoodie(foodie);
                double foodiePoids= foodie_Points/100 ;

                double fooddistance= foodiePoids*minDistance;
                //On respecte le choix de l'utilisteur
                if(fooddistance>maxDistance)
                    fooddistance=maxDistance;

                LatLng foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);
             /* *Si on ne veut pas placer les foodies sur des batiments**/
               /* while (isBatiment(foodiePosition)){
                    fooddistance=fooddistance+1;
                    foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);
                    if(isBatiment(foodiePosition))
                        angleDepositionnement[i]=angleDepositionnement[i]+15;
                    foodiePosition = SphericalUtil.computeOffsetOrigin(userPostion,fooddistance, angleDepositionnement[i]);

                }

                */
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
                        .title(foodie ).snippet(" est ?? " +fooddistance +"m \nPoints : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));

                mMap.addMarker(new MarkerOptions().position(foodiePosition)
                        .title(foodie +"\n" +foodie_Points).snippet("Points : " +foodie_Points).icon(BitmapDescriptorFactory.fromBitmap(btt)));
                //


            }
            i=i+1;

        }

    }
    public void addAndRemoveUserMarker( LatLng userPoistion){
        if(this.userMarker!=null){
            this.userMarker.remove();
        }
        this.userMarker=mMap.addMarker( new MarkerOptions().position(userPoistion).title("Vous ??tes ici"));
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
            // V??rifiez si l'adresse est une route en utilisant la m??thode getMaxAddressLineIndex
            if (address.getMaxAddressLineIndex() > 0) {
                isRoad=true;
                //Toast.makeText(context, "Adresse correspondant ?? une route", Toast.LENGTH_SHORT).show();
            }
        }
      return  isRoad;
    }
    //V??rifie si les locatisations sont pas des batiments
    //Retenir juste les adresses qui sont ?? maximum 30 m
    //Dans ces adresses retenir ce qui font un angle bien d??termin??
    public boolean isBatiment(LatLng userPosition) throws IOException {
        boolean isBat=true;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(userPosition.latitude, userPosition.longitude, 1);
        if (!addresses.isEmpty()) {
            for (Address address : addresses) {
                if (address.getMaxAddressLineIndex() == 0) {
                    // L'emplacement n'est pas un b??timent
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
        Log.e(TAG,"D??marrage de l'activit?? Photo ");

        this.finish();



    }

    public void montrerLepopUp() {
        if(!isPopUpAppear) {

            isPopUpAppear = true;

            String foodieName = foodieClicked;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Distance inf??rieure ?? " + TAKE_PICTURE_DISTANCE + " m??tre")
                    .setMessage("Vous ??tes ?? moins de " + TAKE_PICTURE_DISTANCE + " m??tre de " + foodieName)
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            
                            dialog.dismiss();
                            isPopUpAppear = false;
                        }
                    })
                    .setPositiveButton("Prendre une photo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isPopUpAppear = false;
                            callAugmentedImageActivity(foodieName);
                            takeAPhoto = true;
                            act.finish();

                        }
                    });
            AlertDialog dialog = builder.create();
            if(!takeAPhoto) dialog.show();

        }

/*
// R??cup??rez l'objet Button correspondant au bouton "Annuler"
        Button cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
// D??finissez la couleur du texte du bouton ?? noir
        cancelButton.setTextColor(Color.BLACK);

// R??cup??rez l'objet Button correspondant au bouton "OK"
        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
// D??finissez la couleur du texte du bouton ?? noir
        okButton.setTextColor(Color.BLACK);

        dialog.show();

 */

    }

    public void montrerLepopUp(String foodieName) {
        if (!isPopUpAppear) {
            isPopUpAppear = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle("Distance inf??rieure ?? " + TAKE_PICTURE_DISTANCE + " m??tre")
                    .setMessage("Vous ??tes ?? moins de " + TAKE_PICTURE_DISTANCE + " m??tre de " + foodieName)
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isPopUpAppear = false;
                        }
                    })
                    .setPositiveButton("Prendre une photo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isPopUpAppear = false;
                            callAugmentedImageActivity(foodieName);
                            Log.e(TAG, " ********************************** L'activit?? va finish");
                            takeAPhoto = true;
                            act.finish();

                        }

                    }).create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                }
            });

            Log.e(TAG, " ********************************** Tentative de dialogue");

            //AlertDialog dialog = builder.create();
            //Button buttonAnnuler = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            //buttonAnnuler.
            //  buttonAnnuler.setBackgroundColor(Color.BLACK);
            //buttonAnnuler.setTextColor(Color.WHITE);

            //Button buttonOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            // buttonOK.setBackgroundColor(Color.BLACK);
            // buttonOK.setTextColor(Color.WHITE);
            if(!takeAPhoto) dialog.show();

        }
        //isPopUpAppear = false;
    }
    public void montrerLepopUpForFoodie(String foodieName, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(foodieName).setMessage(description);
        String message ;
        if (manager.isCaptured(foodieName)) {
            message = "Voir la photo";
        }else{
            message = "OK";
        }
        AlertDialog dialog=builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (message !="OK"){ //cas voir la photo
                            callPhotoActivity(foodieName);
                            act.finish();
                        }

                    }
                }).create();



        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });

       // AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callPhotoActivity(String foodieName) {

            act.finish();
            startActivity(new Intent(Maps3Activity.this,GalleryPhotoActivity.class).putExtra("foodieName",foodieName));


    }

    public void drawCercle(LatLng userPosition){
        CircleOptions circleOptions = new CircleOptions()
                .center(userPosition)
                .radius(TAKE_PICTURE_DISTANCE)
                .strokeColor(Color.RED)
                .strokeWidth(10);
        this.circle= mMap.addCircle(circleOptions);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Log.e(TAG, "onLocationChanged: " + location);
        //On change la position du bitmap de l'utilisteur
        System.out.println("*********MAP3:La localisation a chang?? dans ");
        mLocationService.getCurrentLocation();

        location=mLocationService.getLocation();
        LatLng point= new LatLng(location.getLatitude(),location.getLongitude());
        //on dessine un nouveau cercle
        addAndRemoveUserMarker(point);

        circle.remove();
        drawCercle(point);

       // distanceAsyncTask= new DistanceTask(this, point, foodies_positions);
       // distanceAsyncTask.execute(point);

        //TODO : on parcours la liste des foodies_positions et si un des foodie a une distance inf??rieure ?? TAKE_PICTURE_DISTANCE metres alors on fait la suite
        computeDistanceForEachFoodie(point);


    }

    //Elle se d??clanche quand on est ?? moins de 1 m
    // la fonction fait un nouvel intent sur l'activit?? de l'appareil photo
    //On cr??e l'intent de l'appareil photo dans la fonction
    // dans l'activit?? on lui passe en param??tre le nom du foodie
    //on d??marre l'ativit?? photo
    //on met en extrat la position du foodie et la position de l'utilisateur
    //En tout on a 5 valeurs
    /*
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
          Log.e(TAG,"D??marrage de l'activit?? Photo");

      }
      else
      {
          Toast.makeText(Maps3Activity.this,"Vous n'??tes pas ?? moins de 1 m??tres",Toast.LENGTH_LONG);
          Log.e(TAG,"Vous n'??tes pas ?? moins de 1m");

      }



    }

     */
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
