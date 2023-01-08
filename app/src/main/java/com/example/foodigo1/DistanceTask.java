package com.example.foodigo1;


import static com.example.foodigo1.Maps3Activity.TAKE_PICTURE_DISTANCE;

import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.Map;

public class DistanceTask extends AsyncTask<LatLng, Void, Double> {

    private static final String TAG = "DistanceTask";
    private static final double THRESHOLD = TAKE_PICTURE_DISTANCE; // en mètres

    private static final int MESSAGE_SHOW_POPUP = 1;
    private double distance = 2;

    private Maps3Activity activity;
    private LatLng bitmap;
    private Looper looper;
   private android.os.Handler handler;
    private HashMap<String, LatLng> latLngMap;
    private LatLng userPosition;
    private String foodiname;

    public DistanceTask(Maps3Activity activity, LatLng point, HashMap<String, LatLng> _latLngMap) {
        this.activity = activity;
        //this.bitmap = bitmap;
       //Looper.prepare();
        //this.looper = Looper.myLooper();
        this.userPosition=point;
        this.latLngMap=_latLngMap;

       this.handler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_SHOW_POPUP) {
                    activity.montrerLepopUp();
                }
            }
        };
       // Looper.loop();




    }
/*
    @Override
    protected Double doInBackground(LatLng... params) {
        LatLng point = params[0];
        this.distance = SphericalUtil.computeDistanceBetween(point, bitmap);
        Log.d(TAG, "Distance: " + distance);
        //activity.notifyNewDistance(distance);
        return distance;
    }

 */
@Override
protected Double doInBackground(LatLng... params) {
   // LatLng point = params[0];
    double minDistance = Double.MAX_VALUE;


    // Parcourez chaque entrée du Hashmap
    for (Map.Entry<String, LatLng> entry : latLngMap.entrySet()) {
        LatLng latLng = entry.getValue();
        double distance = SphericalUtil.computeDistanceBetween(userPosition, latLng);
        Log.d(TAG, "Distance: " + distance);
        foodiname=entry.getKey();
        if(distance<THRESHOLD)
            activity.montrerLepopUp(foodiname);

        if (distance < minDistance) {
            minDistance = distance;
            foodiname=entry.getKey();
        }
    }

    Log.d(TAG, "MinDistance: " + minDistance);
    return minDistance;
}


    @Override
    protected void onPostExecute(Double distance) {
        if (distance < THRESHOLD) {
            this.distance = distance;
            //Message message = handler.obtainMessage(MESSAGE_SHOW_POPUP);
           // handler.sendMessage(message);
            activity.montrerLepopUp(foodiname);

        }else{

            this.distance = distance;
            activity.notifyNewDistance(distance);

        }
    }

    public double getDistance() {
        return distance;
    }

}
