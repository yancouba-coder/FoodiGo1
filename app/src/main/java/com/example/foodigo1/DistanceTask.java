package com.example.foodigo1;

import static android.content.ContentValues.TAG;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class DistanceTask extends AsyncTask<LatLng, Void, Double> {

    private static final String TAG = "DistanceTask";
    private static final double THRESHOLD = 2; // en mètres
    private static final int MESSAGE_SHOW_POPUP = 1;
    private double distance = 2;

    private Maps3Activity activity;
    private LatLng bitmap;
    private Looper looper;
    private android.os.Handler handler;

    public DistanceTask(Maps3Activity activity, LatLng bitmap) {
        this.activity = activity;
        this.bitmap = bitmap;
        this.looper = Looper.myLooper();

        this.handler = new android.os.Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_SHOW_POPUP) {
                    activity.montrerLepopUp();
                }
            }
        };
    }

    @Override
    protected Double doInBackground(LatLng... params) {
        LatLng point = params[0];
        this.distance = SphericalUtil.computeDistanceBetween(point, bitmap);
        /*while (distance > THRESHOLD && !isCancelled()) {
            distance = SphericalUtil.computeDistanceBetween(point, bitmap);
            Log.d(TAG, "La Distance est entrain de changer " + distance);
        }

         */

        Log.d(TAG, "Distance: " + distance);
        //activity.notifyNewDistance(distance);
        return distance;
    }

    @Override
    protected void onPostExecute(Double distance) {
        if (distance < THRESHOLD) {
            this.distance = distance;
            //Message message = handler.obtainMessage(MESSAGE_SHOW_POPUP);
           // handler.sendMessage(message);
            activity.montrerLepopUp();

        }else{

            this.distance = distance;
            activity.notifyNewDistance(distance);

        }
    }

    public double getDistance() {
        return distance;
    }

}
