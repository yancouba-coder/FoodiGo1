package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //Officiel Maps activity
    private boolean mLocationPermissionGranted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        Fragment fragment= new Map_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragCarte,fragment).commit();
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(this);
        manager.displayCapturedFoodieForMapsActivity(this);
        manager.updatePoints(this);
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
                i = new Intent(this, GalleryFoodiesActivity.class);
                break;
            case R.id.home:
                i = new Intent(this, MainActivity.class);
                break;
            default:
                break;

        }
        if (i !=null) startActivity(i);
    }


}