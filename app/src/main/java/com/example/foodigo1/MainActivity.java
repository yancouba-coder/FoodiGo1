package com.example.foodigo1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int LOCATION_PERMISSION_REQUEST_CODE =298090 ;
    ManageFoodiesCaptured manager;






    /********************************************************************************************
     ************************************** mainActivity() **************************************
     ********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        manager = ManageFoodiesCaptured.getInstance(this);
        //manager.initPreferences(); //Initialise les variables à false si elles n'existent pas déjà
        manager.reInitPreferences();
        //TODO : a effacer dans la version de deploiement, c'est pour le test
        //manager.writeToPreferences("avocat", true);
        //manager.writeToPreferences("mangue", true);
        // FIN DU TODO

        manager.updatePoints(this);
        SharedPreferences sharedPref = this.getSharedPreferences(String.valueOf(R.string.nameOfDidacticiel), Context.MODE_PRIVATE);
        Boolean didacAlreadyShow = sharedPref.getBoolean("didac",false);
        if (!didacAlreadyShow) startActivity(new Intent(this,DidicaticielActivity.class));
    }


    @Override
    public void onClick(View view) {
        /*
        //Pour test : marque tout les foodies comme capturé et permet de réinitialisé le jeu
        List<String> foodieNames = manager.getListOfFoodies();

        for (String foodie : foodieNames){
            manager.writeToPreferences(foodie, true);
        }
        */
        switch (view.getId()) {
            case (R.id.play):
               // mLocationService.getCurrentLocation();

                Intent i = null;
              //  System.out.println("*******************La localisation est" + mLocationService.getCurrentLocation());
                if (manager.gameIsComplete()) {
                    i = new Intent(this, GameCompleteActivity.class);
                } else {
                    i = new Intent(this, GalleryFoodiesActivity.class);

                }
                if (i != null) {
                    startActivity(i);
                }
                break;


            case (R.id.home):

                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;

            //Affichage du menu
            case (R.id.menu):
                Intent menu = new Intent(this, MenuActivity.class);
                menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;
            default:
                return;

        }
    }


    /********************************************************************************************
     *********************************** SERVICE LOCALISATION ***********************************
     ********************************************************************************************/

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
       /* if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        */
    }

    /********************************************************************************************
     ************************************* SERVICE BOUSSOLE *************************************
     ********************************************************************************************/

    private static final String TAG = "MainActivity";

    private CompassService compassService;
    private boolean serviceBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CompassService.CompassBinder binder = (CompassService.CompassBinder) service;
            compassService = binder.getService();
            serviceBound = true;
            Log.d("onServiceConnected", String.valueOf(serviceBound));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            Log.d("onServiceDisconnected", String.valueOf(serviceBound));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity onResume: ");

        // Afficher l'orientation du téléphone dans la console à chaque mise à jour
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

                // LOCATION_PERMISSION_REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    11);
        } else {
            Log.d(TAG, "Permission CAMERA : granted " );
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    11);
        } else {
            Log.d(TAG, "Permission CAMERA : granted " );
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }

    }


    /* *******************************************************************************************
     ***************** Commun au SEERVICE BOUSSOLE et au SERVICE LOCALISATION *******************
     ********************************************************************************************/

    @Override
    protected void onStart() {
        System.out.println("MainActivity onStart: ");

        /* ** SERVICE BOUSSOLE **/
        Intent intent = new Intent(this, CompassService.class);
        // conection au service boussole
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        /* * SERVICE LOCALISATION **/
        super.onStart();
        // Vérifier si le service est lié
       /* if (mBound) {
            // Appeler la méthode getCurrentLocation() sur l'instance du service
            double[] location = mLocationService.getCurrentLocation();
            if (location != null) {
                // Utiliser la latitude et la longitude ici
            }
        }

        */
    }

    @Override
    protected void onStop() {

        System.out.println("MainActivity onStop: ");
        super.onStop();

        /* * SERVICE BOUSSOLE **/
        if (serviceBound) {
            //deconnection du service boussole
            unbindService(serviceConnection);
            serviceBound = false;
        }

        /* * SERVICE LOCALISATION **/
        // Délier le service de l'activité
       /* if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        */
    }



    /****************************************/


    public void montrerLepopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission")
                .setMessage("La permission doit être accordée !")
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
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // Intent intent = new Intent(this, LocationService.class);
                    //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


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




}