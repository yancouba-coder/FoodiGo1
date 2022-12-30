package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
         manager = ManageFoodiesCaptured.getInstance(this);
        manager.initPreferences(); //Initialise les variables à false si elles n'existent pas déjà
        //TODO : a effacer dans la version de deploiement, c'est pour le test
        manager.writeToPreferences("avocat", true);
        manager.writeToPreferences("mangue", true);
        // FIN DU TODO

        manager.updatePoints(this);
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

                if(manager.gameIsComplete()){
                    i = new Intent(this, GameCompleteActivity.class);
                } else{
                    i = new Intent(this, Maps3Activity.class);
                    i.putExtra("latitude",latitude);
                    i.putExtra("longitude",longitude);
                }
                if(i!=null){startActivity(i);}
                break;
            case(R.id.home):

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
}