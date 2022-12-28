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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.arrow_left_black:
            case R.id.grid_ico_black:
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
            default:
                break;
        }
    }


}