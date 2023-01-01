package com.example.foodigo1;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class DistanceTask extends AsyncTask<LatLng, Void, Double> {

    private static final String TAG = "DistanceTask";
    private static final double THRESHOLD = 1; // en mètres
    private static final int MESSAGE_SHOW_POPUP = 1;

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
        double distance = SphericalUtil.computeDistanceBetween(point, bitmap);
        Log.d(TAG, "Distance: " + distance);
        return distance;
    }

    @Override
    protected void onPostExecute(Double distance) {
        if (distance < THRESHOLD) {
            Message message = handler.obtainMessage(MESSAGE_SHOW_POPUP);
            handler.sendMessage(message);
        }
    }
}
