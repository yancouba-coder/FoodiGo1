package com.example.foodigo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddressFetcherTask.OnAddressFetchedListener{
    ManageFoodiesCaptured manager;

    /**********************Localisation**********************/
    private LocationService mLocationService;
    private boolean mBound = false;
    AddressFetcherTask task;
    double[] tableauDeslatitudes = new double[7];
    double[] tableauDesLongitudes=new double[7];
    double[] tableauDesDistances=new double[7];
    double latitude, longitude;
    Intent ii =null;


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
        //fetchAddresses(44.858635,-0.5581200000000001);
        //fetchAddresses(47,0.5555);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //A enlever aprés
        //fetchAddresses(44.858635,-0.5581200000000001);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.play):
                Intent i = null;
                System.out.println("*******************La localisation est" +mLocationService.getCurrentLocation());
                this.latitude= mLocationService.getLatitude();
                this.longitude=mLocationService.getLongitude();

                if(manager.gameIsComplete()){
                    i = new Intent(this, GameCompleteActivity.class);
                } else{
                    // ii est initialisé dès qu'on ouvre l'actvité à l'aide du Thread qui caclule
                    // les addresses proches et non occuppées par des batiments
                    i = new Intent(this, Maps3Activity.class);
                    i.putExtra("latitude",latitude);
                    i.putExtra("longitude",longitude);
                    //ii.putExtra("latitude",latitude);
                    //ii.putExtra("longitude",longitude);
                    //On démarre le Thread pour récuperer les positions non occupées,ou on peut placer les Foodies
                    //fetchAddresses(latitude,longitude);
                    //startActivity(ii);
                    //System.out.println("*******************La Task est:  " +task.getListeAddressWithDistance());


                }

                if(i!=null){startActivity(i);}break;
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
    private void fetchAddresses(double latitude, double longitude) {
       task = new AddressFetcherTask((Context) this, (AddressFetcherTask.OnAddressFetchedListener) MainActivity.this);
        task.execute(latitude, longitude);

    }
    @Override
    public void onAddressFetched(List<AddressHelper.AddressWithDistance> addresses) {

        ii=new Intent(this, Maps3Activity.class);
        int j=0;
        System.out.println("***************Le nombre d'adresse est " +addresses.size());
        for (AddressHelper.AddressWithDistance address : addresses) {
            tableauDeslatitudes[j]= address.address.getLatitude();
            tableauDesLongitudes[j]=address.address.getLongitude();
            tableauDesDistances[j]=address.distance;
            j=j+1;
            StringBuilder addressBuilder = new StringBuilder();
            for (int i = 0; i <= address.address.getMaxAddressLineIndex(); i++) {
                addressBuilder.append(address.address.getAddressLine(i)).append("\n");
            }

           // ii.putExtra("")
            ii.putExtra("tableauDesLatitudes",tableauDeslatitudes);
           ii.putExtra("tableaudDesLongitudes", tableauDesLongitudes);
            ii.putExtra("tableaudesDistances",tableauDesDistances);
            System.out.println("********MainActvity : "+addressBuilder.toString() + " (distance : " + address.distance + " mètres)");
        }
        System.out.println("=================Le tableau des latitudes est " + tableauDeslatitudes[4]);
        System.out.println("=================Le tableau des longitudes est " + tableauDesLongitudes[4]);
        System.out.println("=================Le tableau des distances est " + tableauDesDistances[4]);




    }



}