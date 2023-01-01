package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_COARSE_LOCATION_PERMISSION = 1;
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
        manager.initPreferences(); //Initialise les variables à false si elles n'existent pas déjà
        //TODO : a effacer dans la version de deploiement, c'est pour le test
        manager.writeToPreferences("avocat", true);
        manager.writeToPreferences("mangue", true);
        // FIN DU TODO
        CompassService compassService = new CompassService();
        manager.updatePoints(this);
        System.out.println("direction, N ? " + compassService.getDirection(44.800150341642635, -0.58277131782318, 44.79744010405991, -0.5826854871344793));
        System.out.println("direction, SW ? " + compassService.getDirection(44.79932814818754, -0.5550050900285008, 44.80240370068184, -0.5520439312683263));

        /*// Vérifiez si l'autorisation d'accéder aux capteurs a déjà été accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demandez l'autorisation de l'utilisateur s'il n'a pas encore été accordée
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        // Vérifiez si l'autorisation d'accéder aux capteurs a déjà été accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demandez l'autorisation de l'utilisateur s'il n'a pas encore été accordée
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 2);
        }*/
        // Vérifiez si l'autorisation d'accéder aux capteurs a déjà été accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.HIGH_SAMPLING_RATE_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            // Demandez l'autorisation de l'utilisateur s'il n'a pas encore été accordée
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.HIGH_SAMPLING_RATE_SENSORS}, 3);
        }

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
                Intent i = null;

                System.out.println("*******************La localisation est" +mLocationService.getCurrentLocation());
                double latitude= mLocationService.getLatitude();
                double longitude=mLocationService.getLongitude();

                

                if (manager.gameIsComplete()) {

                    i = new Intent(this, GameCompleteActivity.class);
                } else {
                    i = new Intent(this, Maps3Activity.class);
                    i.putExtra("latitude",latitude);
                    i.putExtra("longitude",longitude);
                }
                if (i != null) {
                    startActivity(i);
                }
                break;
            case (R.id.home):


                //System.out.println("Orientation: " + compassService.getOrientation());
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;

            //Affichage du menu
            case (R.id.menu):
                Intent menu = new Intent(this, MenuActivity.class);
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
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
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
        if (serviceBound) {
            //String orientation = compassService.getOrientation();
            //System.out.println("Orientation: " + orientation);
            //Log.d(TAG, "Orientation: " + orientation);
        }
    }


    /********************************************************************************************
     ***************** Commun au SEERVICE BOUSSOLE et au SERVICE LOCALISATION *******************
     ********************************************************************************************/

    @Override
    protected void onStart() {
        System.out.println("MainActivity onStart: ");

        /*** SERVICE BOUSSOLE **/
        Intent intent = new Intent(this, CompassService.class);
        // conection au service boussole
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        /** SERVICE LOCALISATION **/
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

        System.out.println("MainActivity onStop: ");
        super.onStop();

        /** SERVICE BOUSSOLE **/
        if (serviceBound) {
            //deconnection du service boussole
            unbindService(serviceConnection);
            serviceBound = false;
        }

        /** SERVICE LOCALISATION **/
        // Délier le service de l'activité
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    /*
    * Notes pour le zoom avec Yancouba:
    *
    *       QUESTIONS
    *   - depuis la mainActivity, le bouton jouer doit amener sur la galerie foodie, c'est
    *     le bouton jouoer de la galerieFoodie qui amene sur la maps donc il faut déplacer
    *     le code dans galerieFoodie. De plus le menu lui aussi peu ouvrir la carte. On doit
    *     donc rajouter ca dessus.
    *   - En testant le code, ma position ne change pas lorsque je me déplace, donc je n'ai pas la
    *     notif comme quoi je suis proche du foodie.
    *   - Dans le manager, quelle classe on doiot garder pour le getDrawable ?
    *
    *       INFORMATIONS
    *   - INFO : j'ai du factoriser nos 2 codes a/n du onStart() et du onStop(), c'est ok ?
    *   - INFO : j'ai du refaire la manip pour forcer la synchronisation des dépendances car
    *     le SphericalUtil ne fonctionnait plus.
    *   - INFO : j'ai fait comme toi dans les notes, j'ai mis une note pour l'utilisation de la boussole
    * */
}